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
	
	public String restoreChunk() throws ChunkNotFound {
		long endTime = System.currentTimeMillis() + Constants.SLEEP;
		boolean received = false;
		while(!received) {
			
			if(System.currentTimeMillis() > endTime)
				throw new ChunkNotFound();

			sendGetChunk();
			received = receiveChunk();
		}
		mcSocket.close();
		mdrSocket.close();
		return receivedChunkBody;
	}
	
	public void sendGetChunk() {
		String stringRequest = Constants.GETCHUNK + " " + Constants.VERSION_1 + " " + fileID + " " + chunkNo + " " + Constants.CRLF;
		
		try {
			byte[] request = stringRequest.getBytes(Constants.ENCODING);
			DatagramPacket sendPacket = new DatagramPacket(request, request.length, mcAddress, mcPort);
			mcSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	public boolean receiveChunk() {
		byte[] receiveData = new byte[Constants.ARRAY_SIZE];
		
		try {
			mdrSocket = new MulticastSocket(mdrPort);
			mdrSocket.setSoTimeout(Constants.SLEEP);
			mdrSocket.joinGroup(mdrAddress);
			DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);

			mdrSocket.receive(receivedPacket);
			String receivedMessage = new String(receivedPacket.getData(),Constants.ENCODING).trim();

			if(correctChunk(receivedMessage))
				return true;		
			return false;
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return false;
	}
	
	public boolean correctChunk(String chunk) {
		String[] chunkSplit = chunk.split(Constants.WHITESPACE_REGEX);
		String chunkNumber = ""+chunkNo;
		if(chunkSplit[0].equals(Constants.CHUNK) && chunkSplit[1].equals(Constants.VERSION_1) && chunkSplit[2].equals(fileID) && chunkSplit[3].equals(chunkNumber) && chunkSplit[4].equals(Constants.CRLF)) {
			receivedChunkBody = chunkSplit[6];
			return true;
		}
		else
			return false;
	}	
}
