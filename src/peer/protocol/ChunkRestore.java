package peer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Random;

import peer.database.Database;

public class ChunkRestore extends Thread {
	private final int ARRAY_SIZE = 512;
	private final int SLEEP = 401;
	private final String CHUNK = "CHUNK";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	private final String ENCODING = "US-ASCII";
	private final String WHITESPACE_REGEX = "\\s";
	
	private Database database;
	private String fileID;
	private int chunkNo;
	private String chunkBody;
	private DatagramSocket mdrSocket;
	private int mdrPort;
	private InetAddress mdrAddress;
	private int mcPort;
	private InetAddress mcAddress;
	private Random random;

	public ChunkRestore(Database database, String fileID, int chunkNo, int mdrPort, InetAddress mdrAddress, int mcPort, InetAddress mcAddress) {
		this.database = database;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.mdrPort = mdrPort;
		this.mdrAddress = mdrAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
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
		String chunkMessage = CHUNK + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF + " " + chunkBody;
		
		try {
			byte[] chunkData = chunkMessage.getBytes(ENCODING);
			DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length, mdrAddress, mdrPort);
			
			if(noResponse())
				mdrSocket.send(chunkPacket);
			mdrSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean noResponse() {
		try {
			MulticastSocket mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
			int timeout = random.nextInt(SLEEP);
			mcSocket.setSoTimeout(timeout);			
			
			long endTime = System.currentTimeMillis()+timeout;
			
			while(System.currentTimeMillis() < endTime) {
				try {
					byte[] chunkData = new byte[ARRAY_SIZE];
					DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length);
					mcSocket.receive(chunkPacket);
					String chunk = new String(chunkPacket.getData(),ENCODING).trim();
					
					if(System.currentTimeMillis() < endTime && correctChunk(chunk)) {
						mcSocket.close();
						return false;
					}						
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			mcSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean correctChunk(String chunk) {
		String[] chunkSplit = chunk.split(WHITESPACE_REGEX);
		return (chunkSplit[0].equals(CHUNK) && chunkSplit[1].equals(VERSION_1) && chunkSplit[2].equals(fileID) && chunkSplit[3].equals(Integer.toString(chunkNo)) && chunkSplit[4].equals(CRLF) && chunkSplit[5].equals(CRLF) && chunkSplit[6].equals(chunkBody));
	}
}
