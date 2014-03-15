package peer.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import peer.database.Database;

public class ReplicationCounter extends Thread {
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
		long end = System.currentTimeMillis() + 500;
		while(System.currentTimeMillis() < end) {
			byte[] storeData = new byte[512];
			try {
				DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length);
				mcSocket.receive(storePacket);
				if(correctChunk(new String(storePacket.getData(), "US-ASCII").trim()))
					updateCount();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateCount() {
		database.increaseCount(fileID, chunkNo);
	}
	
	private boolean correctChunk(String store) {
		String[] storeSplit = store.split("\\s");
		return (storeSplit[2].equals(fileID) && storeSplit[3].equals(Integer.toString(chunkNo)));
	}
}
