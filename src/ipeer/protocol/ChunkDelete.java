package ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChunkDelete {
	private static String fileId;
	private static int MCPort;
	private static InetAddress mcAddress;
	private static DatagramPacket sendPacket;
	private static DatagramSocket socket;
	private static int numberOfDeleteMessages;

	public static void main(String[] args) {
		try {
			socket = new DatagramSocket();
			
			for(int i=0;i<numberOfDeleteMessages;i++) {
				String request = createPacket();
				sendPacket(request);
			}

		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static String createPacket() {
		String request = "DELETE";
		request = " "+fileId+" CRLF CRLF"; 
		return request;
	}
	
	public static void sendPacket(String request) throws Exception {
		byte[] asciiRequest = request.getBytes("US-ASCII");
		sendPacket = new DatagramPacket(asciiRequest,asciiRequest.length,mcAddress,MCPort);
		socket.send(sendPacket);
		return;
	}
	
}
