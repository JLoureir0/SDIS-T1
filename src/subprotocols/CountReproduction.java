package subprotocols;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class CountReproduction extends Thread {
	private MulticastSocket mcSocket;
	private DatagramPacket storePacket;
	private byte[] storeData;
	@SuppressWarnings("unused")
	private InetAddress mcAddress;
	@SuppressWarnings("unused")
	private int mcPort;
	private int count;
	
	public CountReproduction(InetAddress mcAddress, int mcPort) {
		count = 0;
		this.mcAddress = mcAddress;
		this.mcPort = mcPort;
		try {
			mcSocket = new MulticastSocket(mcPort);
			mcSocket.joinGroup(mcAddress);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long end = System.currentTimeMillis() + 500;
		while(System.currentTimeMillis() < end) {
			storeData = new byte[512];
			try {
				storePacket = new DatagramPacket(storeData, storeData.length);
				mcSocket.receive(storePacket);
				if(correctChunk())
					count++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		updateCount();
	}
	
	private void updateCount() {
		System.out.println("Count updated: " + count);
	}
	
	private boolean correctChunk() {
		return true;
	}
}
