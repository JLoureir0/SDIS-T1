package backingup.peer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import backingup.Constants;
import backingup.peer.database.Database;

public class ChunkBackup extends Thread {	
	private Database database;
	private String fileID;
	private int chunkNo;
	private int replicationDegree;
	private String chunkBody;
	private DatagramSocket mcSocket;
	private int mcPort;
	private InetAddress mcAddress;
	private Random random;

	public ChunkBackup(Database database, String fileID, int chunkNo, int replicationDegree, String chunkBody, int mcPort, InetAddress mcAddress) {
		this.database = database;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.replicationDegree = replicationDegree;
		this.chunkBody = chunkBody;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		random = new Random();
		
		try {
			mcSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		replicationCounter();
		database.addChunk(fileID, chunkNo, replicationDegree, chunkBody);
		if(database.containsChunk(fileID, chunkNo))
			sendStore();
	}
	
	private void replicationCounter() {
		ReplicationCounter replicationCounter = new ReplicationCounter(database, fileID, chunkNo, mcAddress, mcPort);
		replicationCounter.start();
	}
	
	private void sendStore() {
		String storeMessage = Constants.STORED + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF;
		
		try {
			byte[] storeData = storeMessage.getBytes(Constants.ENCODING);
			DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length, mcAddress, mcPort);
			Thread.sleep(random.nextInt(Constants.SLEEP));
			mcSocket.send(storePacket);
			mcSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
