package backingup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileManager {
	private String directory;
	private String path;
	private boolean append;
	
	public FileManager(String directory, String name) {
		this.directory = directory;
		this.path = directory + File.separator + name;
		this.append = false;
	}
	
	public FileManager(String path) {
		this.directory = "";
		this.path = path;
		this.append = false;
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
			FileOutputStream fileOS;
			if(append)
				fileOS = new FileOutputStream(path, true);
			else
				fileOS = new FileOutputStream(path);
            
            fileOS.write(body.getBytes(Constants.ENCODING));
            fileOS.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String read() {
		 byte[] buffer = new byte[Constants.CHUNKSIZE];
         String body = "";
         try {
        	 FileInputStream inputStream = new FileInputStream(path);
        	 
        	 int count;
        	 while((count=inputStream.read(buffer)) != -1) {
        		 body = new String(buffer,Constants.ENCODING);
				 body = body.substring(0, count);
        	 }
        	 
        	 inputStream.close();
         }
         catch(Exception e) {
        	 e.printStackTrace();
         }
         return body;
	}
	
	public boolean checkIfFileExists() {
		File f = new File(path);
		if(f.exists() && !f.isDirectory()) 
			return true;
		return false;
	}
	
	public void setAppend(boolean append) {
		this.append = append;
	}
}
