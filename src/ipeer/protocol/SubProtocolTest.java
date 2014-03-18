package ipeer.protocol;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import org.junit.Test;

import ipeer.database.Database;
import ipeer.protocol.ChunkBackup;

public class SubProtocolTest {
	private final int SEVENTEEN_SECONDS = 17000;
	private final int HALF_A_SECOND = 500;
	private final int ARRAY_SIZE = 512;
	private final String ENCODING = "US-ASCII";
	private final String STORED = "STORED";
	private final String PUTCHUNK = "PUTCHUNK";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	
	@SuppressWarnings("static-access")
	@Test
	public void testBackup() {
		final String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 2;
		String chunkBody = "sensitive data";
		int mdbPort = 64321;
		final String address = "224.2.2.5";
		final String putchunkMessage = PUTCHUNK + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + replicationDegree + " " + CRLF + " " + CRLF + " " + chunkBody;
		final String storeMessage = STORED + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF;
		final int mcPort = 54321;
		final String mcaddress = "224.2.2.3";
			
		Database db = new Database();	
		
		Thread t1 = new Thread() {
			public void run() {
				
				try {
					MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
					byte[] putchunkData = new byte[ARRAY_SIZE];
					
					DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length);
					mdbSocket.joinGroup(InetAddress.getByName(address));
					mdbSocket.receive(putchunkPacket);
					
					String receivedPutchunk = new String(putchunkPacket.getData(),ENCODING).trim();
					assertEquals(putchunkMessage, receivedPutchunk);
					
					for(int i=0;i<4;i++) {
						mdbSocket.receive(putchunkPacket);
					}
					
				    try {
				    	mdbSocket.setSoTimeout(SEVENTEEN_SECONDS);
				    	mdbSocket.receive(putchunkPacket);
				    	assertEquals("1","2");
				    } catch (SocketTimeoutException e) {
				    	assertEquals("1","1");
				    }
					
					mdbSocket.close();
				} catch (Exception e1) {
				}
			}
		};
		t1.start();
		
		ChunkBackup cb = null;
		try {
			cb = new ChunkBackup(db, fileID, chunkNo, replicationDegree, chunkBody, mdbPort, InetAddress.getByName(address),mcPort,InetAddress.getByName(mcaddress));
		} catch (Exception e1) {
		}
		cb.start();
		
		Thread t2 = new Thread() {
			public void run() {
				try {
					MulticastSocket mdbSocket = new MulticastSocket(mcPort);
					byte[] putchunkData = new byte[ARRAY_SIZE];
					DatagramPacket putchunkPacket = new DatagramPacket(putchunkData, putchunkData.length);
					
					DatagramSocket mcSocket = new DatagramSocket();
					byte[] storeData = storeMessage.getBytes(ENCODING);
					DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length, InetAddress.getByName(mcaddress), mcPort);
					
					mdbSocket.setSoTimeout(SEVENTEEN_SECONDS);
					mdbSocket.joinGroup(InetAddress.getByName(address));
					
					mdbSocket.receive(putchunkPacket);		
					mcSocket.send(storePacket);	
					mdbSocket.receive(putchunkPacket);
					mcSocket.send(storePacket);	
					mcSocket.send(storePacket);	
					
			    	mdbSocket.receive(putchunkPacket);
			    	assertEquals("1","2");
			    	mdbSocket.close();
			    	mcSocket.close();
				} catch (SocketTimeoutException e) {
					assertEquals("1","1");
				}
				catch (Exception e1) {
				}
			}
		};
		t2.start();

		try {
			cb = new ChunkBackup(db, fileID, chunkNo, replicationDegree, chunkBody, mdbPort, InetAddress.getByName(address),mcPort,InetAddress.getByName(mcaddress));
		} catch (Exception e1) {
		}
		cb.start();
	}
	
	@Test
	public void testRestore() {
		
	}
	
	@Test
	public void testDelete() {
		
	}

}
