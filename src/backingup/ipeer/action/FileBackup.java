package backingup.ipeer.action;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.MessageDigest;

import backingup.ipeer.database.Database;
import backingup.ipeer.protocol.ChunkBackup;

public class FileBackup {

	private int CHUNKSIZE = 64000;
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
	private String dirPath;
	
	public FileBackup(String dirPath, String fileName, int replicationDegree ,Database db, int mdbPort, InetAddress mdbAddress, int mcPort, InetAddress mcAddress) {
		this.dirPath = dirPath;
		this.multiple = false;
		this.db = db;
		this.fileID = "";
		this.fileLastModification = 0;
		this.fileName = fileName;
		this.chunkNos = 0;
		this.replicationDegree = replicationDegree;
		this.mcPort = mcPort;
		this.mdbPort = mdbPort;
		this.mdbAddress = mdbAddress;
		this.mcAddress = mcAddress;
		this.path = this.dirPath + File.separator + this.fileName;
	}
	
	public boolean backupFile() { 
		try {
		    File file = new File(path);
		    FileInputStream fileStram = new FileInputStream(file);
		    byte[] dataBody = new byte[(int)file.length()];
		    fileStram.read(dataBody);
		    fileStram.close();
		    String fileBody = new String(dataBody);
		    fileLastModification = file.lastModified();
		    fileName = file.getName();
		    
		    if (fileBody.length() % CHUNKSIZE == 0)
		    	this.multiple = true;
		    
		    generateFileID();
		    createFileChunks(fileBody);
		    updateDatabase();

		    return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false; 
	}
	
	private void createFileChunks(String fileBody) {
		@SuppressWarnings("unused")
		int nRead = 0;
		String chunkBody = "";
		byte[] buffer = new byte[CHUNKSIZE];
		
		try {
			 FileInputStream inputStream = new FileInputStream(path);
			 while((nRead = inputStream.read(buffer)) != -1) {
				 chunkBody = new String(buffer);
				 chunkBody = chunkBody.trim();
	             backupChunk(chunkNos, chunkBody);
				 chunkNos++;
				 System.out.println("ChunkNos: " + chunkNos);
	         }	
			 inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    if(multiple) {
	    	chunkBody = "";
	    	backupChunk(chunkNos, chunkBody);
	    	chunkNos++;
	    }
	}
	
	private void updateDatabase() {
		db.addFile(fileID, path, chunkNos);
	}
	
	private boolean backupChunk(int chunkNo, String chunkBody) {
		ChunkBackup cb = new ChunkBackup(fileID, chunkNo, replicationDegree, chunkBody, mdbPort, mdbAddress, mcPort, mcAddress);
		return cb.backupChunk();
	}
	
	private void generateFileID() {
		String fileIdentification = fileName + fileLastModification;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			fileID = bytesToHexString(digest.digest(fileIdentification.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
        	sb.append(Integer.toHexString(0xFF & bytes[i]));
        }
        return sb.toString();
    }
    
	public void setFileLastModification(long fileLastModification) {
		this.fileLastModification = fileLastModification;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileID() {
		return fileID;
	}

	public int getChunkNos() {
		return chunkNos;
	}

	public void setChunkNos(int chunkNos) {
		this.chunkNos = chunkNos;
	}

	public Database getDb() {
		return db;
	}
	
}
