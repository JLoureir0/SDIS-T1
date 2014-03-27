package backingup.peer.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
			// save chunk to file sys
		}
	}
	
	public ID removeChunk() {
		Iterator<ID> it = chunks.keySet().iterator();
		while(it.hasNext()) {
			ID id = it.next();
			Chunk chunk = chunks.get(id);
			if(chunk.getReplicationDegree() < chunk.getCount()) {
				// remove chunk from file sys
				it.remove();
				return id;
			}
		}
		it = chunks.keySet().iterator();
		if(it.hasNext()) {
			ID id = it.next();
			// remove chunk from file sys
			it.remove();
			return id;
		}
		return null;
	}

	public void removeFile(String fileID) {
		Iterator<ID> it = chunks.keySet().iterator();
		while(it.hasNext()) {
			ID id = it.next();
			if(id.getFileID() == fileID) {
				// remove chunk from file sys
				it.remove();
			}
		}
	}
	
	public boolean containsChunk(String fileID, int chunkNo) {
		return chunks.containsKey(new ID(fileID,chunkNo));
	}
	
	public String getChunkBody(String fileID, int chunkNo) {
		// get Body from file sys
		return "";
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
		// get size of the directory
		return 0;
	}
}
