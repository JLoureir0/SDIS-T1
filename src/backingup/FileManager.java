package backingup;

import java.io.File;
import java.io.FileInputStream;
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
        Path filePath = FileSystems.getDefault().getPath(path);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException | SecurityException e) {
        	e.printStackTrace();
        	return false;
        }
        return true;
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
	
	public String readFile() {
		String fileBody = ""; 
		try {
		    File file = new File(path);
		    FileInputStream fileStram = new FileInputStream(file);
		    byte[] dataBody = new byte[(int)file.length()];
		    fileStram.read(dataBody);
		    fileStram.close();
		    fileBody = new String(dataBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileBody;
	}
	
}
