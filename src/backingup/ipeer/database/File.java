package backingup.ipeer.database;

public class File {

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
