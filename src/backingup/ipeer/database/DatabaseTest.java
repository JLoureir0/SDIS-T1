package backingup.ipeer.database;

import static org.junit.Assert.*;

import org.junit.Test;

public class DatabaseTest {

	@Test
	public void testFile() {
		File f1 = new File("/cenas",10);
		assertEquals("/cenas",f1.getPath());
		assertEquals(10,f1.getChunkNos());
				
	}
	
	@Test
	public void testDatabase() {
		Database db = new Database();
		File f1 = new File("/cenas",10);
		
		db.addFile("abc",f1);
		assertTrue(db.containsFile("abc"));
		assertEquals("/cenas", db.getFilePath("abc"));
		assertEquals(10, db.getFileChunkNos("abc"));
		
		db.removeFile("abc");
		assertFalse(db.containsFile("abc"));
	}
	

}
