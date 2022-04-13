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

import ldb.dbitem.*;
import ldb.util.DBUtils;

public class Main {
	
	// Name of the DB file
	private static String DATABASE = "LibraryDB.db";
	
	private static List<DBItemController> initializeDBItemControllers() {
		List<DBItemController> dbItems = new ArrayList<>();
		ActorController actor = new ActorController();
		ArtistController artist = new ArtistController();
		AuthorController author = new AuthorController();
		AlbumController album = new AlbumController(artist);
		
		dbItems.add(new MovieController(actor));
		dbItems.add(new AlbumController(artist));
		dbItems.add(new TrackController(album));
		dbItems.add(new BookController(author));
		dbItems.add(actor);
		dbItems.add(artist);
		dbItems.add(author);
		dbItems.add(new PatronController());
		dbItems.add(new ReviewController());
		dbItems.add(new CheckoutController());
		dbItems.add(new ConditionController());
		
		return dbItems;
	}
	
	private static Map<Integer, UserOption> initializeOptionMap(List<DBItemController> dbItems) {
		Map<Integer, UserOption> optionMap = new HashMap<>();
		optionMap.put(1, new InsertionManager(dbItems));
		optionMap.put(2, new ModifyManager(dbItems));
		optionMap.put(3, new SearchManager(dbItems));
		optionMap.put(4, new OrderManager());
		optionMap.put(5, new ReportManager());
		optionMap.put(6, null);
		
		return optionMap;
	}
	
	public static void main (String[] args) 
	{
        Connection conn = initializeDB(DATABASE);
        Scanner s = new Scanner(System.in);   
        List<DBItemController> dbItems = initializeDBItemControllers();
        Map<Integer, UserOption> optionMap = initializeOptionMap(dbItems);       
        
        int userChoice = 0;
        
        do {
        	System.out.println("\nWhat would you like to do?");
        	System.out.println("1. Add a new record");
			System.out.println("2. Edit/Delete an existing record");
			System.out.println("3. Search for a record");
			System.out.println("4. Order an item");
			System.out.println("5. View useful reports");
			System.out.println("6. Exit program");
			userChoice = DBUtils.getValidInput(1, 6, s);
			
			UserOption option = optionMap.get(userChoice);
			
			if (option != null) {
				option.execute(conn, s);
			} else {
				System.out.println("Exiting program...");
			}
			
        } while (userChoice != 6);
        
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
	
}