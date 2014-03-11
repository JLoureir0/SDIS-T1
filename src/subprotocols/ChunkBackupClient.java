package subprotocols;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChunkBackupClient {
	
	// Thing to receive from Above
	private static int fileId;
	private static int chunkNumber;
	private static int replicationDegree;
	private static String chunkBody;
	private static double version=1.0;
	
	private static int mdbPort;
	private static InetAddress mdbAddress;
	private static int MCPort;
	private static InetAddress mcAddress;
	
	///////////////////////////////
	private static DatagramPacket sendPacket;
	private static  DatagramPacket receivePacket;
	private static DatagramSocket socket;
	private static byte receiveData[];
	private static int storedCounter;
	private static long tStart;
	private static long tEnd;
	
	public static void main(String[] args) {
		try {
			storedCounter=0;
			socket = new DatagramSocket();
			
			for(int i=1;i<=5;i++) {
				//Create Packet
				String request = createPacket();
				//Send Backup request mdb
				sendPacket(request);
				//Wait 500ms for answers from MC, actualize counter
				tStart = System.currentTimeMillis();
				while(true) {
					reveivePackets();
					tEnd = System.currentTimeMillis();
					if((tEnd-tStart) > (500*i))
						break;
				}
				
				if(storedCounter>=replicationDegree)
					break;
				else
					resetCounter();
			}

		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static String createPacket() {
		String request = "PUTCHUNK";
		request += " "+version+" "+fileId+" "+chunkNumber+" "+replicationDegree+" CRLF CRLF "+chunkBody;
		return request;
	}
	
	public static void sendPacket(String request) throws Exception {
		byte[] asciiRequest = request.getBytes("US-ASCII");
		sendPacket = new DatagramPacket(asciiRequest,asciiRequest.length,mdbAddress,mdbPort);
		socket.send(sendPacket);
		return;
	}
	
	public static void reveivePackets() throws Exception {
		receiveData = new byte[512];
		receivePacket = new DatagramPacket(receiveData, receiveData.length,mcAddress,MCPort);
		socket.receive(receivePacket);
		
		String response = new String(receivePacket.getData());
		System.out.print("Response: "+response);
		
		if(response.substring(0,6).equals("STORED")) {
			System.out.println("STORED");
			storedCounter++;
		}
		else
			System.out.println("Descartei n�o era STORED");
		
		return;
	}
	
	public static void resetCounter() {
		storedCounter = 0;
	}
	
}
