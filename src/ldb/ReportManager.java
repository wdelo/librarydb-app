package ldb;


import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.*;


public class ReportManager implements UserOption {
    
	// Constructor
	public ReportManager() {}
	
	/**
	 *  The database file name.
	 *  
	 *  Make sure the database file is in the root folder of the project if you only provide the name and extension.
	 *  
	 *  Otherwise, you will need to provide an absolute path from your C: drive or a relative path from the folder this class is in.
	 */
	private static String DATABASE = "Media_DB.db";
	
	
	/**
	 *  The query statement to be executed.
	 *  
	 *  Remember to include the semicolon at the end of the statement string.
	 *  (Not all programming languages and/or packages require the semicolon (e.g., Python's SQLite3 library))
	 */
	private static String sqlStatement = "SELECT Media.Title as Title" + 
			"FROM Patron, Media, Movie, Checkout, Media_Instance" + 
			"WHERE Patron.Email_Address = ?" + 
			"AND Checkout.Email_Address = Patron.Email_Address" + 
			"AND Checkout.Call_number = Media_Instance.Call_number" + 
			"AND Media_Instance.MediaID = Media.MediaID;";
	
	private static String sqlStatement2 = "SELECT Name, count(MI.Call_Number) AS Associated_Checkouts " + 
			"From Media NATURAL JOIN Media_Instance AS MI NATURAL JOIN Checkout AS C NATURAL JOIN Contributes_To AS CT NATURAL JOIN Contributor " + 
			"Where Role = 'Actor' " + 
			"GROUP BY Name " + 
			"Order BY count(MI.Call_Number) DESC " + 
			"LIMIT 1; ";
	
	private static String sqlStatement3 = "SELECT Name, SUM(Total_Listens) as Artist_Listens " + 
			"FROM Contributor as C, Contributes_To as CT, " + 
			"(SELECT M.MediaID, SUM(Length) as Total_Listens " + 
			"FROM Media as M, Audio as A, Track as T, Media_Instance as MI, Checkout as C " + 
			"WHERE A.[Album/Audiobook] = true " + 
			"AND T.AudioID = A.AudioID " + 
			"AND M.MediaID = A.AudioID " + 
			"AND M.MediaID = MI.MediaID " + 
			"AND C.Call_Number = MI.Call_Number " + 
			"GROUP BY M.MediaID) as Album_Listens " + 
			"WHERE C.ContributorID = CT.ContributorID " + 
			"AND C.PrimaryRole='Artist' " + 
			"AND CT.ContributorID = C.ContributorID " + 
			"AND CT.MediaID = Album_Listens.MediaID " + 
			"GROUP BY Name " + 
			"ORDER BY Artist_Listens DESC " + 
			"LIMIT 1;";
	private static String sqlStatement4 = "SELECT Name, SUM(Total_Listens) as Author_Listens" + 
			"FROM Contributor as C, Contributes_To as CT," + 
			"(SELECT M.MediaID, SUM(Length) as Total_Listens" + 
			"FROM Media as M, Audio as A, Track as T, Media_Instance as MI, Checkout as C" + 
			"WHERE A.[Album/Audiobook] = false" + 
			"AND T.AudioID = A.AudioID" + 
			"AND M.MediaID = A.AudioID" + 
			"AND M.MediaID = MI.MediaID" + 
			"AND C.Call_Number = MI.Call_Number" + 
			"GROUP BY M.MediaID) as Album_Listens" + 
			"WHERE C.ContributorID = CT.ContributorID" + 
			"AND C.PrimaryRole='Auther'" + 
			"AND CT.ContributorID = C.ContributorID" + 
			"AND CT.MediaID = Album_Listens.MediaID" + 
			"GROUP BY Name" + 
			"ORDER BY Author_Listens DESC" + 
			"LIMIT 1;";
	
	private static String sqlStatement5 = "SELECT Media.Title as MovieTitle, Checkout.Checkout_date" + 
			"FROM Media, Movie, Checkout, Media_Instance" + 
			"WHERE Checkout.Call_number = Media_Instance.Call_number" + 
			"AND Media_Instance.MediaID = Media.MediaID" + 
			"AND Media.MediaID = Movie.MovieID;";
	
	private static String sqlStatement6 = "Select C.Name, M.Title, T.Track_Title" + 
			"FROM Contributor as C, Contributes_To as CT, Media as M, Audio as A, Track as T" + 
			"WHERE M.MediaID = CT.MediaID" + 
			"AND C.ContributorID = CT.ContributorID" + 
			"AND A.AudioID = M.MediaID" + 
			"AND A.[Album/Audiobook] = true" + 
			"AND T.AudioID = A.AudioID" + 
			"AND C.Name = ?" + 
			"AND M.Year < ?;";
	
