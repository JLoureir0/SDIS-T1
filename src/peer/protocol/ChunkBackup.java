package peer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import peer.database.Database;

public class ChunkBackup extends Thread {
	private final int SLEEP = 401;
	private final String STORED = "STORED";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	private final String ENCODING = "US-ASCII";
	
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
		sendStore();
	}
	
	private void replicationCounter() {
		ReplicationCounter replicationCounter = new ReplicationCounter(database, fileID, chunkNo, mcAddress, mcPort);
		replicationCounter.start();
	}
	
	private void sendStore() {
		String storeMessage = STORED + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF;
		
		try {
			byte[] storeData = storeMessage.getBytes(ENCODING);
			DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length, mcAddress, mcPort);
			Thread.sleep(random.nextInt(SLEEP));
			mcSocket.send(storePacket);
			mcSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
