package backingup.ipeer.action;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;

import org.junit.Test;

import backingup.Constants;
import backingup.ipeer.database.Database;

public class ActionTest {

	@Test
	public void testFileRestore() throws NoSuchMethodException, SecurityException {
	    Method method = FileRestore.class.getDeclaredMethod("changeFileContent");
	    method.setAccessible(true);
		String path = System.getProperty(Constants.CURRENT_DIR);
		String fileName = "testRestoreFile.txt";
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
			boolean aux = (boolean) method.invoke(fr);
			assertTrue(aux);
			//assertTrue(fr.changeFileContent());
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}

	@Test
	public void testFileDelete() throws NoSuchMethodException, SecurityException {
	    Method method = FileDelete.class.getDeclaredMethod("removeFileFromDir");
	    method.setAccessible(true);
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testDeleteFile.txt";
		int numberOfDeleteMessages = 1;
		String fileID = "file1";
		int mcPort = 54321;
		String address = "224.2.2.3";
		
		try {
			InetAddress mcAddress = InetAddress.getByName(address);
			FileDelete fd = new FileDelete(fileID,numberOfDeleteMessages,mcAddress, mcPort, path);
			boolean aux = (boolean) method.invoke(fd);
			assertTrue(aux);
			//assertTrue(fd.removeFileFromDir());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateFileID() throws NoSuchMethodException, SecurityException {
	    Method method = FileBackup.class.getDeclaredMethod("generateFileID");
	    method.setAccessible(true);
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testChunks.txt";
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
			method.invoke(fb);
			//fb.generateFileID();
			
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
			method.invoke(fb);
			//fb.updateDatabase();
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
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testChunks.txt";
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
			//fb.createFileChunks(fileBody);
			assertEquals(nChunks,fb.getChunkNos());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
} 
