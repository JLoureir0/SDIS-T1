package backingup.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import backingup.BackingUP;
import backingup.Constants;
import backingup.ipeer.database.Database;

public class CLInterface {
	
	private static BackingUP backingup;
	
	public static void main(String[] args) {
		mainMenu();
		
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
	    	Scanner keyboard = new Scanner(System.in);
	    	System.out.print("Please insert the desired option: ");
	    	int userOption = keyboard.nextInt();
	    	keyboard.close();
	    	
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
		 Scanner keyboard = new Scanner(System.in);
		 System.out.println("Please insert the path of the file you want to backup: ");
		 path = keyboard.next();
		 
		 while(true) {
			 System.out.print("Please insert the replication degree of the file: ");
			 replicationDegree = keyboard.nextInt();

			 
			if(replicationDegree > 0)
				break;
			System.out.println("Error, invalid file replication degree!");
	    }
	    
    	boolean result = backingup.backupFile(path, replicationDegree);
    	if(result)
    		System.out.println("The file has been backup up sucessfully!");
    	else
    		System.out.println("It was not possible to backup the desired file, pleasy try again!");
    	keyboard.close();
	 }
	
	private static void restoreFile() {
		String fileID = printFilesInDatabase();
		backingup.restoreFile(fileID);
	}

	private static void deleteFile() {
		String fileID = printFilesInDatabase();
		Database db = new Database();
		db = backingup.getIpeerDB();
		String path = db.getFilePath(fileID);
		backingup.deleteFile(fileID, path, Constants.NUMBER_OF_DELETED_MESSAGES);
	}
	
	private static void freeSpace() {		
		 int newSize = 0;
		 Scanner keyboard = new Scanner(System.in);
		 
		 while(true) {
			 System.out.print("Please insert the replication degree of the file: ");
			 newSize = keyboard.nextInt();

			 
			if(newSize > 0)
				break;
			System.out.println("Error, invalid file replication degree!");
	    }
	    
    	boolean result = backingup.freeSpace(newSize);
    	if(result)
    		System.out.println("The file has been backup up sucessfully!");
    	else
    		System.out.println("It was not possible to backup the desired file, pleasy try again!");
    	keyboard.close();
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
		    System.out.println("Choose the file you want:");
		    while (it.hasNext()) {
		        Map.Entry<String, backingup.ipeer.database.File> file = (Map.Entry<String, backingup.ipeer.database.File>)it.next();
		        String fileID = (String) file.getKey();
		        fileIDs.add(fileID);
		        File f = new File(db.getFilePath(fileID));
		        String fileName = f.getName();
		        System.out.println(iteration + " -> " + fileName);
		        it.remove();
		        iteration++;
		    }
		    
	    	Scanner keyboard = new Scanner(System.in);
	    	int userOption = keyboard.nextInt();
	    	keyboard.close();
	    	
	    	if(userOption >= 1 && userOption <= iteration) {
	    		index = userOption--;
	    		break;
	    	}
	    	
	    	System.out.println("Error! Invalid option, please try again!");
		}
		
		return fileIDs.get(index);	
	}
}
