package backingup.ipeer.action;

import java.net.InetAddress;

import backingup.FileManager;
import backingup.ipeer.database.Database;
import backingup.ipeer.protocol.ChunkDelete;

public class FileDelete {
	
	private String fileId;
	private String path;
	private int numberOfDeleteMessages;
	private int mcPort;
	private InetAddress mcAddress;
	private Database db;
	
	public FileDelete(String fileId, int numberOfDeleteMessages, InetAddress mcAddress, int mcPort, String path, Database db) {
		this.fileId = fileId;
		this.numberOfDeleteMessages = numberOfDeleteMessages;
		this.mcAddress = mcAddress;   
		this.mcPort = mcPort;
		this.path = path;
		this.db = db;
	}
	
	public boolean DeleteFile() {
		boolean deleteChunkResult = false;
		ChunkDelete cd = new ChunkDelete(fileId,numberOfDeleteMessages,mcAddress,mcPort);
		deleteChunkResult = cd.deleteChunk();
		if(!deleteChunkResult)
			return false;
		
		updateDatabase();
		return removeFileFromDir();
	}
	
	private void updateDatabase() {
		db.removeFile(fileId);
	}
	
	private boolean removeFileFromDir() {
		FileManager fm = new FileManager(path);
		return fm.delete();
	}

}
