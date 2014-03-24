package backingup.ipeer.action;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import backingup.ipeer.database.Database;



public class FileBackup {

	private int CHUNKSIZE = 64000;
	private HashMap<Integer,String> fileChunks;
	private Database db;
	private String path;
	private boolean multiple;
	
	public FileBackup(String path, Database db) {
		this.path = path;
		fileChunks = new HashMap<Integer, String>();
		this.multiple = false;
		this.db = db;
	}
	
	public boolean backupFile() {
		
		try {
		    File file = new File(path);
		    FileInputStream fileStram = new FileInputStream(file);
		    byte[] dataBody = new byte[(int)file.length()];
		    fileStram.read(dataBody);
		    fileStram.close();
		    String fileBody = new String(dataBody, "UTF-8");
		    
		    if (fileBody.length() % CHUNKSIZE == 0)
		    	this.multiple = true;
		    
		    String chunkBody = "";
		    int byteCounter = 0;
		    for(int i = 0; i < fileBody.length() ; i++) {
		    	
		    	if(byteCounter == CHUNKSIZE) {
		    		int chunkNo = i+1;
		    		fileChunks.put(chunkNo,chunkBody);
		    		chunkBody = new String("");
		    		byteCounter = 0;
		    	}
		    	
		    	chunkBody += fileBody.charAt(i);
		    	byteCounter++;
		    }
		    
		    if(multiple) {
		    	int chunkNo = fileBody.length()+2;
		    	fileChunks.put(chunkNo,"");
		    }
		    
		    
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}

//FALTA ACTUALIZAR DB
//FALTA CRIAR FILEID
//FALTA FAZER BACKUP DE TODOS OS CHUNKS
