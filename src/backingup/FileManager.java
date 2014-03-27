package backingup;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {

	String path;
	
	public FileManager(String path) {
		this.path = path;
	}
	
	public boolean deleteFile() {
		boolean success = false;
        Path filePath = FileSystems.getDefault().getPath(path);

        try {
            success = Files.deleteIfExists(filePath);
        } catch (IOException | SecurityException e) {
        	e.printStackTrace();
        	success = false;
        }
        return success;
	}
	
	public boolean writeFile(String fileBody) {
		try {
            FileOutputStream fileOS = new FileOutputStream(path);
            fileOS.write(fileBody.getBytes());
            fileOS.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
