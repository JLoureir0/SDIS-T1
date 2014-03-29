package ui;

import java.net.InetAddress;
import java.util.Scanner;

import backingup.FileManager;
import backingup.ipeer.action.FileBackup;
import backingup.ipeer.database.Database;
import backingup.peer.database.Database;

public class Ui {
	
	private static backingup.ipeer.database.Database ipeerDb;
	private static backingup.peer.database.Database peerDb;
	
	private static int mcPort;
	private static String address;
	private static int mdbPort;
	private static String address1;
	private static int mdrPort;
	private static String address2;
	
	private static InetAddress mcAddress;
	private static InetAddress mdbAddress;
	private static InetAddress mdrAddress;

    public static void main(String[] args) {
    	setAdresses();
    	//Create listeners
    	//Create SearchDeletedFiles Thread
    	//Portas ips dbsize
        mainMenu();
    }
    
    private static void setAdresses() {
    	try {
    		mcAddress = InetAddress.getByName(address);
    		mdbAddress = InetAddress.getByName(address1);
    		mdrAddress = InetAddress.getByName(address2);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    private static void initializaDatabases(int dbMaxSize) {
    	ipeerDb = new Database();
    	peerDb = new Database(dbMaxSize);
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
    	
    	switch (userOption) {
        case 1:  backupFile();
                 break;
        case 2:  restoreFile();
                 break;
        case 3:  deleteFile();
                 break;
        case 4:  freeSpace();
                 break;
        case 5:  System.exit(0);
                 break;
        default: System.out.println("Invalid Option");
        		 Thread.sleep(500);
                 break;
    	}
    }
    
    private static void backupFile() {	
    	String path = "";
    	int replicationDegree = 0;
    	while(true) {
        	Scanner keyboard = new Scanner(System.in);
        	System.out.println("Please insert the path of the file you want to backup: ");
        	path = keyboard.next();
        	
        	boolean fileExists = false;
        	FileManager fm = new FileManager(path);
        	fileExists = fm.CheckIfFileExists();
        	if(fileExists) 
        		break;
        	System.out.println("Error, incorrect file path!");
    	}
    	
    	while(true) {
        	Scanner keyboard = new Scanner(System.in);
        	System.out.print("Please insert the replication degree of the file: ");
        	replicationDegree = keyboard.nextInt();
        	
        	if(replicationDegree > 0)
        		break;
        	System.out.println("Error, invalid file replication degree!");
    	}
    
    	FileBackup fb = new FileBackup(path, replicationDegree, db, mdbPort, mdbAddress, mcPort, mcAddress);
    	boolean result = fb.backupFile();
    	if(result)
    		System.out.println("The file has been backup up sucessfully!");
    	else
    		System.out.println("It was not possible to backup the desired file, pleasy try again!");
    }
    
    
	
}
