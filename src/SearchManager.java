

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

//Utility class - handles searching within the database
//Methods are organized in alphabetical order
public class SearchManager implements UserOption {
	
	// Constructor
	public SearchManager () {}
	
	// Retrieves a specific actor ID through a user search
	public static String retrieveActor(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the actor to search for:");
        String userInput = s.nextLine();
        
        String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        return DBUtils.searchAndSelect(conn, s, sql, "ContributorID", 2);
	}
	
	// Retrieves a specific album ID through a user search
	public static String retrieveAlbum(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Artist Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "";
        String id = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Title = $value;";
			System.out.println("Please enter an album title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Genre = $value;";
			System.out.println("Please enter an album genre to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 3:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Year = $value;";
			System.out.println("Please enter an album year to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID"
					+ " WHERE A.[Album/Audiobook] = 'a' AND Con.Name = $value;";
			System.out.println("Please enter an artist name to search for their albums:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, s, sql, "AudioID", 3);
	}
	
	// Retrieves a specific artist ID through a user search
	public static String retrieveArtist(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the artist to search for:");
	    String userInput = s.nextLine();
	        
	    String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Artist' AND Name = $value;";
	    sql = sql.replace("$value", "'"+userInput+"'");
	        
	    return DBUtils.searchAndSelect(conn, s, sql, "ContributorID", 2);
	}
	
	// Retrieves a specific author ID through a user search
	public static String retrieveAuthor(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the author to search for:");
        String userInput = s.nextLine();
        
        String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Author' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        return DBUtils.searchAndSelect(conn, s, sql, "ContributorID", 2);
	}
	
	// Retrieves a specific book ID through a user search
	public static String retrieveBook(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Author Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Title = $value;";
			System.out.println("Please enter a book title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:	
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Genre = $value;";
			System.out.println("Please enter a book genre to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 3:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Year = $value;";
			System.out.println("Please enter a book year to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+ "WHERE [Album/Audiobook] = 'b' AND Con.Name = $value;";
			System.out.println("Please enter an author name to search for their books:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, s, sql, "AudioID", 3);
	}
	
	// Retrieves a specific call number, email address, and checkout date through a user search
	public static String[] retrieveCheckout(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Patron Email\n");
        int userChoice = DBUtils.getValidInput(1, 2, s);
		
        String userInput = "";
        String sql = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE C.Email_Address = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;		
		default:
			break;
		}
		return DBUtils.searchAndSelect3(conn, s, sql, "Call_Number", "Email_Address", "Checkout_date", 4);
	}
	
	// Retrieves a specific date and call number through a user search
	public static String[] retrieveCondition(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Damage\n3. Missing Status");
        int userChoice = DBUtils.getValidInput(1, 3, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status, Date FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status, Date FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Damage = $value;";
			System.out.println("Please enter a damage condition (Good, Poor, etc.) to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;	
		case 3: 
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status, Date FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Missing_Status = $value;";
			System.out.println("Would you like to search for:\n1. Missing pieces of media\n2. Non-missing pieces of media");
			int missing = DBUtils.getValidInput(1, 2, s);
			sql = sql.replace("$value", "'"+Math.abs(missing-2)+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect2(conn, s, sql, "Date", "Call_Number", 3);
	}
	
	// Retrieves a specific movie ID through a user search
	public static String retrieveMovie(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Actor Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID WHERE Title = $value;";
			System.out.println("Please enter a movie title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID WHERE Genre = $value;";
			System.out.println("Please enter a movie genre to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;		
		case 3:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID WHERE Year = $value;";
			System.out.println("Please enter a movie year to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie AS M JOIN Media AS Med ON Med.MediaID = M.MovieID "
				+"JOIN Contributes_To AS C ON M.MovieID = C.MediaID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+"WHERE Con.Name = $value;";
			System.out.println("Please enter an actor name to search for their movies:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, s, sql, "MovieID", 5);
	}
	
	// Retrieves a specific email address through a user search
	public static String retrievePatron(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Email Address\n2. City\n3. State\n4. Last Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "SELECT Email_Address, City, State, Street_Addr, Fname, Lname FROM Patron WHERE $attribute = $value;";
		switch (userChoice) {
		case 1:
			System.out.println("Please enter an email address to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "Email_Address");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			System.out.println("Please enter a city to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "City");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 3:
			System.out.println("Please enter a state to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "State");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			System.out.println("Please enter a last name to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "Lname");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, s, sql, "Email_Address", 6);
	}
	
	// Retrieves a specific email address and media ID through a user search
	public static String[] retrieveReview(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Patron Email\n");
        int userChoice = DBUtils.getValidInput(1, 2, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Patron_Email, Rating, Review, R.MediaID FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, Patron_Email, Rating, Review, R.MediaID FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;		
		default:
			break;
		}
		return DBUtils.searchAndSelect2(conn, s, sql, "Patron_Email", "MediaID", 4);
	}
	
	// Retrieves a specific album ID and track number through a user search
	public static String[] retrieveTrack(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Album Name\n");
        int userChoice = DBUtils.getValidInput(1, 2, s);
		
        String userInput = "";
        String sql = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Track_Title, Length, AudioID, Track_Number FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID "
	        		+"WHERE [Album/Audiobook] = 'a' AND Track_Title = $value;";
			System.out.println("Please enter a track title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Track_Title, Length, AudioID, Track_Number FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID JOIN Media ON A.AudioID = MediaID "
	        		+"WHERE [Album/Audiobook] = 'a' AND $attribute = $value;";
			System.out.println("Please enter an album name to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "Title");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect2(conn, s, sql, "AudioID", "Track_Number", 2);
	}
	
	// Performs a search for an actor
	private static void searchActor(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the actor to search for:");
        String userInput = s.nextLine();
        
        String sql = "SELECT Name, DOB FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        DBUtils.retrieveRows(conn, sql);
	}
	
	// Performs a search for an album
	private static void searchAlbum(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Artist Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Title = $value;";
			System.out.println("Please enter an album title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Genre = $value;";
			System.out.println("Please enter an album genre to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 3:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Year = $value;";
			System.out.println("Please enter an album year to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 4:
			sql = "SELECT Title, Genre, Year FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID"
					+ " WHERE A.[Album/Audiobook] = 'a' AND Con.Name = $value;";
			System.out.println("Please enter an artist name to search for their albums:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		default:
			break;
		}
	}
	
	// Performs a search for an artist
	private static void searchArtist(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the artist to search for:");
        String userInput = s.nextLine();
        
        String sql = "SELECT Name, DOB FROM Contributor WHERE PrimaryRole = 'Artist' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        DBUtils.retrieveRows(conn, sql);
	}
	
	// Performs a search for an author
	private static void searchAuthor(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the author to search for:");
        String userInput = s.nextLine();
        
        String sql = "SELECT Name, DOB FROM Contributor WHERE PrimaryRole = 'Author' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        DBUtils.retrieveRows(conn, sql);
	}
	
	// Performs a search for an audiobook
	private static void searchBook(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Author Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Title = $value;";
			System.out.println("Please enter a book title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:	
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Genre = $value;";
			System.out.println("Please enter a book genre to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 3:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Year = $value;";
			System.out.println("Please enter a book year to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 4:
			sql = "SELECT Title, Genre, Year FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+ "WHERE [Album/Audiobook] = 'b' AND Con.Name = $value;";
			System.out.println("Please enter an author name to search for their books:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		default:
			break;
		}
	}
	
	// Performs a search for a checkout
	private static void searchCheckout(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Patron Email\n");
        int userChoice = DBUtils.getValidInput(1, 2, s);
		
        String userInput = "";
        String sql = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE C.Email_Address = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;		
		default:
			break;
		}
	}
	
	// Performs a search for a condition
	private static void searchCondition(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Damage\n3. Missing Status");
        int userChoice = DBUtils.getValidInput(1, 3, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Damage = $value;";
			System.out.println("Please enter a damage condition (Good, Poor, etc.) to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;	
		case 3: 
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Missing_Status = $value;";
			System.out.println("Would you like to search for:\n1. Missing pieces of media\n2. Non-missing pieces of media");
			int missing = DBUtils.getValidInput(1, 2, s);
			sql = sql.replace("$value", "'"+Math.abs(missing-2)+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		default:
			break;
		}
	}
	
	// Performs a search for a movie
	private static void searchMovie(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Actor Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length FROM Movie JOIN Media ON MediaID = MovieID WHERE Title = $value;";
			System.out.println("Please enter a movie title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length FROM Movie JOIN Media ON MediaID = MovieID WHERE Genre = $value;";
			System.out.println("Please enter a movie genre to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;		
		case 3:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length FROM Movie JOIN Media ON MediaID = MovieID WHERE Year = $value;";
			System.out.println("Please enter a movie year to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length FROM Movie AS M JOIN Media AS Med ON Med.MediaID = M.MovieID "
				+"JOIN Contributes_To AS C ON M.MovieID = C.MediaID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+"WHERE Con.Name = $value;";
			System.out.println("Please enter an actor name to search for their movies:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		default:
			break;
		}
	}
	
	// Performs a search for a patron
	private static void searchPatron(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Email Address\n2. City\n3. State\n4. Last Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, s);
		
        String userInput = "";
        String sql = "SELECT Email_Address, City, State, Street_Addr, Fname, Lname FROM Patron WHERE $attribute = $value;";
		switch (userChoice) {
		case 1:
			System.out.println("Please enter an email address to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "Email_Address");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			System.out.println("Please enter a city to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "City");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 3:
			System.out.println("Please enter a state to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "State");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 4:
			System.out.println("Please enter a last name to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "Lname");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		default:
			break;
		}
	}
	
	// Performs a search for a review
	private static void searchReview(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Patron Email\n");
        int userChoice = DBUtils.getValidInput(1, 2, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Patron_Email, Rating, Review FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Title, Patron_Email, Rating, Review FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE R.Patron_Email = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;		
		default:
			break;
		}
	}
	
	// Performs a search for a track
	private static void searchTrack(Connection conn, Scanner s) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Album Name\n");
        int userChoice = DBUtils.getValidInput(1, 2, s);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Track_Title, Length FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID "
	        		+"WHERE [Album/Audiobook] = 'a' AND Track_Title = $value;";
			System.out.println("Please enter a track title to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Track_Title, Length FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID JOIN Media ON A.AudioID = MediaID "
	        		+"WHERE [Album/Audiobook] = 'a' AND $attribute = $value;";
			System.out.println("Please enter an album name to search for:");
			userInput = s.nextLine();
			sql = sql.replace("$attribute", "Title");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		default:
			break;
		}
	}
	
	// Text-based UI for searching within the database
	public void execute(Connection conn, Scanner s) {
		System.out.println("What kind of record would you like to search for?");
        System.out.println("1. Movie\n2. Album\n3. Track\n4. Audiobook\n");
        System.out.println("5. Actor\n6. Artist\n7. Author\n");
        System.out.println("8. Patron\n9. Review\n10. Checkout\n");
        System.out.println("11. Condition\n");
        System.out.println("12. Go back");
        int userChoice = DBUtils.getValidInput(1, 12, s);
		
		switch (userChoice) {
		case 1:
			searchMovie(conn, s);	
			break;
		case 2:
			searchAlbum(conn, s);
			break;
		case 3:
			searchTrack(conn, s);
			break;
		case 4:
			searchBook(conn, s);
			break;
		case 5:
			searchActor(conn, s);
			break;
		case 6:
		    searchArtist(conn, s);
			break;
		case 7:
			searchAuthor(conn, s);
			break;
		case 8:
			searchPatron(conn, s);
			break;
		case 9:
			searchReview(conn, s);
			break;
		case 10:
			searchCheckout(conn, s);
			break;
		case 11:
			searchCondition(conn, s);
			break;
		case 12:
			// Nothing happens
		default:
			break;
		}
	}
}
	





