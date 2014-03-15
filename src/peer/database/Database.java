package peer.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Database {

  private Map<ID, Chunk> chunks;

  public Database() {
		chunks = new HashMap<ID,Chunk>();
	}
  
	public void addChunk(String fileID, int chunkNo, int replicationDegree, String chunkBody) {
		ID id = new ID(fileID, chunkNo);
		Chunk chunk = new Chunk(replicationDegree, chunkBody);
		chunks.put(id, chunk);
	}

	public void removeFile(String fileId) {
		Iterator<ID> it = chunks.keySet().iterator();
		while(it.hasNext()) {
			ID id = it.next();
			if(id.getFileID() == fileId)
				it.remove();
		}
	}
	
	public boolean containsChunk(String fileID, int chunkNo) {
		return chunks.containsKey(new ID(fileID,chunkNo));
	}
	
	public String getChunkBody(String fileID, int chunkNo) {
		return chunks.get(new ID(fileID,chunkNo)).getChunkBody();
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
	
	public void resetCount(String fileID, int chunkNo) {
		chunks.get(new ID(fileID, chunkNo)).resetCount();;
	}
}
