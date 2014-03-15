package peer.protocol;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.Test;

import peer.database.Database;

public class SubProtocolTest {
	private final int HALF_A_SECOND = 500;
	private final int ARRAY_SIZE = 512;
	private final String ENCODING = "US-ASCII";
	private final String STORED = "STORED";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";

	@Test
	public void testChunkBackup() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 9;
		String chunkBody = "sensitive data";
		int mcPort = 54321;
		String address = "224.2.2.3";
		Database db = new Database();
		
		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			
			ChunkBackup cb = new ChunkBackup(db, fileID, chunkNo, replicationDegree, chunkBody, mcPort, mcAddress);
			cb.start();		
			
			MulticastSocket mcSocket = new MulticastSocket(mcPort);
			byte[] receivedData = new byte[ARRAY_SIZE];
			DatagramPacket storePacket = new DatagramPacket(receivedData, receivedData.length);
			mcSocket.joinGroup(mcAddress);
			mcSocket.receive(storePacket);
			mcSocket.close();
			
			String storeMessage = STORED + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF;
			String receivedStore = new String(storePacket.getData(),ENCODING).trim();
			
			assertEquals(storeMessage, receivedStore);
			assertTrue(db.containsChunk(fileID, chunkNo));
			
			Thread.sleep(HALF_A_SECOND);
			assertEquals(1, db.getCount(fileID, chunkNo));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReplicationCounter() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 9;
		String chunkBody = "sensitive data";
		int mcPort = 54321;
		String address = "224.2.2.3";
		String storeMessage = STORED + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF;
		Database db = new Database();
		
		db.addChunk(fileID, chunkNo, replicationDegree, chunkBody);
		
		try {
			byte[] storeData = storeMessage.getBytes(ENCODING);
			InetAddress mcAddress = InetAddress.getByName(address);
			
			ReplicationCounter replicationCounter = new ReplicationCounter(db, fileID, chunkNo, mcAddress, mcPort);
			replicationCounter.start();
			
			DatagramSocket mcSocket = new DatagramSocket();
			DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length, mcAddress, mcPort);
			
			mcSocket.send(storePacket);
			mcSocket.send(storePacket);
			mcSocket.send(storePacket);
			mcSocket.send(storePacket);
			
			mcSocket.close();
			Thread.sleep(400);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(4, db.getCount(fileID, chunkNo));
	}
}