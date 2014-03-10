package subprotocols;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class ChunkBackupServer {
	
	private static DatagramSocket mcSocket;
	private static DatagramPacket storePacket;
	private static InetAddress mcAddress;
	private static int mcPort;
	private static String fileID;
	private static int chunkNo;
	
	private static Random random;
	
	public static void main(String[] args) {
		try {
			mcSocket = new DatagramSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		if(!storedChunk())
			storeChunk();
		countReproduction();
		sendStore();
	}
	
	private static void storeChunk() {
		System.out.println("STORED !!");
	}
	
	private static void countReproduction() {
		//Thread to count
		CountReproduction cr = new CountReproduction(mcAddress, mcPort);
		cr.start();
	}
	
	private static boolean storedChunk() {
		//check if it is already stored
		return false;
	}
	
	private static void sendStore() {
		String response = "STORED 1.0 " + fileID + " " + chunkNo + " CRLF CRLF";
		byte[] responseAscii = null;
		
		try {
			responseAscii = response.getBytes("US-ASCII");
			storePacket = new DatagramPacket(responseAscii, responseAscii.length,mcAddress,mcPort);
			int r = random.nextInt(401);
			Thread.sleep(r);
			mcSocket.send(storePacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
