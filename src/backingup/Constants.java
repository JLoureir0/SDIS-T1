package backingup;

import java.io.File;

public class Constants {
	public static final int ARRAY_SIZE = 512;
	public static final int HALF_A_SECOND = 500;
	public static final int SLEEP = 401;
	public static final String PUTCHUNK = "PUTCHUNK";
	public static final String STORED = "STORED";
	public static String GETCHUNK = "GETCHUNK";
	public static final String CHUNK = "CHUNK";
	public static String DELETE = "DELETE";
	public static final String REMOVED = "REMOVED";
	public static final String VERSION_1 = "1.0";
	public static final String CRLF = "CRLF";
	public static final String ENCODING = "US-ASCII";
	public static final String WHITESPACE_REGEX = "\\s";
	public static final String CURRENT_DIR = "user.dir";
	public static final String BACKUP_PATH = System.getProperty(CURRENT_DIR) + File.separator + "backup";
}
