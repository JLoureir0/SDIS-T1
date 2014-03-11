package database;

import java.util.HashMap;
import java.util.Map;

public class Database {
	
	private Map<String, File> chunksMap; // Key -> fileId value -> chunkNo
	
	public Database() {
		chunksMap = new HashMap<String,File>();
	}

	public Map<String, File> getChunksMap() {
		return chunksMap;
	}

	public void setChunksMap(Map<String, File> chunksMap) {
		this.chunksMap = chunksMap;
	}

}
