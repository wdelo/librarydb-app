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
	 *  The query statement to be executed.
	 *  
	 *  Remember to include the semicolon at the end of the statement string.
	 *  (Not all programming languages and/or packages require the semicolon (e.g., Python's SQLite3 library))
	 */
	private static String sqlStatement = "SELECT Title, C.CallNumber, C.PatronEmail, C.CheckoutDate, C.ReturnDate "
			+ "FROM Checkout AS C, MediaInstance AS M, Patron AS P, Media AS Med "
			+ "WHERE C.CallNumber = M.CallNumber AND P.Email = ? "
			+ "AND P.Email = C.PatronEmail AND M.MediaID = Med.MediaID;";
	
	private static String sqlStatement2 = "SELECT Name, count(MI.CallNumber) AS Associated_Checkouts " + 
			"From Media NATURAL JOIN MediaInstance AS MI NATURAL JOIN Checkout AS C NATURAL JOIN ContributesTo AS CT NATURAL JOIN Contributor " + 
			"Where PrimaryRole = 'Actor' " + 
			"GROUP BY Name " + 
			"Order BY count(MI.CallNumber) DESC " + 
			"LIMIT 1; ";
	
	private static String sqlStatement3 = "SELECT Name, SUM(Total_Listens) as Artist_Listens " + 
			"FROM Contributor as C, ContributesTo as CT, " + 
			"(SELECT M.MediaID, SUM(Length) as Total_Listens " + 
			"FROM Media as M, Audio as A, Track as T, MediaInstance as MI, Checkout as C " + 
			"WHERE A.[Album/Audiobook] = 'a' " + 
			"AND T.AudioID = A.AudioID " + 
			"AND M.MediaID = A.AudioID " + 
			"AND M.MediaID = MI.MediaID " + 
			"AND C.CallNumber = MI.CallNumber " + 
			"GROUP BY M.MediaID) as Album_Listens " + 
			"WHERE C.ContributorID = CT.ContributorID " + 
			"AND C.PrimaryRole='Artist' " + 
			"AND CT.ContributorID = C.ContributorID " + 
			"AND CT.MediaID = Album_Listens.MediaID " + 
			"GROUP BY Name " + 
			"ORDER BY Artist_Listens DESC " + 
			"LIMIT 1;";
	
	private static String sqlStatement4 = "SELECT Name, SUM(Total_Listens) as Author_Listens " + 
			"FROM Contributor as C, ContributesTo as CT, " + 
			"(SELECT M.MediaID, SUM(Length) as Total_Listens " + 
			"FROM Media as M, Audio as A, Track as T, MediaInstance as MI, Checkout as C " + 
			"WHERE A.[Album/Audiobook] = 'b' " + 
			"AND T.AudioID = A.AudioID " + 
			"AND M.MediaID = A.AudioID " + 
			"AND M.MediaID = MI.MediaID " + 
			"AND C.CallNumber = MI.CallNumber " + 
			"GROUP BY M.MediaID) as Album_Listens " + 
			"WHERE C.ContributorID = CT.ContributorID " + 
			"AND C.PrimaryRole='Author' " + 
			"AND CT.ContributorID = C.ContributorID " + 
			"AND CT.MediaID = Album_Listens.MediaID " + 
			"GROUP BY Name " + 
			"ORDER BY Author_Listens DESC " + 
			"LIMIT 1;";
	
	private static String sqlStatement5 = "SELECT Email, Fname, Lname, COUNT(*) AS counts " + 
			"FROM Movie AS M JOIN MediaInstance AS I ON MovieID = I.MediaID NATURAL JOIN " + 
			"Checkout AS C JOIN Patron AS P ON Email = PatronEmail " + 
			"GROUP BY P.Email " +
			"ORDER BY counts DESC LIMIT 1;";
	
	private static String sqlStatement6 = "Select C.Name, M.Title, T.TrackTitle " + 
			"FROM Contributor as C, ContributesTo as CT, Media as M, Audio as A, Track as T " + 
			"WHERE M.MediaID = CT.MediaID " + 
			"AND C.ContributorID = CT.ContributorID " + 
			"AND A.AudioID = M.MediaID " + 
			"AND A.[Album/Audiobook] = 'a' " + 
			"AND T.AudioID = A.AudioID " + 
			"AND C.Name = ? " + 
			"AND M.Year < ?;";
	

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

