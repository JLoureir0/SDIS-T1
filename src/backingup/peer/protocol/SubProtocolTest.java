package backingup.peer.protocol;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.Test;

import backingup.Constants;
import backingup.peer.database.Database;

public class SubProtocolTest {
	@Test
	public void testChunkBackup() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 9;
		String chunkBody = "sensitive_data";
		int mcPort = 54321;
		String address = "224.2.2.3";
		String storeMessage = Constants.STORED + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF;
		Database db = new Database();
		
		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			
			ChunkBackup cb = new ChunkBackup(db, fileID, chunkNo, replicationDegree, chunkBody, mcPort, mcAddress);
			cb.start();		
			
			MulticastSocket mcSocket = new MulticastSocket(mcPort);
			byte[] storeData = new byte[Constants.ARRAY_SIZE];
			DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length);
			mcSocket.joinGroup(mcAddress);
			mcSocket.receive(storePacket);
			mcSocket.close();
			
			String receivedStore = new String(storePacket.getData(),Constants.ENCODING).trim();
			
			assertEquals(storeMessage, receivedStore);
			assertTrue(db.containsChunk(fileID, chunkNo));
			
			Thread.sleep(Constants.HALF_A_SECOND);
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
		String chunkBody = "sensitive_data";
		int mcPort = 54321;
		String address = "224.2.2.3";
		String storeMessage = Constants.STORED + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF;
		Database db = new Database();
		
		db.addChunk(fileID, chunkNo, replicationDegree, chunkBody);
		
		try {
			byte[] storeData = storeMessage.getBytes(Constants.ENCODING);
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
			Thread.sleep(Constants.HALF_A_SECOND);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(4, db.getCount(fileID, chunkNo));
	}
	
	@Test
	public void testChunkRestore() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 9;
		String chunkBody = "sensitive_data";
		int mdrPort = 54321;
		String address = "224.2.2.4";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		Database db = new Database();
		db.addChunk(fileID, chunkNo, replicationDegree, chunkBody);
		
		try {
			InetAddress mdrAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			ChunkRestore cr = new ChunkRestore(db, fileID, chunkNo, mdrPort, mdrAddress,mcPort,mcAddress);
			cr.start();		
			
			MulticastSocket mdrSocket = new MulticastSocket(mdrPort);
			byte[] restoreData = new byte[Constants.ARRAY_SIZE];
			DatagramPacket restorePacket = new DatagramPacket(restoreData, restoreData.length);
			mdrSocket.joinGroup(mdrAddress);
			mdrSocket.receive(restorePacket);
			mdrSocket.close();
			
			String restoreMessage = Constants.CHUNK + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF + " " + chunkBody;
			String receivedRestore = new String(restorePacket.getData(),Constants.ENCODING).trim();
			
			assertEquals(restoreMessage, receivedRestore);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			InetAddress mdrAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			
			
			ChunkRestore cr = new ChunkRestore(db, fileID, chunkNo, mdrPort, mdrAddress,mcPort,mcAddress);
			cr.start();
			
			String restoreMessage = Constants.CHUNK + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF + " " + chunkBody;
			byte[] chunkData = restoreMessage.getBytes(Constants.ENCODING);
			DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length, mcAddress, mcPort);
			DatagramSocket mcSocket = new DatagramSocket();
			Thread.sleep(10);
			mcSocket.send(chunkPacket);
			mcSocket.close();
			
			MulticastSocket mdrSocket = new MulticastSocket(mdrPort);
			byte[] restoreData = new byte[Constants.ARRAY_SIZE];
			DatagramPacket restorePacket = new DatagramPacket(restoreData, restoreData.length);
			mdrSocket.joinGroup(mdrAddress);
			mdrSocket.setSoTimeout(Constants.HALF_A_SECOND);
			mdrSocket.receive(restorePacket);
			fail();
			mdrSocket.close();
			
			String receivedRestore = new String(restorePacket.getData(),Constants.ENCODING).trim();
			assertEquals(restoreMessage, receivedRestore);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testChunkDelete() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 9;
		String chunkBody = "sensitive_data";
		Database db = new Database();
		db.addChunk(fileID, chunkNo, replicationDegree, chunkBody);
		
		assertTrue(db.containsChunk(fileID, chunkNo));
		ChunkDelete cd = new ChunkDelete(db,fileID);
		cd.start();
		try {
			Thread.sleep(Constants.HALF_A_SECOND);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertFalse(db.containsChunk(fileID, chunkNo));
	}
}