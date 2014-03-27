package backingup.ipeer.action;

import static org.junit.Assert.*;

import java.net.InetAddress;

import org.junit.Test;

import backingup.ipeer.database.Database;

public class ActionTest {

	@Test
	public void testFileRestore() {
		String path = "C:\\Users\\Daniel Moreira\\Documents\\workspace\\sdisProject\\src\\backingup\\ipeer\\action\\testFile.txt";
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

}
