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
	
	public MDBListener(Database database, int mcPort, InetAddress mcAddress) {
		this.database = database;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
	}
	
	public void run() {
		try {
			@SuppressWarnings("resource")
			MulticastSocket mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
			
			while(true) {
				byte[] chunkData = new byte[Constants.ARRAY_SIZE];
				DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length);
				mcSocket.receive(chunkPacket);
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
	}
	
	private boolean correctChunk(String chunk) {
		String[] chunkSplit = chunk.split(Constants.WHITESPACE_REGEX);
		return (chunkSplit[0].equals(Constants.PUTCHUNK) && (chunkSplit.length == 8));
	}
}
