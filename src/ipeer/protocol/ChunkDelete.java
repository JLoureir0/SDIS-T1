package ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChunkDelete {
	private static String ENCODING = "US-ASCII";
	private static String DELETE = "DELETE";
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
	
	public boolean start() {
		try {
			mcSocket = new DatagramSocket();
			String request = createPacket();
			
			for(int i=0;i<numberOfDeleteMessages;i++) {
				Thread.sleep(100);
				sendPacket(request);
			}
			mcSocket.close();
			System.out.println("Vou fechar com true");
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Vou fechar com false");
		return false;
	}
	
	public String createPacket() {
		String request = DELETE + " " + fileId + " " + CRLF + " " + CRLF; 
		return request;
	}
	
	public void sendPacket(String request) {
		try {
			byte[] deleteRequest = request.getBytes(ENCODING);
			sendPacket = new DatagramPacket(deleteRequest,deleteRequest.length,mcAddress,mcPort);
			mcSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
}
