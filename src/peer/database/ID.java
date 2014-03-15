package peer.database;

public class ID {
	private String fileID;
	private int chunkNo;
	
	public ID(String fileID, int chunkNo) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
	}
	
	public String getFileID() {
		return fileID;
	}
	
	public int getChunkNo() {
		return chunkNo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ID) {
			return this.fileID == ((ID)obj).getFileID() && this.chunkNo == ((ID)obj).getChunkNo();
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		String hash = fileID + chunkNo;
		return hash.hashCode();
	}
}
