package backingup;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class FileManagerTest {

	@Test
	public void testFileManager() {
		
		String path = System.getProperty(Constants.CURRENT_DIR) + File.separator + "backup" + File.separator + "oi.txt";
		String body = "oi tudo bom?"; 
		FileManager fm = new FileManager(path);
		
		assertTrue(fm.writeFile(body));
		File f = new File(path);
		assertEquals(12, f.length());
		assertTrue(f.exists());
		
		assertEquals(body, fm.readFile());
		assertTrue(fm.deleteFile());
		assertFalse(f.exists());
	}
}