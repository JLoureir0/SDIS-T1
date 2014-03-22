package ipeer.protocol;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.Test;

public class SubProtocolTest {
	private final int ARRAY_SIZE = 512;
	private final String WHITESPACE_REGEX = "\\s";
	private final String ENCODING = "US-ASCII";
	private final String PUTCHUNK = "PUTCHUNK";
	private final String STORED = "STORED";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	
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
				String storeMessage = STORED + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF;
				
				try {
					InetAddress mdbAddress = InetAddress.getByName(address);
					
					MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
					mdbSocket.joinGroup(mdbAddress);
					
					byte[] putchunkData = new byte[ARRAY_SIZE];
					DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length);
					
					mdbSocket.receive(putchunkPacket);
					mdbSocket.close();
					String[] putchunkMessage = new String(putchunkPacket.getData(),ENCODING).trim().split(WHITESPACE_REGEX);
					
					assertEquals(8, putchunkMessage.length);
					assertEquals(PUTCHUNK, putchunkMessage[0]);
					assertEquals(VERSION_1, putchunkMessage[1]);
					assertEquals(fileID, putchunkMessage[2]);
					assertEquals(Integer.toString(chunkNo), putchunkMessage[3]);
					assertEquals(Integer.toString(replicationDegree), putchunkMessage[4]);
					assertEquals(CRLF, putchunkMessage[5]);
					assertEquals(CRLF, putchunkMessage[6]);
					assertEquals(chunkBody, putchunkMessage[7]);
					
					InetAddress mcAddress = InetAddress.getByName(address1);
					DatagramSocket mcSocket = new DatagramSocket();
					
					byte[] storeData = storeMessage.getBytes(ENCODING);
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
}
