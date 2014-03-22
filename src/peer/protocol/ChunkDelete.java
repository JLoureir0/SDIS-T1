package peer.protocol;

import peer.database.Database;

public class ChunkDelete extends Thread{
	private Database database;
	private String fileID;
	
	public ChunkDelete(Database database, String fileID) {
		this.database = database;
		this.fileID = fileID;
	}
	
	public void run() {
		database.removeFile(fileID);
	}
}
