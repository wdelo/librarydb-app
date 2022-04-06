

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

//Utility class - handles searching within the database
//Methods are organized in alphabetical order
public class InsertionManager implements UserOption {
	
	// Constructor
	public InsertionManager () {}
		
	// Performs an insertion for an actor and returns the new unique ID generated for it
	private static String insertActor(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the actor:");
		String name = s.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(s);
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Actor'");
		return id;
	}
	
	// Performs an insertion for an artist and returns the new unique ID generated for it
	private static String insertArtist(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the artist:");
		String name = s.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(s);
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Artist'");
		return id;
	}
	
	// Performs an insertion for an author and returns the new unique ID generated for it
	private static String insertAuthor(Connection conn, Scanner s) {
		System.out.println("Please enter the name of the author:");
		String name = s.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(s);
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Author'");
		return id;
	}

	// Performs an insertion for an album
	private static void insertAlbum(Connection conn, Scanner s) {
		System.out.println("Please enter the title of the album:");
		String title = s.nextLine();
		System.out.println("Please enter the genre of the album:");
		String genre = s.nextLine();
		System.out.println("Please enter the year of the album:");
		String year = s.nextLine();
		String id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
			
		ArrayList<String> artistIds = new ArrayList<String>();
			
		System.out.println("Are there any artists of this album that are already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			System.out.println("Let's find some.\n");
			boolean done = false;
			do {
				System.out.println("What is the artist's name?");
				String artistName = s.nextLine();
				String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Artist' AND Name = $value;";
			    sql = sql.replace("$value", "'"+artistName+"'");
				String artistId = DBUtils.searchAndSelect(conn, s, sql, "ContributorID", 2);
				if (artistId != null) {
					artistIds.add(artistId);
				}
				System.out.println("Are there any more artists of this album that are already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
			
		System.out.println("Are there any artists of this album that AREN'T already in the database?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			System.out.println("Let's add some.\n");
			boolean done = false;
			do {
				String artistId = insertArtist(conn, s);
				if (artistId != null) {
					artistIds.add(artistId);
				}
				System.out.println("Are there any more artists of this album that AREN'T already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
				
		DBUtils.insertRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.insertRecord(conn, "Audio", id, "'a'");
			
		for (int i = 0; i < artistIds.size(); i++)
			DBUtils.insertRecord(conn, "Contributes_To", id, "'"+artistIds.get(i)+"'", "'Artist'");
		
		System.out.println("Would you like to add tracks to this album?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			boolean done = false;
			int count = 1;
			do {
				System.out.println("Please enter the title of the track:");
				String trackTitle = s.nextLine();
				System.out.println("Please enter the length of the track (in seconds):");
				int length = DBUtils.getValidInput(0, 9999, s);
				DBUtils.insertRecord(conn, "Track", "'"+id+"'", ""+count, "'"+trackTitle+"'", ""+length);
				count++;
				System.out.println("Would you like to add another?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 2) {
					done = true;
				}
			} while (!done);
		}
	}
	
	// Performs an insertion for a book
	private static void insertBook(Connection conn, Scanner s) {
		System.out.println("Please enter the title of the book:");
		String title = s.nextLine();
		System.out.println("Please enter the genre of the book:");
		String genre = s.nextLine();
		System.out.println("Please enter the year of the book:");
		String year = s.nextLine();
		String id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
			
		ArrayList<String> authorIds = new ArrayList<String>();
			
		System.out.println("Are there any authors of this book that are already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			System.out.println("Let's find some.\n");
			boolean done = false;
			do {
				System.out.println("What is the author's name?");
				String authorName = s.nextLine();
				String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Author' AND Name = $value;";
			    sql = sql.replace("$value", "'"+authorName+"'");
				String authorId = DBUtils.searchAndSelect(conn, s, sql, "ContributorID", 2);
				if (authorId != null) {
					authorIds.add(authorId);
				}
				System.out.println("Are there any more authors of this book that are already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
			
		System.out.println("Are there any authors of this book that AREN'T already in the database?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			System.out.println("Let's add some.\n");
			boolean done = false;
			do {
				String authorId = insertAuthor(conn, s);
				if (authorId != null) {
					authorIds.add(authorId);
				}
				System.out.println("Are there any more authors of this book that AREN'T already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
				
		DBUtils.insertRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.insertRecord(conn, "Audio", id, "'b'");
			
		for (int i = 0; i < authorIds.size(); i++)
			DBUtils.insertRecord(conn, "Contributes_To", id, "'"+authorIds.get(i)+"'", "'Author'");
	}
	
	// Performs an insertion for a checkout
	private static void insertCheckout(Connection conn, Scanner s) {
		System.out.println("Let's find the patron who is checking out. Please enter their last name:");
		String lname = s.nextLine();
		
		String sql = "SELECT Email_Address, Fname, Lname FROM Patron WHERE Lname = '"+lname+"';";
		String email = DBUtils.searchAndSelect(conn, s, sql, "Email_Address", 3);
		
		System.out.println("Let's find the piece of media this patron is checking out. Please enter its title:");
		String title = s.nextLine();
		
		sql = "SELECT Call_Number, Title, Genre, Year FROM Media_Instance AS MI JOIN Media AS M ON MI.MediaID = M.MediaID "
				+ "WHERE Title = '"+title+"' AND Availability = 1;";
		String callNum = DBUtils.searchAndSelect(conn, s, sql, "Call_Number", 4);
		
		if (email != null && callNum != null) {
			System.out.println("Let's get an initial checkout date.");
			String date = DBUtils.getFormattedDate(s);
			System.out.println("Has this checkout been returned yet?\n1. Yes\n2. No");
			int userChoice = DBUtils.getValidInput(1, 2, s);
			if (userChoice == 1) {
				System.out.println("Let's get a return date.");
				String returnDate = DBUtils.getFormattedDate(s);
				DBUtils.insertRecord(conn, "Checkout", "'"+callNum+"'", "'"+email+"'", date, returnDate);
			} else {
				DBUtils.insertRecord(conn, "Checkout", "'"+callNum+"'", "'"+email+"'", date, "NULL");
			}	
		} else {
			System.out.println("Error inserting: nonexistant patron or media OR no available media");
		}
	}
	
	// Performs an insertion for a condition
	private static void insertCondition(Connection conn, Scanner s) {
		System.out.println("Let's find the piece of media this condition will be applied to. Please enter its title:");
		String title = s.nextLine();
		
		String sql = "SELECT Call_Number, Title, Genre, Year, Availability FROM Media_Instance AS MI JOIN Media AS M ON MI.MediaID = M.MediaID "
				+ "WHERE Title = '"+title+"';";
		String callNum = DBUtils.searchAndSelect(conn, s, sql, "Call_Number", 4);
		
		if (callNum != null) {
			System.out.println("Let's get the date this condition is associated with.");
			String date = DBUtils.getFormattedDate(s);
			System.out.println("What condition is this piece of media in? (Good, poor, etc.);");
			String condition = s.nextLine();
			System.out.println("Is this piece of media reported as missing?\n1. Yes\n2. No");
			int missing = DBUtils.getValidInput(1, 2, s);
			if (missing == 2)
				missing = 0;
			System.out.println("What is the reason for the current condition of this piece of media?");
			String reason = s.nextLine();
			DBUtils.insertRecord(conn, "Condition", date, "'"+callNum+"'", "'"+condition+"'", ""+missing, "'"+reason+"'");
		} else {
			System.out.println("Error inserting: nonexistant media OR no available media");
		}
	}
	
	// Performs an insertion for a movie
	private static void insertMovie(Connection conn, Scanner s) {
		System.out.println("Please enter the title of the movie:");
		String title = s.nextLine();
		System.out.println("Please enter the genre of the movie:");
		String genre = s.nextLine();
		System.out.println("Please enter the year of the movie:");
		String year = s.nextLine();
		System.out.println("Please enter the content rating of the movie (PG-13, R, etc.):");
		String cr = s.nextLine();
		System.out.println("Please enter the length of the movie (whole number of minutes):");
		int minutes = DBUtils.getValidInput(0, 999, s);
		String id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
		
		ArrayList<String> actorIds = new ArrayList<String>();
		
		System.out.println("Are there any actors in this movie that are already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			System.out.println("Let's find some.\n");
			boolean done = false;
			do {
				System.out.println("What is the actor's name?");
				String actorName = s.nextLine();
				String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
		        sql = sql.replace("$value", "'"+actorName+"'");
				String actorId = DBUtils.searchAndSelect(conn, s, sql, "ContributorID", 2);
				if (actorId != null) {
					actorIds.add(actorId);
				}
				System.out.println("Are there any more actors in this movie that are already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
		
		System.out.println("Are there any actors in this movie that AREN'T already in the database?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			System.out.println("Let's add some.\n");
			boolean done = false;
			do {
				String actorId = insertActor(conn, s);
				if (actorId != null) {
					actorIds.add(actorId);
				}
				System.out.println("Are there any more actors in this movie that AREN'T already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
			
		DBUtils.insertRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.insertRecord(conn, "Movie", id, "'"+cr+"'", ""+minutes);
		
		for (int i = 0; i < actorIds.size(); i++)
			DBUtils.insertRecord(conn, "Contributes_To", id, "'"+actorIds.get(i)+"'", "'Actor'");
	}
	
	// Performs an insertion for a patron
	private static void insertPatron(Connection conn, Scanner s) {
		System.out.println("Please enter an email address:");
		String email = "";
		boolean emailIsUnique = false;
		do {
			email = s.nextLine();
			emailIsUnique = !DBUtils.valueExists(conn, "Patron", "Email_Address", "'"+email+"'");
			if (!emailIsUnique) {
				System.out.println("That email is taken. Please enter a different one:");
			}
		} while (!emailIsUnique);
		System.out.println("Please enter a city name:");
		String city = s.nextLine();
		System.out.println("Please enter a state:");
		String state = s.nextLine();
		System.out.println("Please enter a street address:");
		String addr = s.nextLine();
		System.out.println("Please enter a first name:");
		String fname = s.nextLine();
		System.out.println("Please enter a last name:");
		String lname = s.nextLine();
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Patron", "'"+email+"'", "'"+city+"'", "'"+state+"'", "'"+addr+"'", "'"+fname+"'", "'"+lname+"'");
	}
	
	// Performs an insertion for a review
	private static void insertReview(Connection conn, Scanner s) {	
		System.out.println("Let's find the patron who is submitting this review. Please enter their last name:");
		String lname = s.nextLine();
		
		String sql = "SELECT Email_Address, Fname, Lname FROM Patron WHERE Lname = '"+lname+"';";
		String email = DBUtils.searchAndSelect(conn, s, sql, "Email_Address", 3);
		
		System.out.println("Let's find the piece of media this patron is reviewing. Please enter its title:");
		String title = s.nextLine();
		
		sql = "SELECT Title, Genre, Year, MediaID FROM Media WHERE Title = '"+title+"';";
		String id = DBUtils.searchAndSelect(conn, s, sql, "MediaID", 3);
		
		if (email != null && id != null) {
			System.out.println("Please enter the rating (1-10) the review gave:");
			int rating = DBUtils.getValidInput(1, 10, s);
			System.out.println("Please enter the content of this review:");
			String content = s.nextLine();
			
			DBUtils.insertRecord(conn, "Review", "'"+email+"'", "'"+id+"'", ""+rating, "'"+content+"'");
		} else {
			System.out.println("Error inserting: nonexistant patron or media");
		}
	}
	
	// Performs an insertion for a track
	private static void insertTrack(Connection conn, Scanner s) {
		System.out.println("Please enter the title of the track:");
		String title = s.nextLine();
		System.out.println("Please enter the length of the track (in seconds):");
		int length = DBUtils.getValidInput(0, 9999, s);
		
		int trackNum = 0;
		String id = "";
		
		System.out.println("Is this track part of an album?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, s);
		if (userChoice == 1) {
			do {
				System.out.println("Is this album already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, s);
				if (userChoice == 1) {
					System.out.println("Let's find it.\n");
					System.out.println("What is the name of the album?");
					String albumName = s.nextLine();
					String sql = "SELECT Title, Genre, Year, AudioID FROM Media JOIN Audio ON MediaID = AudioID "
						+ "WHERE [Album/Audiobook] = 'a' AND Title = $value";
					sql = sql.replace("$value", "'"+albumName+"'");
					id = DBUtils.searchAndSelect(conn, s, sql, "AudioID", 3);
				} else {
					System.out.println("Let's add it.");
					System.out.println("What is the title of this album?");
					String albumTitle = s.nextLine();
					System.out.println("What genre is this album?");
					String genre = s.nextLine();
					System.out.println("What year was this album released?");
					String year = s.nextLine();
					id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
					DBUtils.insertRecord(conn, "Media", "'"+id+"'", "'"+albumTitle+"'", "'"+genre+"'", "'"+year+"'");
					DBUtils.insertRecord(conn, "Audio", "'"+id+"'", "'a'");
				}
			} while (id == null);
			trackNum = DBUtils.getTrackCount(conn, "'"+id+"'") + 1;
		} else {
			System.out.println("What genre is this track?");
			String genre = s.nextLine();
			System.out.println("What year was this track released?");
			String year = s.nextLine();
			id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
			DBUtils.insertRecord(conn, "Media", "'"+id+"'", "'"+title+"'", "'"+genre+"'", "'"+year+"'");
			DBUtils.insertRecord(conn, "Audio", "'"+id+"'", "'a'");
			trackNum = 1;
		}
		
		DBUtils.insertRecord(conn, "Track", "'"+id+"'", ""+trackNum, "'"+title+"'", ""+length);
	}
	
	// Text-based UI for inserting into the database
	public void execute(Connection conn, Scanner s) {
		System.out.println("What kind of record would you like to insert?");
        System.out.println("1. Movie\n2. Album\n3. Track\n4. Audiobook\n");
        System.out.println("5. Actor\n6. Artist\n7. Author\n");
        System.out.println("8. Patron\n9. Review\n10. Checkout\n");
        System.out.println("11. Condition\n");
        System.out.println("12. Go back");
        int userChoice = DBUtils.getValidInput(1, 12, s);
		
		switch (userChoice) {
		case 1:
			insertMovie(conn, s);
			break;
		case 2:
			insertAlbum(conn, s);
			break;
		case 3:
			insertTrack(conn, s);
			break;
		case 4:
			insertBook(conn, s);
			break;
		case 5:
			insertActor(conn, s);
			break;
		case 6:
		    insertArtist(conn, s);
			break;
		case 7:
			insertAuthor(conn, s);
			break;
		case 8:
			insertPatron(conn, s);
			break;
		case 9:
			insertReview(conn, s);
			break;
		case 10:
			insertCheckout(conn, s);
		case 11:
			insertCondition(conn, s);
		case 12:
			// Nothing happens
		default:
			break;
		}
	}
}




