package peer.database;

import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseTest {

	@Test
	public void testID() {
		ID id = new ID("id1", 1);
		assertEquals("id1",id.getFileID());
		assertEquals(1, id.getChunkNo());
		
		ID id1 = new ID("id1",1);
		ID id2 = new ID("dsada",1);
		ID id3 = new ID("id1",2);
		ID id4 = new ID("dasda",2);
		
		assertTrue(id.equals(id1));
		assertTrue(id1.equals(id));
		
		assertFalse(id.equals(id2));
		assertFalse(id.equals(id3));
		assertFalse(id.equals(id4));
	}
	
	@Test
	public void testChunk() {
		Chunk chunk = new Chunk(0, "dasdas");
		
		assertEquals(0, chunk.getReplicationDegree());
		assertEquals("dasdas", chunk.getChunkBody());
		assertEquals(0, chunk.getCount());
		
		chunk.increaseCount();
		assertEquals(1, chunk.getCount());
		
		chunk.resetCount();
		assertEquals(0, chunk.getCount());
	}
	
	@Test
	public void testDatabase() {
		Database db = new Database();
		
		db.addChunk("id1", 1, 9, "very_important_data");
		
		assertTrue(db.containsChunk("id1", 1));
		assertFalse(db.containsChunk("id2", 1));
		assertEquals(9,db.getReplicationDegree("id1",1));
		assertEquals("very_important_data", db.getChunkBody("id1", 1));
		assertEquals(0, db.getCount("id1",1));
		
		db.increaseCount("id1",1);
		assertEquals(1, db.getCount("id1",1));
		db.resetCount("id1",1);
		assertEquals(0, db.getCount("id1",1));
		db.increaseCount("id1",1);
		
		db.addChunk("id2", 1, 9, "cool_data");
		
		db.addChunk("id1", 1, 9, "swag_data");
		assertEquals(0, db.getCount("id1",1));
		assertEquals("swag_data", db.getChunkBody("id1", 1));
		
		db.addChunk("id1", 2, 9, "stupid_data");
		
		db.removeFile("id1");
		assertFalse(db.containsChunk("id1", 1));
		assertFalse(db.containsChunk("id1", 2));
		assertTrue(db.containsChunk("id2", 1));		
	}
}
