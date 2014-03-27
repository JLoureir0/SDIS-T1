package backingup.ipeer.action;

import java.io.FileOutputStream;
import java.net.InetAddress;

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
		for(int i=1; i<=chunkNos; i++) {
			try {
				ChunkRestore cr = new ChunkRestore(fileID, i, mcPort, mdrPort, mcAddress, mdrAddress);
				fileBody += cr.restoreChunk();
			} catch (Exception e) {
				return false;
			}
		}
		return changeFileContent();
	}


	public boolean changeFileContent() {
		
		try {
            FileOutputStream fileOS = new FileOutputStream(path);
            fileOS.write(fileBody.getBytes());
            fileOS.close();
		} catch (Exception e) {
			return false;
		}
		return true;
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
