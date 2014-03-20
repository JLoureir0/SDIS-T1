package ipeer.protocol;

import ipeer.database.Database;
import ipeer.database.File;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class ChunkBackup {
	private final int HALF_A_SECOND = 500;
	private final String ENCODING = "US-ASCII";
	private final String PUTCHUNK = "PUTCHUNK";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	
	private Database db;
	private String path;
	private String fileID;
	private int chunkNos;
	private int replicationDegree;
	private String chunkBody;
	private int mdbPort;
	private InetAddress mdbAddress;
	private int mcPort;
	private InetAddress mcAddress;
	private DatagramSocket mdbSocket;
	private int[] replicationCounter;
	
	public ChunkBackup(Database db, String fileID, String path, int chunkNos, int replicationDegree, String chunkBody, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.db = db;
		this.fileID = fileID;
		this.chunkNos = chunkNos;
		this.replicationDegree = replicationDegree;
		this.replicationCounter = new int[1];
		this.chunkBody = chunkBody;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		this.path = path;
		
		try {
			mdbSocket = new DatagramSocket();
		} catch (Exception /*SocketException*/ e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			for(int iteration=0; iteration < 5; iteration++) {
				sendPutChunk();
				replicationCounter(iteration);
				Thread.sleep((long) (HALF_A_SECOND+Math.pow(2, iteration)));
				if(replicationCounter[0] >= replicationDegree) {
					File file = new File(path,chunkNos);
					db.addFile(fileID, file);
					mdbSocket.close();
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void replicationCounter(int iteration) {
		ReplicationCounter rc = new ReplicationCounter(fileID,chunkNos,mcAddress,mcPort,replicationCounter,iteration);
		rc.start();
	}
	
	public  void sendPutChunk() {
		String putchunkMessage = PUTCHUNK + " " + VERSION_1 + " " + fileID + " " + chunkNos + " " + replicationDegree + " " + CRLF + " " +CRLF + chunkBody;
		try {
			byte[] putchunkData = putchunkMessage.getBytes(ENCODING);
			DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length, mdbAddress, mdbPort);
			mdbSocket.send(putchunkPacket);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
}
