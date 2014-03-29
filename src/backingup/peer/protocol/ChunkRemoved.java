package backingup.peer.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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

	public ChunkRemoved(Database database, String fileID, int chunkNo, int replicationDegree, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.database = database;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.replicationDegree = replicationDegree;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		chunkBody = database.getChunkBody(fileID, chunkNo);
		random = new Random();
	}
	
	public void run() {
		database.decreaseCount(fileID, chunkNo);
		if(database.getCount(fileID, chunkNo) < replicationDegree) {
			if(noResponse()) {
				ChunkBackup chunkBackup = new ChunkBackup(fileID, chunkNo, replicationDegree, chunkBody, mdbPort, mdbAddress, mcPort, mcAddress);
				chunkBackup.backupChunk();
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
					String chunk = new String(putchunkPacket.getData(),Constants.ENCODING).trim();
					
					if(System.currentTimeMillis() < endTime && correctChunk(chunk)) {
						mdbSocket.close();
						return false;
					}						
				} catch(Exception e) {
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
		String[] chunkSplit = chunk.split(Constants.WHITESPACE_REGEX);
		return (chunkSplit[0].equals(Constants.PUTCHUNK) && chunkSplit[1].equals(Constants.VERSION_1) && chunkSplit[2].equals(fileID) && chunkSplit[3].equals(Integer.toString(chunkNo)) && chunkSplit[4].equals(Integer.toString(replicationDegree)) && chunkSplit[5].equals(Constants.CRLF));
	}
}