    /**
     * Connects to the database if it exists, creates it if it does not, and returns the connection object.
     * 
     * @param databaseFileName the database file name
     * @return a connection object to the designated database
     */
    public static Connection initializeDB(String databaseFileName) {
    	/**
    	 * The "Connection String" or "Connection URL".
    	 * 
    	 * "jdbc:sqlite:" is the "subprotocol".
    	 * (If this were a SQL Server database it would be "jdbc:sqlserver:".)
    	 */
        String url = "jdbc:sqlite:" + databaseFileName;
        Connection conn = null; // If you create this variable inside the Try block it will be out of scope
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
    

    public static void sqlQueryBasicSelect(Connection conn, String sql){
        try {
        	Statement stmt = conn.createStatement();
        	ResultSet rs = stmt.executeQuery(sql);
        	ResultSetMetaData rsmd = rs.getMetaData();
        	int columnCount = rsmd.getColumnCount();
        	for (int i = 1; i <= columnCount; i++) {
        		String value = rsmd.getColumnName(i);
        		System.out.print(value);
        		if (i < columnCount) System.out.print(",  ");
        	}
			System.out.print("\n");
        	while (rs.next()) {
        		for (int i = 1; i <= columnCount; i++) {
        			String columnValue = rs.getString(i);
            		System.out.print(columnValue);
            		if (i < columnCount) System.out.print(",  ");
        		}
    			System.out.print("\n");
        	}
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void sqlQueryPatronCheckouts(Connection conn, String sql, String email){
        try {
        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, email);
        	ResultSet rs = stmt.executeQuery();
        	ResultSetMetaData rsmd = rs.getMetaData();
        	int columnCount = rsmd.getColumnCount();
        	for (int i = 1; i <= columnCount; i++) {
        		String value = rsmd.getColumnName(i);
        		System.out.print(value);
        		if (i < columnCount) System.out.print(",  ");
        	}
			System.out.print("\n");
        	while (rs.next()) {
        		for (int i = 1; i <= columnCount; i++) {
        			String columnValue = rs.getString(i);
            		System.out.print(columnValue);
            		if (i < columnCount) System.out.print(",  ");
        		}
    			System.out.print("\n");
        	}
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static void sqlQueryArtistTracks(Connection conn, String sql, String name, String year){
        try {
        	PreparedStatement stmt = conn.prepareStatement(sql);
        	stmt.setString(1, name);
        	stmt.setString(2, year);
        	ResultSet rs = stmt.executeQuery();
        	ResultSetMetaData rsmd = rs.getMetaData();
        	int columnCount = rsmd.getColumnCount();
        	for (int i = 1; i <= columnCount; i++) {
        		String value = rsmd.getColumnName(i);
        		System.out.print(value);
        		if (i < columnCount) System.out.print(",  ");
        	}
			System.out.print("\n");
        	while (rs.next()) {
        		for (int i = 1; i <= columnCount; i++) {
        			String columnValue = rs.getString(i);
            		System.out.print(columnValue);
            		if (i < columnCount) System.out.print(",  ");
        		}
    			System.out.print("\n");
        	}
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void execute(Connection conn, Scanner s) {
    	System.out.println("*********************************************************************");
    	System.out.println("Select A Popular Report");
    	System.out.println("1. Show Checkouts");
    	System.out.println("2. Show Most Popular Actor");
    	System.out.println("3. Show Most Popular Artist");
    	System.out.println("4. Show Most Popular Author");
    	System.out.println("5. Show Movies Checked Out");
    	System.out.println("6. Show Tracks by Artist before Year");
    	System.out.print("Enter Number Selection: ");
		int selection = 0;
		while(selection < 1 || selection > 6) {
			if(s.hasNextInt()) {
				selection = s.nextInt();
			}
			if(selection == 1) {
				System.out.println("Enter Patron Email: ");
				String email = s.next();
				sqlQueryPatronCheckouts(conn, sqlStatement, email);
			}else if(selection == 2) {
				sqlQueryBasicSelect(conn, sqlStatement2);
			}else if(selection == 3){
				sqlQueryBasicSelect(conn, sqlStatement3);
			}else if(selection == 4){
				sqlQueryBasicSelect(conn, sqlStatement4);
			}else if(selection == 5){
				sqlQueryBasicSelect(conn, sqlStatement5);
			}else if(selection == 6){
				System.out.println("Enter Artist Name: ");
				String name = s.next();
				String year = "";
				while(year.length() != 4) {
				System.out.println("Enter Year: ");
				year = s.next();
				if(year.length() != 4) {
					System.out.println("Error - Invalid Year Length");
				}				
				}
				sqlQueryArtistTracks(conn, sqlStatement6, name, year);
			}else {
				System.out.println("Error - Invalid Entry");
				System.out.print("Enter Number Selection: ");
			}
		}
    }
}

