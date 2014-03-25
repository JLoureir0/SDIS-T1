package backingup.ipeer.action;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map.Entry;

import backingup.ipeer.database.Database;
import backingup.ipeer.database.FileDb;
import backingup.ipeer.protocol.ChunkBackup;

public class FileBackup {

	private int CHUNKSIZE = 64000;
	private HashMap<Integer,String> fileChunks;
	private String path;
	private boolean multiple;
	private String fileID;
	private Database db;
	private long fileLastModification;
	private String fileName;
	private int chunkNos;
	private int replicationDegree;
	private int mdbPort;
	private InetAddress mdbAddress;
	private int mcPort;
	private InetAddress mcAddress;
	
	
	public FileBackup(String path, int replicationDegree ,Database db, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.path = path;
		fileChunks = new HashMap<Integer, String>();
		this.multiple = false;
		this.db = db;
		this.fileID = "";
		this.fileLastModification = 0;
		this.fileName = "";
		this.chunkNos = 1;
		this.replicationDegree = replicationDegree;
		this.mcPort = mcPort;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcAddress = mcAddress;
	}
	
	public boolean backupFile() {
		
		try {
		    File file = new File(path);
		    FileInputStream fileStram = new FileInputStream(file);
		    byte[] dataBody = new byte[(int)file.length()];
		    fileStram.read(dataBody);
		    fileStram.close();
		    String fileBody = new String(dataBody, "UTF-8");
		    fileLastModification = file.lastModified();
		    fileName = file.getName();
		    
		    if (fileBody.length() % CHUNKSIZE == 0)
		    	this.multiple = true;
		    
		    generateFileID();
		    createFileChunks(fileBody);
		    updateDatabase();
		    return backupChunks();
	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void createFileChunks(String fileBody) {
	    String chunkBody = "";
	    int byteCounter = 0;
	    for(int i = 0; i < fileBody.length() ; i++) {
	    	
	    	if(byteCounter == CHUNKSIZE) {
	    		int chunkNo = i+1;
	    		fileChunks.put(chunkNo,chunkBody);
	    		chunkBody = new String("");
	    		byteCounter = 0;
	    		chunkNo++;
	    	}
	    	
	    	chunkBody += fileBody.charAt(i);
	    	byteCounter++;
	    }
	    
	    if(multiple) {
	    	int chunkNo = fileBody.length()+2;
	    	fileChunks.put(chunkNo,"");
	    	chunkNo++;
	    }
	}
	
	private void updateDatabase() {
		FileDb file = new FileDb(path,chunkNos);
		db.addFile(fileID, file);
	}
	
	private boolean backupChunks() {	

		boolean result = false;
		for (Entry<Integer, String> e : fileChunks.entrySet()) { 
			ChunkBackup cb = new ChunkBackup(fileID,e.getKey(),replicationDegree,e.getValue(),mdbPort,mdbAddress,mcPort,mcAddress);
			result = cb.backupChunk();
			
			if(!result)
				return false;
		}
		
		return true;
	}
	
	private void generateFileID() {
		String fileIdentification = fileName + fileLastModification;	
	    MessageDigest msg=null;
	    
	    try {
	    	msg = MessageDigest.getInstance("SHA-256");
	    	msg.update(fileIdentification.getBytes());

	        fileID = bytesToHexString(msg.digest());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
	
}
