package backingup.peer.multicastlistener;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import backingup.Constants;
import backingup.peer.protocol.ChunkDelete;
import backingup.peer.protocol.ChunkRemoved;
import backingup.peer.protocol.ChunkRestore;
import backingup.peer.database.Database;

public class MCListener extends Thread {
	private Database database;
	private int mdbPort;
	private InetAddress mdbAddress;
	private int mcPort;
	private InetAddress mcAddress;
	private int mdrPort;
	private InetAddress mdrAddress;
	private MulticastSocket mcSocket;
	
	public MCListener(Database database, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress, int mdrPort, InetAddress mdrAddress) {
		this.database = database;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		this.mdrPort = mdrPort;
		this.mdrAddress = mdrAddress;
		try {
			mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {			
			while(true) {
				byte[] chunkData = new byte[Constants.ARRAY_SIZE];
				DatagramPacket chunkPacket = new DatagramPacket(chunkData, chunkData.length);
				mcSocket.receive(chunkPacket);
				String chunk = new String(chunkPacket.getData(),Constants.ENCODING).substring(0, chunkPacket.getLength());
				try {
					parseChunk(chunk);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseChunk(String chunk) {
		String[] chunkSplit = chunk.split(Constants.CRLF);
		String[] headerSplit = chunkSplit[0].split(Constants.WHITESPACE_REGEX);
		if(headerSplit[0].equals(Constants.GETCHUNK) && headerSplit[1].equals(Constants.VERSION_1)) {
			ChunkRestore chunkRestore = new ChunkRestore(database, headerSplit[2], Integer.parseInt(headerSplit[3]), mdrPort, mdrAddress);
			chunkRestore.start();
		}
		else if(headerSplit[0].equals(Constants.DELETE)) {
			ChunkDelete chunkDelete = new ChunkDelete(database, headerSplit[1]);
			chunkDelete.start();
		}
		else if(headerSplit[0].equals(Constants.REMOVED) && headerSplit[1].equals(Constants.VERSION_1)) {
			ChunkRemoved chunkRemoved = new ChunkRemoved(database, headerSplit[2], Integer.parseInt(headerSplit[3]), mdbPort, mdbAddress, mcPort, mcAddress);
			chunkRemoved.start();
		}
	}
}
