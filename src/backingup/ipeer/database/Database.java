package backingup.ipeer.database;

import java.util.HashMap;

public class Database {

	private HashMap<String, FileDb> files;
	
	public Database() {
		files = new HashMap<String,FileDb>();
	}
	
	public void addFile(String fileID, FileDb file) {
		files.put(fileID, file);
	}
	
	public void removeFile(String fileID) {
		files.remove(fileID);
	}

	public boolean containsFile(String fileID) {
		return files.containsKey(fileID);
	}

	public String getFilePath(String fileID) {
		if(files.containsKey(fileID))
			return files.get(fileID).getPath();
		return null;
	}

	public int getFileChunkNos(String fileID) {
		if(files.containsKey(fileID))
			return files.get(fileID).getChunkNos();
		return 0;
	}

}
