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
	
	public void addChunk(Chunk chunk) {
		chunks.add(chunk);
	}
	
	public void deleteChunk(int chunkNo) {
		for(int i=0;i<chunks.size();i++) {
			if(chunks.get(i).getChunkNo()==chunkNo)
				chunks.remove(i);
		}
	}
	
}
