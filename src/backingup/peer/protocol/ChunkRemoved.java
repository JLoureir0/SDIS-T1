package backingup.peer.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Random;

import backingup.Constants;
import backingup.ipeer.protocol.ChunkBackup;
import backingup.peer.database.Database;

public class ChunkRemoved extends Thread {	
	private Database database;
	private String fileID;
	private int chunkNo;
	private int replicationDegree;
	private String chunkBody;
	private int mdbPort;
	private InetAddress mdbAddress;
	private int mcPort;
	private InetAddress mcAddress;
	private Random random;

	public ChunkRemoved(Database database, String fileID, int chunkNo, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.database = database;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		
		random = new Random();
	}
	
	public void run() {
		if(database.containsChunk(fileID, chunkNo)) {
			replicationDegree = database.getReplicationDegree(fileID, chunkNo);
			chunkBody = database.getChunkBody(fileID, chunkNo);
			
			database.decreaseCount(fileID, chunkNo);
			if(database.getCount(fileID, chunkNo) < replicationDegree) {
				if(noResponse()) {
					ChunkBackup chunkBackup = new ChunkBackup(fileID, chunkNo, replicationDegree, chunkBody, mdbPort, mdbAddress, mcPort, mcAddress);
					chunkBackup.backupChunk();
				}
			}
		}
	}
	
	private boolean noResponse() {
		try {
			MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
			mdbSocket.joinGroup(mdbAddress);
			int timeout = random.nextInt(Constants.SLEEP);
			mdbSocket.setSoTimeout(timeout);			
			
			long endTime = System.currentTimeMillis()+timeout;
			
			while(System.currentTimeMillis() < endTime) {
				try {
					byte[] putchunkData = new byte[Constants.ARRAY_SIZE];
					DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length);
					mdbSocket.receive(putchunkPacket);
					String chunk = new String(putchunkPacket.getData(),Constants.ENCODING).substring(0, putchunkPacket.getLength());
					
					if(System.currentTimeMillis() < endTime && correctChunk(chunk)) {
						mdbSocket.close();
						return false;
					}
					
				} catch(SocketTimeoutException e) {
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			mdbSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private boolean correctChunk(String chunk) {
		try {
			String[] chunkSplit = chunk.split(Constants.CRLF);
			String[] headerSplit = chunkSplit[0].split(Constants.WHITESPACE_REGEX);
			return (headerSplit[0].equals(Constants.PUTCHUNK) && headerSplit[1].equals(Constants.VERSION_1) && headerSplit[2].equals(fileID) && headerSplit[3].equals(Integer.toString(chunkNo)) && headerSplit[4].equals(Integer.toString(replicationDegree)));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
