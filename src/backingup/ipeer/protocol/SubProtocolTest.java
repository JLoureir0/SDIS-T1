package backingup.ipeer.protocol;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.Test;

import backingup.Constants;

public class SubProtocolTest {
	@Test
	public void testChunkBackup() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 1;
		String chunkBody = "sensitive_data";
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		
		try {
		InetAddress mdbAddress = InetAddress.getByName(address);
		InetAddress mcAddress = InetAddress.getByName(address1);
		
		ChunkBackup cb = new ChunkBackup(fileID, chunkNo, replicationDegree, chunkBody, mdbPort, mdbAddress, mcPort, mcAddress);
		assertFalse(cb.backupChunk());
		
		
		Thread t = new Thread() {
			public void run() {
				String fileID = "id1";
				int chunkNo = 1;
				int replicationDegree = 1;
				String chunkBody = "sensitive_data";
				int mdbPort = 64321;
				String address = "224.2.2.5";
				int mcPort = 54321;
				String address1 = "224.2.2.3";
				String storeMessage = Constants.STORED + " " + Constants.VERSION_1 +  " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF;
				
				try {
					InetAddress mdbAddress = InetAddress.getByName(address);
					
					MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
					mdbSocket.joinGroup(mdbAddress);
					
					byte[] putchunkData = new byte[Constants.ARRAY_SIZE];
					DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length);
					
					mdbSocket.receive(putchunkPacket);
					mdbSocket.close();
					String[] putchunkMessage = new String(putchunkPacket.getData(),Constants.ENCODING).trim().split(Constants.WHITESPACE_REGEX);
					
					assertEquals(8, putchunkMessage.length);
					assertEquals(Constants.PUTCHUNK, putchunkMessage[0]);
					assertEquals(Constants.VERSION_1, putchunkMessage[1]);
					assertEquals(fileID, putchunkMessage[2]);
					assertEquals(Integer.toString(chunkNo), putchunkMessage[3]);
					assertEquals(Integer.toString(replicationDegree), putchunkMessage[4]);
					assertEquals(Constants.CRLF, putchunkMessage[5]);
					assertEquals(Constants.CRLF, putchunkMessage[6]);
					assertEquals(chunkBody, putchunkMessage[7]);
					
					InetAddress mcAddress = InetAddress.getByName(address1);
					DatagramSocket mcSocket = new DatagramSocket();
					
					byte[] storeData = storeMessage.getBytes(Constants.ENCODING);
					DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length, mcAddress, mcPort);
					
					mcSocket.send(storePacket);
					mcSocket.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		assertTrue(cb.backupChunk());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testChunkRestore() {
		String fileID = "id1";
		int chunkNo= 2;
		String chunkBody = "sensitive_data";
		String address = "224.2.2.3";
		int mcPort = 54321;
		int mdrPort = 55321;
		String address1 = "224.2.2.4";

		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			InetAddress mdrAddress = InetAddress.getByName(address1);
			
			ChunkRestore cr = new ChunkRestore(fileID, chunkNo, mcPort, mdrPort, mcAddress, mdrAddress);
			
			Thread t = new Thread() {
				public void run() {
					String fileID = "id1";
					int chunkNo = 2;
					String address = "224.2.2.3";
					int mcPort = 54321;
					int mdrPort = 55321;
					String address1 = "224.2.2.4";
					String chunkBody = "sensitive_data";
					String sendChunkMessage = Constants.CHUNK + " " + Constants.VERSION_1 + " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF + " " + chunkBody; 
					
					try {
						InetAddress mcAddress = InetAddress.getByName(address);
						InetAddress mdrAddress = InetAddress.getByName(address1);
						
						MulticastSocket mcSocket = new MulticastSocket(mcPort);
						mcSocket.joinGroup(mcAddress);
						
						byte[] getChunkData = new byte[Constants.ARRAY_SIZE];
						DatagramPacket getChunkPacket = new DatagramPacket(getChunkData, getChunkData.length);
						mcSocket.receive(getChunkPacket);
						mcSocket.close();
						String[] getChunkMessage = new String(getChunkPacket.getData(),Constants.ENCODING).trim().split(Constants.WHITESPACE_REGEX);
						
						assertEquals(6, getChunkMessage.length);
						assertEquals(Constants.GETCHUNK, getChunkMessage[0]);
						assertEquals(Constants.VERSION_1, getChunkMessage[1]);
						assertEquals(fileID, getChunkMessage[2]);
						DatagramSocket mdrSocket = new DatagramSocket();
						
						byte[] chunkData = sendChunkMessage.getBytes(Constants.ENCODING);
						DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length, mdrAddress, mdrPort);
						
						mdrSocket.send(chunkPacket);
						mdrSocket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			};
			t.start();
			Thread.sleep(10);
			String result = cr.restoreChunk();
			assertEquals(chunkBody,result);		
		} catch (ChunkNotFound cf) {
			fail();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	@Test
	public void testChunkDelete() {
		String fileId = "id1";
		int numberOfDeleteMessages = 10;
		String address = "224.2.2.3";
		int mcPort = 54321;
		
		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			
			ChunkDelete cd = new ChunkDelete(fileId, numberOfDeleteMessages, mcAddress, mcPort);
			
			Thread t = new Thread() {
				public void run() {
					String fileId = "id1";
					int numberOfDeleteMessages = 10;
					String address = "224.2.2.3";
					int mcPort = 54321;
					
					try {
						InetAddress mcAddress = InetAddress.getByName(address);
						
						MulticastSocket mcSocket = new MulticastSocket(mcPort);
						mcSocket.joinGroup(mcAddress);
					
						for(int i=0; i<numberOfDeleteMessages; i++) {
							byte[] deletechunkData = new byte[Constants.ARRAY_SIZE];
							DatagramPacket deleteChunkPacket = new DatagramPacket(deletechunkData, deletechunkData.length);
							mcSocket.receive(deleteChunkPacket);
							String [] deletechunkMessage = new String(deleteChunkPacket.getData(),Constants.ENCODING).trim().split(Constants.WHITESPACE_REGEX);
							assertEquals(4, deletechunkMessage.length);
							assertEquals(Constants.DELETE, deletechunkMessage[0]);
							assertEquals(fileId, deletechunkMessage[1]);
							assertEquals(Constants.CRLF, deletechunkMessage[2]);
							assertEquals(Constants.CRLF, deletechunkMessage[3]);
						}
						mcSocket.close();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			};
			t.start();
			assertTrue(cd.chunkDelete());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
