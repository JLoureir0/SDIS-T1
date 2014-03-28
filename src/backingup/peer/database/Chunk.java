package backingup.peer.database;

public class Chunk {

	private int replicationDegree;
	private int count;
	
	public Chunk(int replicationDegree) {
		this.replicationDegree = replicationDegree;
		count = 0;
	}
	
	public int getReplicationDegree() {
		return replicationDegree;
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
