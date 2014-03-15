package peer.protocol;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.junit.Test;

import peer.database.Database;

public class SubProtocolTest {

	@Test
	public void testChunkBackup() {
		String fileID = "id1";
		int chunkNo = 1;
		int replicationDegree = 9;
		String chunkBody = "sensitive data";
		int mcPort = 54321;
		String address = "224.2.2.3";
		InetAddress mcAddress;
		Database db = new Database();
		
		try {
			mcAddress = InetAddress.getByName(address);
			
			ChunkBackup cb = new ChunkBackup(db, fileID, chunkNo, replicationDegree, chunkBody, mcPort, mcAddress);
			cb.start();		
			
			MulticastSocket mcSocket = new MulticastSocket(mcPort);
			byte[] receivedData = new byte[512];
			DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
			mcSocket.joinGroup(mcAddress);
			mcSocket.receive(receivedPacket);
			mcSocket.close();
			
			String store = "STORED 1.0 " + fileID + " " + chunkNo + " CRLF CRLF";
			String receivedStore = new String(receivedPacket.getData(),"US-ASCII").trim();
			assertEquals(store, receivedStore);
			assertTrue(db.containsChunk(fileID, chunkNo));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
