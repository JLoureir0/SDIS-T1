package backingup.peer.multicastlistener;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import backingup.Constants;
import backingup.peer.protocol.ChunkBackup;

public class MDBListener extends Thread {
	private backingup.peer.database.Database peerDB;
	private backingup.ipeer.database.Database ipeerDB;
	private int mcPort;
	private InetAddress mcAddress;
	private MulticastSocket mdbSocket;
	
	public MDBListener(backingup.peer.database.Database peerDB, backingup.ipeer.database.Database ipeerDB, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.peerDB = peerDB;
		this.ipeerDB = ipeerDB;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		try {
			mdbSocket = new MulticastSocket(mdbPort);
			mdbSocket.joinGroup(mdbAddress);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			
			
			while(true) {
				byte[] chunkData = new byte[Constants.ARRAY_SIZE];
				DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length);
				mdbSocket.receive(chunkPacket);
				String chunk = new String(chunkPacket.getData(),Constants.ENCODING).substring(0, chunkPacket.getLength());
						
				if(correctChunk(chunk)) {
					String[] chunkSplit = chunk.split(Constants.WHITESPACE_REGEX);
					int beginIndex = chunkSplit[0].length()+chunkSplit[1].length()+chunkSplit[2].length()+chunkSplit[3].length()+chunkSplit[4].length()+chunkSplit[5].length()+6;
					ChunkBackup chunkBackup = new ChunkBackup(peerDB, chunkSplit[2], Integer.parseInt(chunkSplit[3]), Integer.parseInt(chunkSplit[4]), chunk.substring(beginIndex, chunk.length()), mcPort, mcAddress);
					chunkBackup.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mdbSocket.close();
	}
	
	private boolean correctChunk(String chunk) {
		String[] chunkSplit = chunk.split(Constants.WHITESPACE_REGEX);
		return (chunkSplit[0].equals(Constants.PUTCHUNK) && (!ipeerDB.containsFile(chunkSplit[2])) && chunkSplit[1].equals(Constants.VERSION_1));
	}
}
