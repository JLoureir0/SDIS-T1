package ipeer.protocol;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ReplicationCounter extends Thread {
	private final int HALF_A_SECOND = 500;
	private final int ARRAY_SIZE = 512;
	private final String ENCODING = "US-ASCII";
	private final String WHITESPACE_REGEX = "\\s";

	private String fileID;
	private int chunkNo;
	private MulticastSocket mcSocket;
	@SuppressWarnings("unused")
	private InetAddress mcAddress;
	@SuppressWarnings("unused")
	private int mcPort;
	private int[] replicationCounter;
	private int iteration;
	
	public ReplicationCounter(String fileID, int chunkNo, InetAddress mcAddress, int mcPort, int[] replicationCounter, int iteration) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.mcAddress = mcAddress;
		this.mcPort = mcPort;
		this.replicationCounter = replicationCounter;
		replicationCounter[0] = 0;
		this.iteration = iteration;
		
		try {
			mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
			mcSocket.setSoTimeout((int) (HALF_A_SECOND*Math.pow(2, iteration)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		byte[] storeData = new byte[ARRAY_SIZE];
		long endTime = (long) (System.currentTimeMillis() + 500*Math.pow(2, iteration));
		
		while(System.currentTimeMillis() < endTime) {
			try {
				DatagramPacket storePacket = new DatagramPacket(storeData, storeData.length);
				mcSocket.receive(storePacket);
				if(System.currentTimeMillis() < endTime && correctChunk(new String(storePacket.getData(), ENCODING).trim()))
					replicationCounter[0] = replicationCounter[0]+1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		mcSocket.close();
	}
	
	private boolean correctChunk(String store) {
		String[] storeSplit = store.split(WHITESPACE_REGEX);
		return (storeSplit[0].equals("STORED") && storeSplit[2].equals(fileID) && storeSplit[3].equals(Integer.toString(chunkNo)));
	}
}
