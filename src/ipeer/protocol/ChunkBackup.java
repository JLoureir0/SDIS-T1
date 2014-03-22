package ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChunkBackup {
	private final int HALF_A_SECOND = 500;
	private final int ARRAY_SIZE = 512;
	private final String WHITESPACE_REGEX = "\\s";
	private final String ENCODING = "US-ASCII";
	private final String PUTCHUNK = "PUTCHUNK";
	private final String STORED = "STORED";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	
	private String fileID;
	private int chunkNo;
	private int replicationDegree;
	private String chunkBody;
	private int mdbPort;
	private InetAddress mdbAddress;
	@SuppressWarnings("unused")
	private int mcPort;
	@SuppressWarnings("unused")
	private InetAddress mcAddress;
	private DatagramSocket mdbSocket;
	private MulticastSocket mcSocket;
	private int replicationCounter;
	
	public ChunkBackup(String fileID, int chunkNo, int replicationDegree, String chunkBody, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.replicationDegree = replicationDegree;
		this.replicationCounter = 0;
		this.chunkBody = chunkBody;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		
		try {
			mdbSocket = new DatagramSocket();
			
			mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean backupChunk() {
		try {
			for(int iteration=0; iteration < 5; iteration++) {
				sendPutChunk();
				replicationCounter(iteration);
				if(replicationCounter >= replicationDegree) {
					mdbSocket.close();
					mcSocket.close();
					return true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private  void sendPutChunk() {
		String putchunkMessage = PUTCHUNK + " " + VERSION_1 + " " + fileID + " " + chunkNo + " " + replicationDegree + " " + CRLF + " " + CRLF + " " + chunkBody;
		try {
			byte[] putchunkData = putchunkMessage.getBytes(ENCODING);
			DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length, mdbAddress, mdbPort);
			mdbSocket.send(putchunkPacket);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void replicationCounter(int iteration) {
		byte[] storeData = new byte[ARRAY_SIZE];
		long endTime = (long) (System.currentTimeMillis() + HALF_A_SECOND*Math.pow(2, iteration));
		
		try {
			mcSocket.setSoTimeout((int) (HALF_A_SECOND*Math.pow(2, iteration)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		replicationCounter = 0;
		
		while(System.currentTimeMillis() < endTime) {
			try {
				DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length);
				mcSocket.receive(storePacket);
				if(System.currentTimeMillis() < endTime && correctChunk(new String(storePacket.getData(), ENCODING).trim())) {
					replicationCounter++;
				}
			} catch (Exception e) {
			}
		}
	}
	
	private boolean correctChunk(String store) {
		String[] storeSplit = store.split(WHITESPACE_REGEX);
		return (storeSplit[0].equals(STORED) && storeSplit[1].equals(VERSION_1) && storeSplit[2].equals(fileID) && storeSplit[3].equals(Integer.toString(chunkNo)) && storeSplit[4].equals(CRLF) && storeSplit[5].equals(CRLF));
	}
}
