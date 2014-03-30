package backingup.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import backingup.BackingUP;
import backingup.Constants;
import backingup.FileManager;
import backingup.ipeer.database.Database;

public class CLInterface {
	
	private static BackingUP backingup;
	private static Scanner keyboard;
	
	public static void main(String[] args) {
		loadBackingup();
		keyboard = new Scanner(System.in);
		if(parseArgs(args))
			mainMenu();
		else
			System.out.println("Usage: mcPort mcAddress mdbPort mdbAddress mdrPort mdrAddress databaseSize");
	}
	
	private static boolean parseArgs(String[] args) {
		if(args.length == 7) {
			int mcPort = Integer.parseInt(args[0]); int mdbPort = Integer.parseInt(args[2]); int mdrPort = Integer.parseInt(args[4]);
			String mcAddress = args[1]; String mdbAddress = args[3]; String mdrAddress = args[5];
			int databaseSize = Integer.parseInt(args[6]);
			if(invalidPort(mcPort) || invalidPort(mdbPort) || invalidPort(mdrPort))
				return false;
			if(invalidAddress(mcAddress) || invalidAddress(mdbAddress) || invalidAddress(mdrAddress))
				return false;
			backingup = new BackingUP(mcPort, mcAddress, mdbPort, mdbAddress, mdrPort, mdrAddress, databaseSize);
			return true;
		}
		return false;
	}
	
	private static boolean invalidPort(int port) {
		if(port < 1000 || port > 65535)
			return true;
		return false;
	}
	
	private static boolean invalidAddress(String address) {
		String[] range = address.split(Constants.DOT_REGEX);
		if(range.length != 4)
			return true;
		
		int first = Integer.parseInt(range[0]);
		int second = Integer.parseInt(range[1]);
		int third = Integer.parseInt(range[2]);
		int fourth = Integer.parseInt(range[3]);
		
		//The multicast addresses are in the range 224.0.0.0 through 239.255.255.255
		if(first < 224 || first > 239)
			return true;
		if(second < 0 || second > 255)
			return true;
		if(third < 0 || third > 255)
			return true;
		if(fourth < 0 || fourth > 255)
			return true;
		return false;
	}
	
	private static void mainMenu() {
		while(true) {
    		printMainMenu();
    		getMainMenuChoice();
    	}
	}
	
	 private static void printMainMenu() {
	    	System.out.println("*******************************************************************************");
	    	System.out.println("                                   MENU                                        ");
	    	System.out.println("*******************************************************************************");
	    	System.out.println();
	    	System.out.println("1 -> Backup a File");
	    	System.out.println("2 -> Restore a File");
	    	System.out.println("3 -> Delete a File");
	    	System.out.println("4 -> Free space");
	    	System.out.println("5 -> Exit");
	 }
	 
	 private static void getMainMenuChoice() {
	    	System.out.print("Please insert the desired option: ");
	    	int userOption = Integer.parseInt(keyboard.nextLine());
	    	
	    	switch (userOption) {
	        case 1:
	        	backupFile();
	        	break;
	        case 2:
	        	restoreFile();
	        	break;
	        case 3:
	        	deleteFile();
	        	break;
	        case 4:
	        	freeSpace();
	        	break;
	        case 5:
	        	saveBackingUp();
	        	try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	System.exit(0);
	        	break;
	        default: 
	        	System.out.println("Invalid Option");
	        	break;
	    	}
	 }

	 private static void backupFile() {
		 String path = "";
		 int replicationDegree = 0;
		 System.out.print("Please insert the path of the file you want to backup: ");
		 path = keyboard.nextLine();
		 
		 while(true) {
			 System.out.print("Please insert the replication degree of the file: ");
			 replicationDegree = Integer.parseInt(keyboard.nextLine());
			 
			if(replicationDegree > 0)
				break;
			System.out.println("Error, invalid file replication degree!");
	    }
	    
    	boolean result = backingup.backupFile(path, replicationDegree);
    	if(result)
    		System.out.println("The file has been backup up sucessfully!");
    	else
    		System.out.println("It was not possible to backup the desired file, please try again!");
	 }
	
	private static void restoreFile() {
		String fileID = printFilesInDatabase();
		boolean result = backingup.restoreFile(fileID);
		if(result)
			System.out.println("The file has been restored sucessfully");
		else
			System.out.println("It was not possible to restore the desired file, please try again!");
	}

	private static void deleteFile() {
		String fileID = printFilesInDatabase();
		Database db = new Database();
		db = backingup.getIpeerDB();
		String path = db.getFilePath(fileID);
		boolean result = backingup.deleteFile(fileID, path, Constants.NUMBER_OF_DELETED_MESSAGES);
		if(result)
			System.out.println("The file has been deleted sucessfully");
		else
			System.out.println("It was not possible to delete the desired file, please try again!");
	}
	
	private static void freeSpace() {		
		 int newSize = 0;
		 
		 while(true) {
			 System.out.print("Please insert new database size: ");
			 newSize = Integer.parseInt(keyboard.nextLine());

			 
			if(newSize >= 0)
				break;
			System.out.println("Error, invalid database size!");
	    }
	    
    	boolean result = backingup.freeSpace(newSize);
    	if(result)
    		System.out.println("The Database has been updated successfully!");
    	else
    		System.out.println("It was not possible to update Database size!");
	}
	
	private static String printFilesInDatabase() {
		
		Database db = new Database();
		db = backingup.getIpeerDB();
		HashMap<String, backingup.ipeer.database.File> files = db.getFiles();
		ArrayList<String> fileIDs = new ArrayList<String> ();
		int index = 0;
		
		while(true) {
			fileIDs.clear();
		    Iterator<Entry<String, backingup.ipeer.database.File>> it = files.entrySet().iterator();
		    int iteration = 1;
		    while (it.hasNext()) {
		        Map.Entry<String, backingup.ipeer.database.File> file = (Map.Entry<String, backingup.ipeer.database.File>)it.next();
		        String fileID = (String) file.getKey();
		        fileIDs.add(fileID);
		        File f = new File(((backingup.ipeer.database.File)file.getValue()).getPath());
		        String fileName = f.getName();
		        System.out.println(iteration + " -> " + fileName);
		        iteration++;
		    }
		    System.out.print("Choose the file you want: ");
		    
	    	int userOption = Integer.parseInt(keyboard.nextLine());
	    	
	    	if(userOption >= 1 && userOption <= iteration) {
	    		index = userOption-1;
	    		break;
	    	}
	    	
	    	System.out.println("Error! Invalid option, please try again!");
		}
		
		return fileIDs.get(index);	
	}
	
	private static void saveBackingUp() {
		File file = new File(Constants.SAVE_PATH);
		file.mkdir();
		String path = Constants.SAVE_PATH + File.separator + "save.ser";
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
			os.writeObject(backingup);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void loadBackingup() {
		String path = Constants.SAVE_PATH + File.separator + "save.ser";
		FileManager fm = new FileManager(path);
		boolean exists = fm.checkIfFileExists();
		if(!exists)
			return;
		
		try {
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
			backingup = (BackingUP) is.readObject();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

