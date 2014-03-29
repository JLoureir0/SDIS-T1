package backingup;

import java.net.InetAddress;

import backingup.ipeer.action.SearchDeletedFiles;
import backingup.ipeer.database.Database;
import backingup.peer.multicastlistener.MCListener;
import backingup.peer.multicastlistener.MDBListener;

public class BackingUP {
	private backingup.ipeer.database.Database ipeerDB;
	private backingup.peer.database.Database peerDB;
	
	
	
//	private int mcPort;
//	private int mdbPort;
//	private int mdrPort;
	
	private InetAddress mcAddress;
	private InetAddress mdbAddress;
	private InetAddress mdrAddress;
	
	public BackingUP(int mcPort, String mcAddress, int mdbPort, String mdbAddress, int mdrPort, String mdrAddress, int databaseSize) {
//		this.mcPort = mcPort;
//		this.mdbPort = mdbPort;
//		this.mdrPort = mdrPort;
		
		try {
		this.mcAddress = InetAddress.getByName(mcAddress);
		this.mdbAddress = InetAddress.getByName(mdbAddress);
		this.mdrAddress = InetAddress.getByName(mdrAddress);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		ipeerDB = new Database();
		peerDB = new backingup.peer.database.Database(databaseSize);
		
		SearchDeletedFiles deletedFiles = new SearchDeletedFiles(ipeerDB, mcPort, mdrPort, this.mcAddress, this.mdrAddress);
		deletedFiles.start();
		
		MCListener mcListener = new MCListener(peerDB, mdbPort, this.mdbAddress, mcPort, this.mcAddress, mdrPort, this.mdrAddress);
		mcListener.start();
		
		MDBListener mdbListener = new MDBListener(peerDB, mdbPort, this.mdbAddress, mcPort, this.mcAddress);
		mdbListener.start();
	}
	
	public boolean backupFile() {
		return true;
	}
	
	public boolean restoreFile() {
		return true;
	}
	
	public boolean deleteFile() {
		return true;
	}
	
	public boolean freeSpace() {
		return true;
	}
}
