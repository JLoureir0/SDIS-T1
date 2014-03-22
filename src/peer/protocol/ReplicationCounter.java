package peer.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import peer.database.Database;

public class ReplicationCounter extends Thread {
	private final int HALF_A_SECOND = 500;
	private final int ARRAY_SIZE = 512;
	private final String ENCODING = "US-ASCII";
	private final String WHITESPACE_REGEX = "\\s";
	private final String STORED = "STORED";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	
	private Database database;
	private String fileID;
	private int chunkNo;
	private MulticastSocket mcSocket;
	@SuppressWarnings("unused")
	private InetAddress mcAddress;
	@SuppressWarnings("unused")
	private int mcPort;
	
	public ReplicationCounter(Database database, String fileID, int chunkNo, InetAddress mcAddress, int mcPort) {
		this.database = database;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.mcAddress = mcAddress;
		this.mcPort = mcPort;
		try {
			mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long end = System.currentTimeMillis() + HALF_A_SECOND;
		while(System.currentTimeMillis() < end) {
			byte[] storeData = new byte[ARRAY_SIZE];
			try {
				DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length);
				mcSocket.receive(storePacket);
				if(correctChunk(new String(storePacket.getData(), ENCODING).trim()))
					updateCount();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mcSocket.close();
	}
	
	private void updateCount() {
		database.increaseCount(fileID, chunkNo);
	}
	
	private boolean correctChunk(String store) {
		String[] storeSplit = store.split(WHITESPACE_REGEX);
		return (storeSplit[0].equals(STORED) && storeSplit[1].equals(VERSION_1) && storeSplit[2].equals(fileID) && storeSplit[3].equals(Integer.toString(chunkNo)) && storeSplit[4].equals(CRLF) && storeSplit[5].equals(CRLF));
	}
}
