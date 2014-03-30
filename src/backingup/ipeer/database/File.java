package backingup.ipeer.database;

public class File implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3543431154083149242L;
	private String path;
	private int chunkNos;
	
	public File(String path, int chunkNos) {
		this.path = path;
		this.chunkNos = chunkNos;
	}

	public String getPath() {
		return path;
	}
	
	public int getChunkNos() {
		return chunkNos;
	}


}
