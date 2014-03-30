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
		String storeMessage = Constants.STORED + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF;
		Database db = new Database(100);
		
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
		String storeMessage = Constants.STORED + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF;
		Database db = new Database(100);
		
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
		String address = "224.2.2.5";
		Database db = new Database(100);
		db.addChunk(fileID, chunkNo, replicationDegree, chunkBody);
		
		try {
			InetAddress mdrAddress = InetAddress.getByName(address);
			
			ChunkRestore cr = new ChunkRestore(db, fileID, chunkNo, mdrPort, mdrAddress);
			cr.start();		
			
			MulticastSocket mdrSocket = new MulticastSocket(mdrPort);
			byte[] restoreData = new byte[Constants.ARRAY_SIZE];
			DatagramPacket restorePacket = new DatagramPacket(restoreData, restoreData.length);
			mdrSocket.joinGroup(mdrAddress);
			mdrSocket.receive(restorePacket);
			mdrSocket.close();
			
			String restoreMessage = Constants.CHUNK + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + chunkBody;
			String receivedRestore = new String(restorePacket.getData(),Constants.ENCODING).trim();
			
			assertEquals(restoreMessage, receivedRestore);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			InetAddress mdrAddress = InetAddress.getByName(address);
					
			ChunkRestore cr = new ChunkRestore(db, fileID, chunkNo, mdrPort, mdrAddress);
			cr.start();
			
			String restoreMessage = Constants.CHUNK + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + chunkBody;
			byte[] chunkData = restoreMessage.getBytes(Constants.ENCODING);
			DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length, mdrAddress, mdrPort);
			MulticastSocket mdrSocket = new MulticastSocket(mdrPort);
			Thread.sleep(10);
			mdrSocket.send(chunkPacket);
			mdrSocket.close();
			
			byte[] restoreData = new byte[Constants.ARRAY_SIZE];
			DatagramPacket restorePacket = new DatagramPacket(restoreData, restoreData.length);
			mdrSocket.joinGroup(mdrAddress);
			mdrSocket.setSoTimeout(Constants.HALF_A_SECOND);
			mdrSocket.receive(restorePacket);
			mdrSocket.close();
			fail();			
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
		Database db = new Database(100);
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
	
	@Test
	public void testFreeSpace() {
		String fileID = "id1";
		int chunkNo1 = 1, chunkNo2 = 2;
		int replicationDegree = 3;
		String chunkBody1 = "sensitive_data";
		int mcPort = 54321;
		String address = "224.2.2.3";
		
		Database db = new Database(28);
		db.addChunk(fileID, chunkNo1, replicationDegree, chunkBody1);
		db.increaseCount(fileID, chunkNo1);
		db.increaseCount(fileID, chunkNo1);
		db.increaseCount(fileID, chunkNo1);
		
		db.addChunk(fileID, chunkNo2, replicationDegree, chunkBody1);
		db.increaseCount(fileID, chunkNo2);
		db.increaseCount(fileID, chunkNo2);
		db.increaseCount(fileID, chunkNo2);
		db.increaseCount(fileID, chunkNo2);
		
		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			
			FreeSpace fs = new FreeSpace(db,mcPort,mcAddress);
			Thread t = new Thread() {
				public void run() {
					String fileID = "id1";
					int chunkNo = 2;
					String address = "224.2.2.3";
					int mcPort = 54321;
					
					try {
						InetAddress mcAddress = InetAddress.getByName(address);
						
						MulticastSocket mcSocket = new MulticastSocket(mcPort);
						mcSocket.joinGroup(mcAddress);
						
						byte[] removedData = new byte[Constants.ARRAY_SIZE];
						DatagramPacket removedPacket = new DatagramPacket(removedData, removedData.length);
						
						mcSocket.receive(removedPacket);
						mcSocket.close();
						String[] removedMessage = new String(removedPacket.getData(),Constants.ENCODING).trim().split(Constants.WHITESPACE_REGEX);
						
						assertEquals(5, removedMessage.length);
						assertEquals(Constants.REMOVED, removedMessage[0]);
						assertEquals(Constants.VERSION_1, removedMessage[1]);
						assertEquals(fileID, removedMessage[2]);
						assertEquals(Integer.toString(chunkNo), removedMessage[3]);
						assertEquals(Constants.CRLF, removedMessage[4]);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
			fs.freeSpace(15);
			assertEquals(15,db.getMaxSize());
			
			assertEquals(14, db.getSize());
			assertTrue(db.containsChunk(fileID, chunkNo1));
			
			t = new Thread() {
				public void run() {
					String fileID = "id1";
					int chunkNo = 1;
					String address = "224.2.2.3";
					int mcPort = 54321;
					
					try {
						InetAddress mcAddress = InetAddress.getByName(address);
						
						MulticastSocket mcSocket = new MulticastSocket(mcPort);
						mcSocket.joinGroup(mcAddress);
						
						byte[] removedData = new byte[Constants.ARRAY_SIZE];
						DatagramPacket removedPacket = new DatagramPacket(removedData, removedData.length);
						
						mcSocket.receive(removedPacket);
						mcSocket.close();
						String[] removedMessage = new String(removedPacket.getData(),Constants.ENCODING).trim().split(Constants.WHITESPACE_REGEX);
						
						assertEquals(5, removedMessage.length);
						assertEquals(Constants.REMOVED, removedMessage[0]);
						assertEquals(Constants.VERSION_1, removedMessage[1]);
						assertEquals(fileID, removedMessage[2]);
						assertEquals(Integer.toString(chunkNo), removedMessage[3]);
						assertEquals(Constants.CRLF, removedMessage[4]);
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
			fs.freeSpace(10);
			assertEquals(10,db.getMaxSize());
			assertEquals(0, db.getSize());
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Test
	public void testChunkRemoved() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 9;
		String chunkBody = "sensitive_data";
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		Database db = new Database(100);
		db.addChunk(fileID, chunkNo, replicationDegree, chunkBody);
		
		try {
			InetAddress mdbAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
					
			ChunkRemoved cr = new ChunkRemoved(db, fileID, chunkNo, mdbPort, mdbAddress, mcPort, mcAddress);
			cr.start();
			
			String putchunkMessage = Constants.PUTCHUNK + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + replicationDegree + " " +  Constants.CRLF + " " + chunkBody;
			byte[] chunkData = putchunkMessage.getBytes(Constants.ENCODING);
			DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length, mdbAddress, mdbPort);
			MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
			Thread.sleep(10);
			mdbSocket.send(chunkPacket);
			
			byte[] putchunkData = new byte[Constants.ARRAY_SIZE];
			DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length);
			mdbSocket.joinGroup(mdbAddress);
			mdbSocket.setSoTimeout(Constants.HALF_A_SECOND);
			mdbSocket.receive(putchunkPacket);
			mdbSocket.close();
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			InetAddress mdbAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			ChunkRemoved cr = new ChunkRemoved(db, fileID, chunkNo, mdbPort, mdbAddress, mcPort, mcAddress);
			cr.start();		
			
			MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
			byte[] putchunkData = new byte[Constants.ARRAY_SIZE];
			DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length);
			mdbSocket.joinGroup(mdbAddress);
			mdbSocket.receive(putchunkPacket);
			mdbSocket.close();
			
			String putchunkMessage = Constants.PUTCHUNK + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + replicationDegree + " " +  Constants.CRLF + " " + chunkBody;
			String receivedPutchunk = new String(putchunkPacket.getData(),Constants.ENCODING).substring(0,putchunkPacket.getLength());
			
			assertEquals(putchunkMessage, receivedPutchunk);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}