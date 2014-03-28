package backingup.ipeer.action;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;

import org.junit.Test;

import backingup.Constants;
import backingup.FileManager;
import backingup.ipeer.database.Database;

public class ActionTest {

	@Test
	public void testFileRestore() throws NoSuchMethodException, SecurityException {
	    Method method = FileRestore.class.getDeclaredMethod("writeChunk", String.class);
	    method.setAccessible(true);
		String dirPath = System.getProperty(Constants.CURRENT_DIR);
		dirPath += "\\src\\backingup\\ipeer\\action"; 
		String fileName = "testRestoreFile.txt";
		String path = dirPath + File.separator + fileName;
		String fileContent = "Just an example ";
		String fileContent1 = "of a text in order ";
		String fileContent2 = "to test ";
		String fileContent3 = "if fileRestore ";
		String fileContent4 = "is working properly";
		Database db = new Database();
		int mdrPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		String fileID = "file1";
		
		try {
			InetAddress mdrAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			FileRestore fr = new FileRestore(fileID,mcPort,mdrPort,mcAddress,mdrAddress,db);
			fr.setPath(path);
			boolean aux = (boolean) method.invoke(fr,fileContent);
			assertTrue(aux);
			aux = (boolean) method.invoke(fr,fileContent);
			assertTrue(aux);
			aux = (boolean) method.invoke(fr,fileContent1);
			assertTrue(aux);
			aux = (boolean) method.invoke(fr,fileContent2);
			assertTrue(aux);
			aux = (boolean) method.invoke(fr,fileContent3);
			assertTrue(aux);
			aux = (boolean) method.invoke(fr,fileContent4);
			assertTrue(aux);
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}

	@Test
	public void testFileDelete() throws NoSuchMethodException, SecurityException {
		createFileBeforeBeingDeleted();
	    Method method = FileDelete.class.getDeclaredMethod("removeFileFromDir");
	    method.setAccessible(true);
		String dirPath = System.getProperty(Constants.CURRENT_DIR);
		dirPath += "\\src\\backingup\\ipeer\\action"; 
		String fileName = "testDeleteFile.txt";
		String path = dirPath + File.separator + fileName;
		int numberOfDeleteMessages = 1;
		String fileID = "file1";
		int mcPort = 54321;
		String address = "224.2.2.3";
		Database db = new Database();
		db.addFile(fileID, path, 2);
		
		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			FileDelete fd = new FileDelete(fileID,numberOfDeleteMessages,mcAddress, mcPort, path, db);
			boolean aux = (boolean) method.invoke(fd);
			assertTrue(aux);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createFileBeforeBeingDeleted() {
		String dirPath = System.getProperty(Constants.CURRENT_DIR);
		dirPath += "\\src\\backingup\\ipeer\\action"; 
		String fileName = "testDeleteFile.txt";
		String path = dirPath + File.separator + fileName;
		String fileBody = "Just a file to test FileDelete";
		FileManager fm = new FileManager(path);
		fm.write(fileBody);
	}
	
	@Test
	public void testCreateFileID() throws NoSuchMethodException, SecurityException {
	    Method method = FileBackup.class.getDeclaredMethod("generateFileID");
	    method.setAccessible(true);
		String dirPath = System.getProperty(Constants.CURRENT_DIR);
		dirPath += "\\src\\backingup\\ipeer\\action"; 
		String fileName = "testChunks.txt";
		String path = dirPath + File.separator + fileName;
		int replicationDegree = 2;
		Database db = new Database();
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		long fileLastModification = 0;
		
		try {
			InetAddress mdbAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
			File file = new File(path);
		    fileLastModification = file.lastModified();	    
			
			FileBackup fb = new FileBackup(path, replicationDegree, db, mdbPort, mdbAddress, mcPort, mcAddress);
			fb.setFileLastModification(fileLastModification);
			fb.setFileName(fileName);
			method.invoke(fb);
			
			System.out.println("Generated FileID: " + fb.getFileID());			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testFileBackupUpdateDatabase() throws NoSuchMethodException, SecurityException {
	    Method method = FileBackup.class.getDeclaredMethod("updateDatabase");
	    method.setAccessible(true);
		String dirPath = System.getProperty(Constants.CURRENT_DIR);
		dirPath += "\\src\\backingup\\ipeer\\action"; 
		String fileName = "testRestoreFile.txt";
		String path = dirPath + File.separator + fileName;
		int replicationDegree = 2;
		Database db = new Database();
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		long fileLastModification = 0;
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
			method.invoke(fb);
			
			db = fb.getDb();
			assertTrue(db.containsFile(fileID));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateFileChunks() throws NoSuchMethodException, SecurityException {
	    Method method = FileBackup.class.getDeclaredMethod("createFileChunks", String.class);
	    method.setAccessible(true);
		String dirPath = System.getProperty(Constants.CURRENT_DIR);
		dirPath += "\\src\\backingup\\ipeer\\action"; 
		String fileName = "testChunks.txt";
		String path = dirPath + File.separator + fileName;
		int replicationDegree = 2;
		Database db = new Database();
		int mdbPort = 64321;
		String address = "224.2.2.5";
		int mcPort = 54321;
		String address1 = "224.2.2.3";
		long fileLastModification = 0;
		
		try {
			InetAddress mdbAddress = InetAddress.getByName(address);
			InetAddress mcAddress = InetAddress.getByName(address1);
			
		    File file = new File(path);
		    FileInputStream fileStram = new FileInputStream(file);
		    byte[] dataBody = new byte[(int)file.length()];
		    fileStram.read(dataBody);
		    fileStram.close();
		    String fileBody = new String(dataBody);
			
			double fileSize = fileBody.length()/64000.0;
			int nChunks = (int) Math.ceil(fileSize);
			if(fileBody.length() % 64000 == 0)
				nChunks++;
			
		    fileLastModification = file.lastModified();
		    fileName = file.getName();
			
			FileBackup fb = new FileBackup(path, replicationDegree, db, mdbPort, mdbAddress, mcPort, mcAddress);
			fb.setFileLastModification(fileLastModification);
			fb.setFileName(fileName);
			db = fb.getDb();
			method.invoke(fb,fileBody);

			assertEquals(nChunks,fb.getChunkNos());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
} 
