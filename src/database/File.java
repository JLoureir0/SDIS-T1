package database;

import java.util.ArrayList;

public class File {

	private ArrayList<Chunk> chunks;
	
	public File() {
		chunks = new ArrayList<Chunk>();
	}

	public ArrayList<Chunk> getChunks() {
		return chunks;
	}

	public void setChunks(ArrayList<Chunk> chunks) {
		this.chunks = chunks;
	}
	
}
