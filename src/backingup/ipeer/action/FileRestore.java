package backingup.ipeer.action;

import java.net.InetAddress;

import backingup.FileManager;
import backingup.ipeer.database.Database;
import backingup.ipeer.protocol.ChunkRestore;

public class FileRestore {

	private String fileBody;
	private String path;
	private String fileID;
	private Database db;
	private int chunkNos;
	private int mdrPort;
	private InetAddress mdrAddress;
	private int mcPort;
	private InetAddress mcAddress;
	
	public FileRestore(String fileID, int chunkNo, int mcPort, int mdrPort, InetAddress mcAddress, InetAddress mdrAddress, Database db) {
		this.fileID = fileID;
		this.db = db;
		this.mdrPort = mdrPort;
		this.mdrAddress = mdrAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		this.path = this.db.getFilePath(fileID);
		this.chunkNos = db.getFileChunkNos(fileID);
		this.fileBody = "";
	}
	
	public boolean restoreFile() {
		for(int i=0; i<chunkNos; i++) {
			try {
				ChunkRestore cr = new ChunkRestore(fileID, i, mcPort, mdrPort, mcAddress, mdrAddress);
				fileBody += cr.restoreChunk();
			} catch (Exception e) {
				return false;
			}
		}
		return changeFileContent();
	}

	private boolean changeFileContent() {
		FileManager fm = new FileManager(path);
		return fm.write(fileBody);
	}
	
	public String getFileBody() { 
		return fileBody;
	}

	public void setFileBody(String fileBody) {
		this.fileBody = fileBody;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
