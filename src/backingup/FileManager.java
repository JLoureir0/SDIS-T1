package backingup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileManager {
	private String path;
	
	public FileManager(String path) {
		this.path = path;
	}
	
	public boolean deleteFile() {
		try{			 
    		File file = new File(path);
    		if(file.delete())
    			return true; 
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
	}
	
	public boolean writeFile(String body) {
		try {
			File file = new File(Constants.BACKUP_PATH);
			file.mkdir();
            FileOutputStream fileOS = new FileOutputStream(path);
            fileOS.write(body.getBytes());
            fileOS.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String readFile() {
		 byte[] buffer = new byte[Constants.ARRAY_SIZE];
         String body = "";
         try {
        	 FileInputStream inputStream = new FileInputStream(path);
        	 
        	 while(inputStream.read(buffer) != -1) {
        		 body += new String(buffer).trim();
        	 }
        	 
        	 inputStream.close();
         }
         catch(Exception e) {
        	 e.printStackTrace();
         }
         return body;
	}
}
