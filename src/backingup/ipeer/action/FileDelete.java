package backingup.ipeer.action;

import java.net.InetAddress;

import backingup.FileManager;
import backingup.ipeer.protocol.ChunkDelete;

public class FileDelete {
	
	String fileId;
	String path;
	int numberOfDeleteMessages;
	int mcPort;
	InetAddress mcAddress;

	public FileDelete(String fileId, int numberOfDeleteMessages, InetAddress mcAddress, int mcPort, String path) {
		this.fileId = fileId;
		this.numberOfDeleteMessages = numberOfDeleteMessages;
		this.mcAddress = mcAddress;   
		this.mcPort = mcPort;
		this.path = path;
	}
	
	public boolean DeleteFile() {
		boolean deleteChunkResult = false;
		ChunkDelete cd = new ChunkDelete(fileId,numberOfDeleteMessages,mcAddress,mcPort);
		deleteChunkResult = cd.deleteChunk();
		if(!deleteChunkResult)
			return false;
		
		return removeFileFromDir();
	}
	
	private boolean removeFileFromDir() {
		FileManager fm = new FileManager(path);
		return fm.delete();
	}

}
