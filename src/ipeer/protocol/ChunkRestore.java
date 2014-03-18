package ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ChunkRestore {
	private final String VERSION_1 = "1.0";
	private final String CRLF = "CRLF";
	private final String ENCODING = "US-ASCII";
	private final String GETCHUNK = "GETCHUNK";
	
	private String fileID;
	private int chunkNo;
	private DatagramSocket mcSocket;
	private int mcPort;
	private InetAddress mcAddress;
	
	private DatagramSocket mdrSocket;
	private int mdrPort;
	private InetAddress mdrAddress;
	private String chunkBody;
	private static byte receiveData[];

	public ChunkRestore(String fileID, int chunkNo, int mcPort, int mdrPort, InetAddress mcAddress, InetAddress mdrAddress) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		this.mdrPort = mdrPort;
		this.mdrAddress = mdrAddress;
		
		try {
			mcSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		sendPacket();
		receivePacket();
	}
	
	public void sendPacket() {
		String stringRequest = GETCHUNK + " " + VERSION_1 + " " + fileID + " " + chunkNo + " " + CRLF + " " + CRLF;
		
		try {
			byte[] request = stringRequest.getBytes(ENCODING);
			DatagramPacket sendPacket = new DatagramPacket(request, request.length, mcAddress, mcPort);
			mcSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receivePacket() {
		receiveData = new byte[512];
		
		try {
			mdrSocket = new DatagramSocket();
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length, mdrAddress, mdrPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
