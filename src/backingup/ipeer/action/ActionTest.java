package backingup.ipeer.action;

import static org.junit.Assert.*;

import java.io.File;
import java.net.InetAddress;

import org.junit.Test;

import backingup.ipeer.database.Database;

public class ActionTest {

	@Test
	public void testFileRestore() {
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testRestoreFile.txt";
		String fileContent = "Just an example of a text in order to test if fileRestore is working properly";
		Database db = new Database();
		int mdrPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		String fileID = "file1";
		
		try {
			InetAddress mdrAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			FileRestore fr = new FileRestore(fileID,1,mcPort,mdrPort,mcAddress,mdrAddress,db);
			fr.setPath(path);
			fr.setFileBody(fileContent);
			assertTrue(fr.changeFileContent());
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	@Test
	public void testFileDelete() {
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testDeleteFile.txt";
		int numberOfDeleteMessages = 1;
		String fileID = "file1";
		int mcPort = 54321;
		String address = "224.2.2.3";
		
		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			FileDelete fd = new FileDelete(fileID,numberOfDeleteMessages,mcAddress, mcPort, path);
			assertTrue(fd.removeFileFromDir());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateFileID() {
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testRestoreFile.txt";
		int replicationDegree = 2;
		Database db = new Database();
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		long fileLastModification = 0;
		String fileName = "";
		
		try {
			InetAddress mdbAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			File file = new File(path);
		    fileLastModification = file.lastModified();
		    fileName = file.getName();
			
			FileBackup fb = new FileBackup(path, replicationDegree, db, mdbPort, mdbAddress, mcPort, mcAddress);
			fb.setFileLastModification(fileLastModification);
			fb.setFileName(fileName);
			fb.generateFileID();
			
			System.out.println("Generated FileID: "+fb.getFileID());
			assertEquals(1,1);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testFileBackupUpdateDatabase() {
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testRestoreFile.txt";
		int replicationDegree = 2;
		Database db = new Database();
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		long fileLastModification = 0;
		String fileName = "";
		String fileID = "";
		
		try {
			InetAddress mdbAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			File file = new File(path);
		    fileLastModification = file.lastModified();
		    fileName = file.getName();
			
			FileBackup fb = new FileBackup(path, replicationDegree, db, mdbPort, mdbAddress, mcPort, mcAddress);
			fb.setFileLastModification(fileLastModification);
			fb.setFileName(fileName);
			fb.setChunkNos(3);
			fileID = fb.getFileID();
			fb.updateDatabase();
			db = fb.getDb();
			assertTrue(db.containsFile(fileID));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateFileChunks() {
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testRestoreFile.txt";
		String fileBody = "Just an example of a text in order to test if fileRestore is working properly";
		int replicationDegree = 2;
		Database db = new Database();
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		long fileLastModification = 0;
		String fileName = "";
		int fileSize = fileBody.length();
		
		int nChunks = (int) Math.ceil(fileSize);
		if(fileBody.length() % 64000 == 0)
			nChunks++;
		
		try {
			InetAddress mdbAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			File file = new File(path);
		    fileLastModification = file.lastModified();
		    fileName = file.getName();
			
			FileBackup fb = new FileBackup(path, replicationDegree, db, mdbPort, mdbAddress, mcPort, mcAddress);
			fb.setFileLastModification(fileLastModification);
			fb.setFileName(fileName);
			fb.setChunkNos(3);
			db = fb.getDb();
			fb.createFileChunks(fileBody);
			assertEquals(fb.getChunkNos(),nChunks);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
