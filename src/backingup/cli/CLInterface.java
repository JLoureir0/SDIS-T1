package backingup.cli;

import java.util.Scanner;

import backingup.BackingUP;

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
		
	}
	
	private static void restoreFile() {
		// TODO Auto-generated method stub
		
	}

	private static void deleteFile() {
		// TODO Auto-generated method stub
		
	}
	
	private static void freeSpace() {
		// TODO Auto-generated method stub
		
	}
}
