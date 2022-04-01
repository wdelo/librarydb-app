

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
	
	// Name of the DB file
	private static String DATABASE = "Media_DB4.db";
	
	public static void main (String[] args) 
	{
        Connection conn = initializeDB(DATABASE);
        Scanner s = new Scanner(System.in);
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
			
			switch (userChoice) {
			case 1:
				InsertionManager.userInsert(conn, s);
				break;
			case 2:
				ModifyManager.userModify(conn, s);
				break;
			case 3:
				SearchManager.userSearch(conn, s);
				break;
			case 4:
				OrderManager.createOrReceiveOrder(conn, s);
				break;
			case 5:
				PopularQueries.menu(conn, s);
				break;
			case 6:
				System.out.println("Exiting program...");
				break;
			default:
				break;
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



