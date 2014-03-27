package backingup.peer.database;

public class Chunk {

	private int replicationDegree;
	private String chunkBody;
	private int count;
	
	public Chunk(int replicationDegree, String chunkBody) {
		this.replicationDegree = replicationDegree;
		this.chunkBody = chunkBody;
		count = 0;
	}
	
	public int getReplicationDegree() {
		return replicationDegree;
	}
	
	public String getChunkBody() {
		return chunkBody;
	}

	public int getCount() {
		return count;
	}

	public void resetCount() {
		count = 0;
	}
	
	public void increaseCount() {
		count++;
	}
	
	public void decreaseCount() {
		count--;
	}
}
