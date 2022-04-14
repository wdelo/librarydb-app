import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ldb.*;
import ldb.dbitem.*;
import ldb.util.*;

public class Main {
	
	// Name of the DB file
	private static String DATABASE = "LibraryDB.db";
	
	public static void main (String[] args) 
	{
        Connection conn = initializeDB(DATABASE);
        Scanner in = new Scanner(System.in);   
        
        Menu mainMenu = createMainMenu();
        
        while (!mainMenu.isExited()) {        	
        	mainMenu.execute(conn, in);
        }
        System.out.println("Exiting...");
        try {
        	conn.close();
        	System.out.println("Connection closed successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("There was a problem closing the database.");
        }        
	}
	
	// Makes a connection to the database
	// Note: returns a null value if the connection fails
	public static Connection initializeDB(String databaseFileName) {
        String url = "jdbc:sqlite:" + databaseFileName;
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
            	// Provides some positive assurance the connection and/or creation was successful.
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("The connection to the database was successful.");
            } else {
            	// Provides some feedback in case the connection failed but did not throw an exception.
            	System.out.println("Null Connection");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("There was a problem connecting to the database.");
        }
        
        return conn;
    }
	
	private static Menu createMainMenu() {		
		UserOption[] mediaUserOptions = new UserOption[] { 
				MovieController::execute, 
				AlbumController::execute, 
				BookController::execute,
				null,
		};
		
		UserOption[] contributorUserOptions = new UserOption[] { 
				ActorController::execute, 
				ArtistController::execute,
				AuthorController::execute,
				DirectorController::execute,
				null,
		};
		
		UserOption[] adminUserOptions = new UserOption[] {
				PatronController::execute,
				CheckoutController::execute,
				null,
		};
		
		String mainMenuPrompt = "What would you like to do?";	
		String[] mainMenuScreenOptions = {
	    		"Manage Media", 
	    		"Manage Contributors", 
	    		"Manage Library Cards",
	    		"Manage Orders", 
	    		"View reports", 
	    		"Exit program",
		};
		
		String manageMenuPrompt = "What would you like to manage?";
		
		String[] mediaMenuScreenOptions = {
				"Manage movies",
				"Manage albums",
				"Manage audiobooks",
				"Back",
		};
		
		String[] contributorMenuScreenOptions = {
				"Manage actors",
				"Manage artists",
				"Manage authors",
				"Manage directors",
				"Back",
		};
		
		String[] adminMenuScreenOptions = {
				"Manage patrons",
				"Manage checkouts",
				"Back",
		};
		
		UserOption[] menuUserOptions = new UserOption[] {
				new Menu(mediaUserOptions, new MenuScreen(manageMenuPrompt, mediaMenuScreenOptions)),
				new Menu(contributorUserOptions, new MenuScreen(manageMenuPrompt, contributorMenuScreenOptions)),
				new Menu(adminUserOptions, new MenuScreen(manageMenuPrompt, adminMenuScreenOptions)),
				new OrderManager(),
				new ReportManager(),
		};
		
		return new Menu(menuUserOptions, new MenuScreen(mainMenuPrompt, mainMenuScreenOptions)); 
	}
}