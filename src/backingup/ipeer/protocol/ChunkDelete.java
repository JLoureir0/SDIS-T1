package backingup.ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import backingup.Constants;

public class ChunkDelete {
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
	
	public boolean deleteChunk() {
		try {
			mcSocket = new DatagramSocket();
			String request = createPacket();
			
			for(int i=0;i<numberOfDeleteMessages;i++) {
				Thread.sleep(100);
				sendPacket(request);
			}
			mcSocket.close();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String createPacket() {
		String request = Constants.DELETE + " " + fileId + " " + Constants.CRLF + " " + Constants.CRLF; 
		return request;
	}
	
	public void sendPacket(String request) {
		try {
			byte[] deleteRequest = request.getBytes(Constants.ENCODING);
			sendPacket = new DatagramPacket(deleteRequest,deleteRequest.length,mcAddress,mcPort);
			mcSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
}
