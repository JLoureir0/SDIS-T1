package backingup.cli;

import java.util.Scanner;

import backingup.BackingUP;

public class CLInterface {
	
	private static BackingUP backingup;
	
	public static void main(String[] args) {
		if(parseArgs(args))
			mainMenu();
		else
			System.out.println("Usage: backingup mcPort mcAddress mdbPort mdbAddress mdrPort mdrAddress databaseSize");
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
		if(port < 1000 && port > 65535)
			return true;
		return false;
	}
	
	private static boolean invalidAddress(String address) {
		String[] range = address.split(".");
		if(range.length != 4)
			return true;
		
		int first = Integer.getInteger(range[0]);
		int second = Integer.getInteger(range[1]);
		int third = Integer.getInteger(range[2]);
		int fourth = Integer.getInteger(range[3]);
		
		//The multicast addresses are in the range 224.0.0.0 through 239.255.255.255
		if(first < 224 && first > 239)
			return true;
		if(second < 0 && second > 255)
			return true;
		if(third < 0 && third > 255)
			return true;
		if(fourth < 0 && fourth > 255)
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
		// TODO Auto-generated method stub
		
	}

	private static void deleteFile() {
		// TODO Auto-generated method stub
		
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
}
