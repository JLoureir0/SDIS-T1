package database;

public class Chunk {

	private String chunkBody;
	private int fileId;
	private int replicationDegree;
	private int chunkNo;
	
	public Chunk() {
		chunkBody = "";
		fileId = 0;
		replicationDegree = 0;
		chunkNo = 0;
	}
	
	public Chunk(int fileId,int replicationDegree,int chunkNo,String chunkBody) {
		this.chunkBody = chunkBody;
		this.fileId = fileId;
		this.replicationDegree = replicationDegree;
		this.chunkNo = chunkNo;
	}

	public String getChunkBody() {
		return chunkBody;
	}

	public void setChunkBody(String chunkBody) {
		this.chunkBody = chunkBody;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public void setReplicationDegree(int replicationDegree) {
		this.replicationDegree = replicationDegree;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public void setChunkNo(int chunkNo) {
		this.chunkNo = chunkNo;
	}
	
}
