package backingup.peer.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import backingup.Constants;
import backingup.peer.database.Database;

public class ReplicationCounter extends Thread {	
	private Database database;
	private String fileID;
	private int chunkNo;
	private MulticastSocket mcSocket;
	
	public ReplicationCounter(Database database, String fileID, int chunkNo, InetAddress mcAddress, int mcPort) {
		this.database = database;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		try {
			mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long end = System.currentTimeMillis() + Constants.HALF_A_SECOND;
		while(System.currentTimeMillis() < end) {
			byte[] storeData = new byte[Constants.ARRAY_SIZE];
			try {
				DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length);
				mcSocket.receive(storePacket);
				if(correctChunk(new String(storePacket.getData(), Constants.ENCODING).trim()))
					updateCount();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mcSocket.close();
	}
	
	private void updateCount() {
		if(database.containsChunk(fileID, chunkNo))
			database.increaseCount(fileID, chunkNo);
	}
	
	private boolean correctChunk(String store) {
		try {
			String[] storeSplit = store.split(Constants.CRLF);
			String[] headerSplit = storeSplit[0].split(Constants.WHITESPACE_REGEX);
			return (headerSplit[0].equals(Constants.STORED) && headerSplit[1].equals(Constants.VERSION_1) && headerSplit[2].equals(fileID) && headerSplit[3].equals(Integer.toString(chunkNo)));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
