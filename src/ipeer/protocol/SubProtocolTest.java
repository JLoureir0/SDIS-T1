package ipeer.protocol;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Random;

import org.junit.Test;

import ipeer.database.Database;
import ipeer.protocol.ReplicationCounter;

public class SubProtocolTest {
	private final int SEVENTEEN_SECONDS = 17000;
	private final int HALF_A_SECOND = 500;
	private final String ENCODING = "US-ASCII";
	private final String STORED = "STORED";
	private final String PUTCHUNK = "PUTCHUNK";
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";

	
	@Test
	public void testReplicationCounter() {
		String fileID = "id1";
		int chunkNo = 1;
		int[] replicationCounter = {0};
		int mcPort = 54321;
		String address = "224.2.2.3";
		String storeMessage = STORED + " " + VERSION_1 +  " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF;
		
		try {
			byte[] storeData = storeMessage.getBytes(ENCODING);
			InetAddress mcAddress = InetAddress.getByName(address);
			
			DatagramSocket mcSocket = new DatagramSocket();
			
						
			for(int i=0; i<5; i++) {
				Random r = new Random();
				ReplicationCounter rc = new ReplicationCounter(fileID, chunkNo, mcAddress, mcPort, replicationCounter, i);
				rc.start();
				
				assertEquals(0, replicationCounter[0]);
				
				DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length, mcAddress, mcPort);
				
				int random = r.nextInt(5)+1;
				for(int j=0; j<random; j++)
					mcSocket.send(storePacket);
				
				Thread.sleep((long)(HALF_A_SECOND*Math.pow(2, i)));
				assertEquals(random, replicationCounter[0]);
				replicationCounter[0] = 0;
			}
			
			mcSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeleteFile() {
		final int ARRAY_SIZE = 512;
		final String fileID = "id1";
		final int numberOfDeleteMessages = 3;
		final String address = "224.2.2.3";
		final int mcPort = 54321;
		
		Thread test1 = new Thread() {
			public void run() {
				try {
					String DELETE = "DELETE " + fileID + " CRLF CRLF";
					int deleteMessagesCounter = 0;
					MulticastSocket mcSocket = new MulticastSocket(mcPort);
					mcSocket.joinGroup(InetAddress.getByName(address));
					
					byte[] receivedData = new byte[ARRAY_SIZE];
					DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
					
					mcSocket.receive(receivedPacket);
					System.out.println("HERE!");
					deleteMessagesCounter++;
					String receivedMessage = new String(receivedPacket.getData(),ENCODING).trim();
					assertEquals(receivedMessage,DELETE);
					
					for(int i=1; i<numberOfDeleteMessages; i++) {
						mcSocket.receive(receivedPacket);
						deleteMessagesCounter++;
					}
					assertEquals(deleteMessagesCounter,numberOfDeleteMessages);
					mcSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		InetAddress mcAddress = null;
		try {
			mcAddress = InetAddress.getByName(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ChunkDelete cd = new ChunkDelete(fileID, numberOfDeleteMessages, mcAddress, mcPort);
		test1.run();
		cd.start();
	}
	
	@Test
	public void testBackup() {
		final int ARRAY_SIZE = 512;
		final String fileID = "id1";
		String path = "/test.txt";
		int chunkNos = 1;
		int replicationDegree = 2;
		String chunkBody = "sensitive data";
		final int mdbPort = 64321;
		final String address = "224.2.2.5";
		final int mcPort = 54321;
		final String address1 = "224.2.2.3";
		
		final String putchunkMessage = PUTCHUNK + " " + VERSION_1 +  " " + fileID + " " + chunkNos + " " + replicationDegree + " " + CRLF + " " + CRLF + " " + chunkBody;
		final String storeMessage = STORED + " " + VERSION_1 +  " " + fileID + " " + chunkNos + " " + CRLF + " " + CRLF;
			
		final Database db = new Database();
		
		InetAddress mdbAddress = null;
		InetAddress mcAddress = null;
		try {
			mcAddress = InetAddress.getByName(address1);
			mdbAddress = InetAddress.getByName(address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		ChunkBackup cb = new ChunkBackup(db, fileID, path, chunkNos, replicationDegree, chunkBody, mdbPort, mdbAddress, mcPort, mcAddress);
		cb.start();
		try {
			Thread.sleep(SEVENTEEN_SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertFalse(db.containsFile(fileID));
		
		Thread test1 = new Thread() {
			public void run (){
				try {
					InetAddress mdbAddress = InetAddress.getByName(address);
					MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
					
					byte[] receivedData = new byte[ARRAY_SIZE];
					DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
					mdbSocket.joinGroup(mdbAddress);
					mdbSocket.receive(receivedPacket);
					
					String received = new String(receivedPacket.getData(),ENCODING).trim();
					if(received.equals(putchunkMessage))
						assertEquals(0,0);
					else
						assertEquals(1,0);
					
					mdbSocket.receive(receivedPacket);
					mdbSocket.receive(receivedPacket);
					mdbSocket.receive(receivedPacket);
					mdbSocket.receive(receivedPacket);
					assertEquals(0,0);
					
					mdbSocket.setSoTimeout(1000);
					try {
						mdbSocket.receive(receivedPacket);
						assertEquals(1,0);
						mdbSocket.leaveGroup(mdbAddress);
						mdbSocket.close();
					} catch (SocketTimeoutException e) {
						assertEquals(0,0);
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		cb = new ChunkBackup(db, fileID, path, chunkNos, replicationDegree, chunkBody, mdbPort, mdbAddress, mcPort, mcAddress);
		test1.run();
		cb.start();
		assertFalse(db.containsFile(fileID));
		/*
		Thread test2 = new Thread() {
			public void run() {
				try {
					InetAddress mdbAddress = InetAddress.getByName(address);
					InetAddress mcAddress = InetAddress.getByName(address1);
					
					MulticastSocket mdbSocket = new MulticastSocket(mdbPort);
					DatagramSocket mcSocket = new DatagramSocket();
					
					byte[] sendData = storeMessage.getBytes(ENCODING).trim();
					byte[] receivedData = new byte[ARRAY_SIZE];
					DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, mcAddress, mcPort);
					
					mdbSocket.joinGroup(mdbAddress);
					mdbSocket.receive(receivedPacket);
					mcSocket.send(sendPacket);
					assertFalse(db.containsFile(fileID));
					
					mdbSocket.receive(receivedPacket);
					mcSocket.send(sendPacket);
					mcSocket.send(sendPacket);
					assertTrue(db.containsFile(fileID));
					
					mdbSocket.close();
					mdbSocket.leaveGroup(mdbAddress);
					mcSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		cb = new ChunkBackup(db, fileID, path, chunkNos, replicationDegree, chunkBody, mdbPort, mdbAddress, mcPort, mcAddress);
		test2.run();
		cb.start();
		*/
	}
	

}
