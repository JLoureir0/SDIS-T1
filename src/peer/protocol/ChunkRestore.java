package peer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import peer.database.Database;

public class ChunkRestore extends Thread {
	private final int SLEEP = 401;
	private final String CHUNK = "CHUNK";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	private final String ENCODING = "US-ASCII";
	
	private Database database;
	private String fileID;
	private int chunkNo;
	private String chunkBody;
	private DatagramSocket mdrSocket;
	private int mdrPort;
	private InetAddress mdrAddress;
	private Random random;

	public ChunkRestore(Database database, String fileID, int chunkNo, int mdrPort, InetAddress mdrAddress) {
		this.database = database;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.mdrPort = mdrPort;
		this.mdrAddress = mdrAddress;
		random = new Random();
		
		try {
			mdrSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		if(containsChunk())
			sendChunk();
	}
	
	private boolean containsChunk() {
		return database.containsChunk(fileID, chunkNo);
	}
	
	private void sendChunk() {
		chunkBody = database.getChunkBody(fileID, chunkNo);
		String storeMessage = CHUNK + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF + " " + chunkBody;
		
		try {
			byte[] storeData = storeMessage.getBytes(ENCODING);
			DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length, mdrAddress, mdrPort);
			Thread.sleep(random.nextInt(SLEEP));
			mdrSocket.send(storePacket);
			mdrSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
