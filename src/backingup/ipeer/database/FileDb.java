package backingup.ipeer.database;

public class FileDb {

	private String path;
	private int chunkNos;
	
	public FileDb(String path, int chunkNos) {
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
