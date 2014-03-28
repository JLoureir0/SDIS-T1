package backingup.ipeer.action;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import backingup.FileManager;
import backingup.ipeer.database.Database;
import backingup.ipeer.database.File;

public class SearchDeletedFiles extends Thread {

	private int FIVE_SECONDS = 5000;
	private Database db;
	private int mcPort;
	private int mdrPort;
	private InetAddress mcAddress;
	private InetAddress mdrAddress;
	private HashMap<String, File> files;
	
	public SearchDeletedFiles(Database database, int mcPort, int mdrPort, InetAddress mcAddress, InetAddress mdrAddress) {
		this.db = database;
		this.mcPort = mcPort;
		this.mdrPort = mdrPort;
		this.mcAddress = mcAddress;
		this.mdrAddress = mdrAddress;
		files = db.getFiles();
	}
	
	public void run() {
		boolean pathExists = false;
		while(true) {
			files = db.getFiles();
			try {
			    Iterator it = files.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry file = (Map.Entry)it.next();
			        String fileID = (String) file.getKey();
			        String path = db.getFilePath(fileID);
			        FileManager fm = new FileManager(path);
			        pathExists = fm.CheckIfFileExists();
			        if(!pathExists)
			        	restoreFile(fileID);
			        it.remove();
			    }
			    Thread.sleep(FIVE_SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	private void restoreFile(String fileID) {
		FileRestore fr = new FileRestore(fileID, mcPort, mdrPort, mcAddress, mdrAddress, db);
		fr.restoreFile();
	}
	
}
