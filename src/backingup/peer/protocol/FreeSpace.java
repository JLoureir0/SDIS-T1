package backingup.peer.protocol;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import backingup.Constants;
import backingup.peer.database.Database;
import backingup.peer.database.ID;

public class FreeSpace {
	private Database database;
	private int mcPort;
	private InetAddress mcAddress;
	
	public FreeSpace(Database database, int mcPort, InetAddress mcAddress) {
		this.database = database;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
	}
	
	public void freeSpace(int newSize) {
		if(newSize < 0)
			newSize = 0;
		if(newSize >= database.getSize())
			database.setMaxSize(newSize);
		else {
			while(database.getSize() > newSize) {
				ID id = database.removeChunk();
				if(id != null)
					sendRemoved(id);
			}
			database.setMaxSize(newSize);
		}
	}
	
	private void sendRemoved(ID id) {
		String removedMessage = Constants.REMOVED + " " + Constants.VERSION_1 +  " " + id.getFileID() + " " + id.getChunkNo() + " " + Constants.CRLF + " " + Constants.CRLF;
		
		try {
			DatagramSocket mcSocket = new DatagramSocket();
			byte[] removedData = removedMessage.getBytes(Constants.ENCODING);
			DatagramPacket removedPacket = new DatagramPacket(removedData, removedData.length, mcAddress, mcPort);
			mcSocket.send(removedPacket);
			mcSocket.close();
			Thread.sleep(100);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
