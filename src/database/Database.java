package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Database {
	
	private Map<Integer,Integer> chunksMap; // Key -> fileId value -> chunkNo
	private ArrayList<Chunk> chunks;

	public Database() {
		setChunksMap(new HashMap<Integer,Integer>());
		setChunks(new ArrayList<Chunk>());
	}

	public Map<Integer,Integer> getChunksMap() {
		return chunksMap;
	}

	public void setChunksMap(Map<Integer,Integer> chunksMap) {
		this.chunksMap = chunksMap;
	}

	public ArrayList<Chunk> getChunks() {
		return chunks;
	}

	public void setChunks(ArrayList<Chunk> chunks) {
		this.chunks = chunks;
	}
	
}
