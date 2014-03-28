package backingup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileManager {
	private String directory;
	private String path;
	
	public FileManager(String directory, String name) {
		this.directory = directory;
		path = directory + File.separator + name;
	}
	
	public boolean delete() {
		try{			 
    		File file = new File(path);
    		if(file.delete())
    			return true; 
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
	}
	
	public boolean write(String body) {
		try {
			File file = new File(directory);
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
	
	public String read() {
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
