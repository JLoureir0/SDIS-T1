package database;

import java.util.HashMap;
import java.util.Map;

public class Database {
	
	private Map<String, File> files; // Key -> fileId value -> File
	
	public Database() {
		files = new HashMap<String,File>();
	}

	public Map<String, File> getChunksMap() {
		return files;
	}

	public void setChunksMap(Map<String, File> chunksMap) {
		this.files = chunksMap;
	}
	
	public void addFile(String fileId,File file) {
		files.put(fileId, file);
	}
	
	public void removeFile(String fileId) {
		files.remove(fileId);
	}

}
