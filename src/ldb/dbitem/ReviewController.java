package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class ReviewController {

	private static String conditionMenuPrompt = "What would you like to manage with this condition log?";	
	private static String[] conditionMenuScreenOptions = {
    		"Add a Review", 
    		"Select a Review",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this Review?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this Review",
			"Edit this Review",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(conditionMenuPrompt, conditionMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in, String[] parentIds) {
		System.out.println("Let's find the patron who is submitting this review. Please enter their last name:");
		String lname = in.nextLine();
		
		String sql = "SELECT Email, Fname, Lname FROM Patron WHERE Lname = '"+lname+"';";
		String[] email = DBUtils.searchAndSelect(conn, in, sql, 3, "Email_Address");
		
		System.out.println("Let's find the piece of media this patron is reviewing. Please enter its title:");
		String title = in.nextLine();
		
		sql = "SELECT Title, Genre, Year, MediaID FROM Media WHERE Title = '"+title+"';";
		String[] id = DBUtils.searchAndSelect(conn, in, sql, 3, "MediaID");
		
		if (email != null && id != null) {
			System.out.println("Please enter the rating (1-10) the review gave:");
			int rating = DBUtils.getValidInput(1, 10, in);
			System.out.println("Please enter the content of this review:");
			String content = in.nextLine();
			
			DBUtils.insertRecord(conn, "Review", "'"+email+"'", "'"+id+"'", ""+rating, "'"+content+"'");
			return new String[] { email[0], id[0] };
		} else {
			System.out.println("Error inserting: nonexistant patron or media");
			return null;
		}
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the desired rating (1-10):");
		int rating = DBUtils.getValidInput(1, 10, in);
		System.out.println("Please enter the desired response:");
		String content = in.nextLine();
		DBUtils.editRecord(conn, "Review", 2, "PatronEmail", "'"+ids[0]+"'", "MediaID", "'"+ids[1]+"'", "Rating", "'"+rating+"'", "Response", "'"+content+"'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Review WHERE Patron_Email="+"'"+ids[0]+"'"+" AND MediaID="+"'"+ids[1]+"'");	
	}

	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Patron Email\n");
        int userChoice = DBUtils.getValidInput(1, 2, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Patron_Email, Rating, Review, R.MediaID FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, Patron_Email, Rating, Review, R.MediaID FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;		
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, in, sql, 4, "Patron_Email", "MediaID");
	}

	public static void execute(Connection conn, Scanner in, String[] parentIds) {
		menuScreen.displayBlank();
		int menuSelection = menuScreen.getOption(in);
		switch (menuSelection) {
		case 1:
			insert(conn, in, parentIds);
			break;
		case 2:
			view(conn, in, parentIds);
			break;
		default:
			break;
		}
		
	}
	
	public static void view(Connection conn, Scanner in, String[] parentIds) {
		String[] ids = retrieve(conn, in, parentIds);
		if (ids != null) {
			selectedMenuScreen.display();
			int menuSelection = selectedMenuScreen.getOption(in);
			switch (menuSelection) {
			case 1:			
				delete(conn, in, ids);
				break;
			case 2:
				edit(conn, in, ids);
				break;
			default:
				break;
			}	
		} else {
			DBUtils.blank();
			System.out.println("No reviews :(\nType \"1\" to continue to the main menu.");
			DBUtils.getValidInput(1, 1, in);
		}
	}

}
