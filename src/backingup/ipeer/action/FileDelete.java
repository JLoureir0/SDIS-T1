package backingup.ipeer.action;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

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
	
	public boolean removeFileFromDir() {
		boolean success = false;
        Path filePath = FileSystems.getDefault().getPath(path);

        try {
            success = Files.deleteIfExists(filePath);
        } catch (IOException | SecurityException e) {
        	e.printStackTrace();
        }
        return success;
	}

}
