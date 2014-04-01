package backingup.ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import backingup.Constants;

public class ChunkBackup {
	private String fileID;
	private int chunkNo;
	private int replicationDegree;
	private String chunkBody;
	private int mdbPort;
	private InetAddress mdbAddress;
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
	
	private void sendPutChunk() {
		String putchunkMessage = Constants.PUTCHUNK + " " + Constants.VERSION_1 + " " + fileID + " " + chunkNo + " " + replicationDegree + Constants.CRLF + chunkBody;
		try {
			byte[] putchunkData = putchunkMessage.getBytes(Constants.ENCODING);
			DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length, mdbAddress, mdbPort);
			mdbSocket.send(putchunkPacket);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void replicationCounter(int iteration) {
		byte[] storeData = new byte[Constants.ARRAY_SIZE];
		long endTime = (long) (System.currentTimeMillis() + Constants.HALF_A_SECOND*Math.pow(2, iteration));
		
		try {
			mcSocket.setSoTimeout((int) (Constants.HALF_A_SECOND*Math.pow(2, iteration)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		replicationCounter = 0;
		
		while(System.currentTimeMillis() < endTime) {
			try {
				DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length);
				mcSocket.receive(storePacket);
				if(System.currentTimeMillis() < endTime && correctChunk(new String(storePacket.getData(), Constants.ENCODING).trim())) {
					replicationCounter++;
				}
			} catch (Exception e) {
			}
		}
	}
	
	private boolean correctChunk(String store) {
		String[] storeSplit = store.split(Constants.CRLF);
		String[] headerSplit = storeSplit[0].split(Constants.WHITESPACE_REGEX);
		return (headerSplit[0].equals(Constants.STORED) && headerSplit[1].equals(Constants.VERSION_1) && headerSplit[2].equals(fileID) && headerSplit[3].equals(Integer.toString(chunkNo)));
	}
}
