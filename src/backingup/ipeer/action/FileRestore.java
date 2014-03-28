package backingup.ipeer.action;

import java.net.InetAddress;

import backingup.FileManager;
import backingup.ipeer.database.Database;
import backingup.ipeer.protocol.ChunkRestore;

public class FileRestore {

	private String path;
	private String fileID;
	private Database db;
	private int chunkNos;
	private int mdrPort;
	private InetAddress mdrAddress;
	private int mcPort;
	private InetAddress mcAddress;
	private FileManager fm;
	
	public FileRestore(String fileID, int mcPort, int mdrPort, InetAddress mcAddress, InetAddress mdrAddress, Database db) {
		this.fileID = fileID;
		this.db = db;
		this.mdrPort = mdrPort;
		this.mdrAddress = mdrAddress;
		this.mcPort = mcPort;
		this.mcAddress = mcAddress;
		this.path = this.db.getFilePath(fileID);
		this.chunkNos = db.getFileChunkNos(fileID);
		fm = new FileManager(path);
		fm.setAppend(false);
	}
	
	public boolean restoreFile() {
		boolean resultWriteChunk = false;
		String fileBody = "";
		for(int i=0; i<chunkNos; i++) {
			try {
				ChunkRestore cr = new ChunkRestore(fileID, i, mcPort, mdrPort, mcAddress, mdrAddress);
				fileBody = cr.restoreChunk();
				resultWriteChunk = writeChunk(fileBody);
				if(!resultWriteChunk)
					return false;
				fileBody = "";
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	private boolean writeChunk(String chunkBody) {
		boolean result = fm.write(chunkBody);
		fm.setAppend(true);
		return result;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		this.fm = new FileManager(path);
	}
}
