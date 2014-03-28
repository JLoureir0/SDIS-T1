package backingup.peer.database;

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
		Chunk chunk = new Chunk(0);
		
		assertEquals(0, chunk.getReplicationDegree());
		assertEquals(0, chunk.getCount());
		
		chunk.increaseCount();
		assertEquals(1, chunk.getCount());
		
		chunk.increaseCount();
		assertEquals(2, chunk.getCount());
		chunk.decreaseCount();
		assertEquals(1, chunk.getCount());
		
		chunk.resetCount();
		assertEquals(0, chunk.getCount());
	}
	
	@Test
	public void testDatabase() {
		Database db = new Database(100);
		
		assertEquals(100, db.getMaxSize());
		
		db.setMaxSize(19);
		db.addChunk("id1", 1, 9, "very_important_data");
		db.addChunk("id1", 2, 9, "very_important_data");
		assertTrue(db.containsChunk("id1", 1));
		assertFalse(db.containsChunk("id1", 2));
		
		db.setMaxSize(150);
		assertEquals(150, db.getMaxSize());
		
		db.addChunk("id1", 1, 9, "very_important_data");
		
		assertTrue(db.containsChunk("id1", 1));
		assertFalse(db.containsChunk("id2", 1));
		assertEquals(9,db.getReplicationDegree("id1",1));
		assertEquals("very_important_data", db.getChunkBody("id1", 1));
		assertEquals(0, db.getCount("id1",1));
		
		db.increaseCount("id1",1);
		assertEquals(1, db.getCount("id1",1));
		db.increaseCount("id1",1);
		assertEquals(2, db.getCount("id1",1));
		db.decreaseCount("id1", 1);
		assertEquals(1, db.getCount("id1",1));
		db.resetCount("id1",1);
		assertEquals(0, db.getCount("id1",1));
		db.increaseCount("id1",1);
		
		db.addChunk("id2", 1, 1, "cool_data");
		
		db.addChunk("id1", 1, 9, "swag_data");
		assertEquals(0, db.getCount("id1",1));
		assertEquals("swag_data", db.getChunkBody("id1", 1));
		
		db.addChunk("id1", 2, 9, "stupid_data");
		assertEquals(29, db.getSize());
		
		db.removeFile("id1");
		assertFalse(db.containsChunk("id1", 1));
		assertFalse(db.containsChunk("id1", 2));
		assertTrue(db.containsChunk("id2", 1));
		assertEquals(9,db.getSize());
		
		db.addChunk("id1", 1, 9, "important_data");
		db.increaseCount("id2", 1);
		db.increaseCount("id2", 1);
		
		ID id2 = db.removeChunk();
		assertEquals("id2", id2.getFileID());
		assertEquals(1, id2.getChunkNo());
		assertEquals(14, db.getSize());
		
		ID id1 = db.removeChunk();
		assertEquals("id1", id1.getFileID());
		assertEquals(1, id1.getChunkNo());
		assertEquals(0, db.getSize());
	}
}
