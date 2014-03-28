package backingup.peer.multicastlistener;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import backingup.Constants;
import backingup.peer.database.Database;
import backingup.peer.protocol.ChunkBackup;

public class MDBListener extends Thread {
	private Database database;
	private int mcPort;
	private InetAddress mcAddress;
	private MulticastSocket mdbSocket;
	
	public MDBListener(Database database, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.database = database;
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
				String chunk = new String(chunkPacket.getData(),Constants.ENCODING).trim();
						
				if(correctChunk(chunk)) {
					String[] chunkSplit = chunk.split(Constants.WHITESPACE_REGEX);
					ChunkBackup chunkBackup = new ChunkBackup(database, chunkSplit[2], Integer.parseInt(chunkSplit[3]), Integer.parseInt(chunkSplit[4]), chunkSplit[7], mcPort, mcAddress);
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
		return (chunkSplit[0].equals(Constants.PUTCHUNK) && (chunkSplit.length == 7));
	}
}
