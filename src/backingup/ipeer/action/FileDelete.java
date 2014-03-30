package backingup.ipeer.action;

import java.net.InetAddress;

import backingup.FileManager;
import backingup.ipeer.database.Database;
import backingup.ipeer.protocol.ChunkDelete;

public class FileDelete {
	
	private String fileID;
	private String path;
	private int numberOfDeleteMessages;
	private int mcPort;
	private InetAddress mcAddress;
	private Database db;
	
	public FileDelete(String fileID, int numberOfDeleteMessages, InetAddress mcAddress, int mcPort, String path, Database db) {
		this.fileID = fileID;
		this.numberOfDeleteMessages = numberOfDeleteMessages;
		this.mcAddress = mcAddress;   
		this.mcPort = mcPort;
		this.path = path;
		this.db = db;
	}
	
	public boolean deleteFile() {
		boolean deleteChunkResult = false;
		ChunkDelete cd = new ChunkDelete(fileID,numberOfDeleteMessages,mcAddress,mcPort);
		deleteChunkResult = cd.deleteChunk();
		if(!deleteChunkResult)
			return false;
		
		updateDatabase();
		return removeFileFromDir();
	}
	
	private void updateDatabase() {
		db.removeFile(fileID);
	}
	
	private boolean removeFileFromDir() {
		FileManager fm = new FileManager(path);
		return fm.delete();
	}
	
}
