package backingup.peer.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Random;

import backingup.Constants;
import backingup.peer.database.Database;

public class ChunkRestore extends Thread {	
	private Database database;
	private String fileID;
	private int chunkNo;
	private String chunkBody;
	private MulticastSocket mdrSocket;
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
			mdrSocket = new MulticastSocket(mdrPort);
		} catch (Exception e) {
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
		String chunkMessage = Constants.CHUNK + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + chunkBody;
		
		try {
			byte[] chunkData = chunkMessage.getBytes(Constants.ENCODING);
			DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length, mdrAddress, mdrPort);
			
			if(noResponse()) {
				mdrSocket.send(chunkPacket);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		mdrSocket.close();
	}
	
	private boolean noResponse() {
		try {
			mdrSocket.joinGroup(mdrAddress);
			int timeout = random.nextInt(Constants.SLEEP);
			mdrSocket.setSoTimeout(timeout);			
			
			long endTime = System.currentTimeMillis()+timeout;
			
			while(System.currentTimeMillis() < endTime) {
				try {
					byte[] chunkData = new byte[Constants.ARRAY_SIZE];
					DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length);
					mdrSocket.receive(chunkPacket);
					String chunk = new String(chunkPacket.getData(),Constants.ENCODING).trim();
					
					if(System.currentTimeMillis() < endTime && correctChunk(chunk)) {
						return false;
					}						
				}catch(SocketTimeoutException e) {
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean correctChunk(String chunk) {
		String[] chunkSplit = chunk.split(Constants.WHITESPACE_REGEX);
		return (chunkSplit[0].equals(Constants.CHUNK) && chunkSplit[1].equals(Constants.VERSION_1) && chunkSplit[2].equals(fileID) && chunkSplit[3].equals(Integer.toString(chunkNo)) && chunkSplit[4].equals(Constants.CRLF));
	}
}
