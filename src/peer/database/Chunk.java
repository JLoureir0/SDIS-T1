package peer.database;

public class Chunk {

	private int replicationDegree;
	private String chunkBody;
	private int count;

	public Chunk() {
		chunkBody = "";
		replicationDegree = 0;
		count = 0;
	}
	
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
}
