package ipeer.protocol;

import ipeer.database.Database;
import ipeer.database.File;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ChunkBackup {
	private final  String ENCODING = "US-ASCII";
	private final  String PUTCHUNK = "PUTCHUNK";
	private final  String VERSION_1 = "1.0";
	private final  String CRLF = "CRLF";
	
	private  Database db;
	private  String path;
	private  String fileId;
	private  int chunkNumber;
	private  int replicationDegree;
	private  String chunkBody;
	private  int mdbPort;
	private  InetAddress mdbAddress;
	private  int mcPort;
	private  InetAddress mcAddress;
	private  DatagramSocket mdbSocket;
	private  int replicationCounter;
	
	public ChunkBackup(Database db, String fileId, String path, int chunkNumber, int replicationDegree, String chunkBody, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.db = db;
		this.fileId = fileId;
		this.chunkNumber = chunkNumber;
		this.replicationDegree = replicationDegree;
		this.chunkBody = chunkBody;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		this.path = path;
		
		try {
			mdbSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			for(int iteration=0; iteration < 5; iteration++) {
				sendPutChunk();
				replicationCounter(iteration);
				if(replicationCounter>=replicationDegree) {
					File file = new File(path,chunkNumber);
					db.addFile(fileId, file);
					mdbSocket.close();
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void replicationCounter(int iteration) {
		ReplicationCounter rc = new ReplicationCounter(fileId,chunkNumber,mcAddress,mcPort,replicationCounter,iteration);
		rc.start();
	}
	
	public  void sendPutChunk() {
		String putchunkMessage = PUTCHUNK + " " + VERSION_1 + " " + fileId + " " + chunkNumber + " " + replicationDegree + " " + CRLF + " " +CRLF + chunkBody;
		try {
			byte[] putchunkData = putchunkMessage.getBytes(ENCODING);
			DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length, mdbAddress, mdbPort);
			mdbSocket.send(putchunkPacket);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}	
}
