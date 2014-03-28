package backingup.peer.database;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import backingup.Constants;
import backingup.FileManager;

public class Database {
	private int maxSize;
	private Map<ID, Chunk> chunks;

	public Database(int maxSize) {
		chunks = new HashMap<ID,Chunk>();
		this.maxSize = maxSize;
	}
  
	public void addChunk(String fileID, int chunkNo, int replicationDegree, String chunkBody) {
		if((chunkBody.length()+getSize()) <= maxSize) {
			ID id = new ID(fileID, chunkNo);
			Chunk chunk = new Chunk(replicationDegree);
			chunks.put(id, chunk);
			FileManager fileManager = new FileManager(Constants.BACKUP_PATH, fileID + chunkNo);
			fileManager.write(chunkBody);
		}
	}
	
	public ID removeChunk() {
		 Iterator<Entry<ID, Chunk>> ite = chunks.entrySet().iterator();
		while(ite.hasNext()) {
			Map.Entry<ID, Chunk> entry = (Map.Entry<ID, Chunk>)ite.next();
			ID id = entry.getKey();
			Chunk chunk = entry.getValue();
			if(chunk.getReplicationDegree() < chunk.getCount()) {
				ite.remove();
				FileManager fileManager = new FileManager(Constants.BACKUP_PATH, id.getFileID() + id.getChunkNo());
				fileManager.delete();
				return id;
			}
		}
		Iterator<ID> it = chunks.keySet().iterator();
		if(it.hasNext()) {
			ID id = it.next();
			it.remove();
			FileManager fileManager = new FileManager(Constants.BACKUP_PATH, id.getFileID() + id.getChunkNo());
			fileManager.delete();
			return id;
		}
		return null;
	}

	public void removeFile(String fileID) {
		Iterator<ID> it = chunks.keySet().iterator();
		while(it.hasNext()) {
			ID id = it.next();
			if(id.getFileID() == fileID) {
				it.remove();
				FileManager fileManager = new FileManager(Constants.BACKUP_PATH, id.getFileID() + id.getChunkNo());
				fileManager.delete();
			}
		}
	}
	
	public boolean containsChunk(String fileID, int chunkNo) {
		return chunks.containsKey(new ID(fileID,chunkNo));
	}
	
	public String getChunkBody(String fileID, int chunkNo) {
		FileManager fileManager = new FileManager(Constants.BACKUP_PATH, fileID + chunkNo);
		return fileManager.read();
	}

	public int getReplicationDegree(String fileID, int chunkNo) {
		return chunks.get(new ID(fileID, chunkNo)).getReplicationDegree();
	}
	
	public int getCount(String fileID, int chunkNo) {
		return chunks.get(new ID(fileID, chunkNo)).getCount();
	}
	
	public void increaseCount(String fileID, int chunkNo) {
		chunks.get(new ID(fileID, chunkNo)).increaseCount();;
	}
	
	public void decreaseCount(String fileID, int chunkNo) {
		chunks.get(new ID(fileID, chunkNo)).decreaseCount();
	}
	
	public void resetCount(String fileID, int chunkNo) {
		chunks.get(new ID(fileID, chunkNo)).resetCount();;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public int getSize() {
		int size = 0;
		for(ID id: chunks.keySet()) {
			File file = new File(Constants.BACKUP_PATH + File.separator + id.getFileID() + id.getChunkNo());
			size += file.length();
		}
		return size;
	}
}
