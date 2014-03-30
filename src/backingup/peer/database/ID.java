package backingup.peer.database;

public class ID implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1850333793836945277L;
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
			return (this.fileID.equals(((ID)obj).getFileID()) && this.chunkNo == ((ID)obj).getChunkNo());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		String hash = fileID + chunkNo;
		return hash.hashCode();
	}
}
