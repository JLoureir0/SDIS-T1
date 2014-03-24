package backingup.ipeer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import backingup.Constants;

public class ChunkRestore {
	private String fileID;
	private int chunkNo;

	private int mcPort;
	private InetAddress mcAddress;
	private int mdrPort;
	private InetAddress mdrAddress;
	private DatagramSocket mcSocket;
	private MulticastSocket mdrSocket;
	private String receivedChunkBody;

	public ChunkRestore(String fileID, int chunkNo, int mcPort, int mdrPort, InetAddress mcAddress, InetAddress mdrAddress) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		this.mdrPort = mdrPort;
		this.mdrAddress = mdrAddress;
		this.receivedChunkBody = "";
		
		try {
			mcSocket = new DatagramSocket();
			mdrSocket = new MulticastSocket(mdrPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String start() {
		boolean received = false;
		while(!received) {
			System.out.println("Entrei no WHILE");
			sendPacket();
			received = receivePacket();
		}
		mcSocket.close();
		mdrSocket.close();
		System.out.println("Sai do WHILE");
		return receivedChunkBody;
	}
	
	public void sendPacket() {
		String stringRequest = Constants.GETCHUNK + " " + Constants.VERSION_1 + " " + fileID + " " + chunkNo + " " + Constants.CRLF + " " + Constants.CRLF;
		
		try {
			byte[] request = stringRequest.getBytes(Constants.ENCODING);
			DatagramPacket sendPacket = new DatagramPacket(request, request.length, mcAddress, mcPort);
			mcSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean receivePacket() {
		byte[] receiveData = new byte[Constants.ARRAY_SIZE];
		
		try {
			mdrSocket = new MulticastSocket(mdrPort);
			//mdrSocket.setSoTimeout(TWO_SECONDS);
			mdrSocket.joinGroup(mdrAddress);
			DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);

			mdrSocket.receive(receivedPacket);
			String receivedMessage = new String(receivedPacket.getData(),Constants.ENCODING).trim();
			String[] receivedSplit = receivedMessage.split(Constants.WHITESPACE_REGEX);
			
			System.out.println("************************");
			System.out.println("0: "+receivedSplit[0] + " 2: " + receivedSplit[2] + " 3: " + receivedSplit[3]);
			System.out.println("************************");
			if(receivedSplit[0].equals(Constants.CHUNK) && receivedSplit[2].equals(fileID) /*&& receivedSplit[3].equals(chunkNo)*/) {
				System.out.println("CERTO!");
				receivedChunkBody = receivedSplit[6];
				return true;
			}
			else
				return false;

		} catch (Exception e) {
			e.printStackTrace();
		}		
		return false;
	}
}
