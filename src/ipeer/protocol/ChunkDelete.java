package ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChunkDelete {
	private static String ENCODING = "US-ASCII";
	private static String DELETE = "delete";
	private static String CRLF = "CRLF";
	private int mcPort;
	private InetAddress mcAddress;
	private DatagramPacket sendPacket;
	private DatagramSocket mcSocket;
	private String fileId;
	private int numberOfDeleteMessages;

	public ChunkDelete(String fileId,int numberOfDeleteMessages,InetAddress mcAddress,int mcPort) {
		this.fileId = fileId;
		this.numberOfDeleteMessages = numberOfDeleteMessages;
		this.mcAddress = mcAddress;
		this.mcPort = mcPort;
	}
	
	public void start() {
		try {
			mcSocket = new DatagramSocket();
			String request = createPacket();
			
			for(int i=0;i<numberOfDeleteMessages;i++) {
				sendPacket(request);
			}
			mcSocket.close();
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public String createPacket() {
		String request = DELETE + " " + fileId + " " + CRLF + " " + CRLF; 
		return request;
	}
	
	public void sendPacket(String request) throws Exception {
		byte[] deleteRequest = request.getBytes(ENCODING);
		sendPacket = new DatagramPacket(deleteRequest,deleteRequest.length,mcAddress,mcPort);
		mcSocket.send(sendPacket);
		return;
	}
	
}
