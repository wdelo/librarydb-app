package ldb.dbitem;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class CheckoutController {

	private static DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private static String menuPrompt = "What would you like to manage with checkouts?";	
	private static String[] menuScreenOptions = {
    		"Add a new checkout", 
    		"View checkouts",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "Which checkouts would you like to view";
	private static String[] selectedMenuScreenOptions = {
			"Completed checkouts",
			"Checkouts in-progress",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(menuPrompt, menuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	
	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Let's find the patron who is checking out.");
		String[] email = PatronController.retrieve(conn, in);
		
		if (email == null) {
			System.out.println("Error: Nonexistant patron");
			return null;
		}
		
		System.out.println("Let's find the piece of media this patron is checking out. Please enter its title:");
		String title = in.nextLine();
		
		String sql = "SELECT CallNumber, Title, Genre, Year FROM MediaInstance AS MI JOIN Media AS M ON MI.MediaID = M.MediaID "
				+ "WHERE Title = '"+title+"' AND IsAvailable = true;";
		String[] callNum = DBUtils.searchAndSelect(conn, in, sql, 4, "CallNumber");
		
		if (callNum == null) {
			System.out.println("Error: Nonexistant media");
			return null;
		}
		
		String date = LocalDate.now().format(dtFormatter).toString();
		System.out.println(date);
		
		DBUtils.insertRecord(conn, "Checkout", "'"+callNum[0]+"'", "'"+email[0]+"'", "'"+date+"'", "'NULL'");
		return new String[] { callNum[0], email[0], date };
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE CallNumber="+"'"+ids[0]+"'"+" AND PatronEmail="+"'"+ids[1]+"'"+" AND CheckoutDate="+"'"+ids[2]+"'");
	}

	public static String[] retrieveCompleted(Connection conn, Scanner in) {	
		String sql = "SELECT Title, C.CallNumber, C.PatronEmail, C.CheckoutDate, C.ReturnDate "
				+ "FROM Checkout AS C, MediaInstance AS M, Patron AS P, Media AS Med "
				+ "WHERE C.ReturnDate <> 'NULL' AND C.CallNumber = M.CallNumber "
				+ "AND P.Email = C.PatronEmail AND M.MediaID = Med.MediaID;";

		return DBUtils.searchAndSelect(conn, in, sql, 5, "CallNumber", "PatronEmail", "CheckoutDate");
	}
	
	public static String[] retrieveInProgress(Connection conn, Scanner in) {	
		String sql = "SELECT Title, C.CallNumber, C.PatronEmail, C.CheckoutDate, C.ReturnDate "
				+ "FROM Checkout AS C, MediaInstance AS M, Patron AS P, Media AS Med "
				+ "WHERE C.ReturnDate = 'NULL' AND C.CallNumber = M.CallNumber "
				+ "AND P.Email = C.PatronEmail AND M.MediaID = Med.MediaID;";

		return DBUtils.searchAndSelect(conn, in, sql, 4, "CallNumber", "PatronEmail", "CheckoutDate");
	}
	
	public static void retrieveByPatron(Connection conn, Scanner in, String[] parentIds) {
		String sql = "SELECT Title, C.CallNumber, C.PatronEmail, C.CheckoutDate, C.ReturnDate "
				+ "FROM Checkout AS C, MediaInstance AS M, Patron AS P, Media AS Med "
				+ "WHERE C.CallNumber = M.CallNumber AND P.Email = "+"'"+parentIds[0]+"' "
				+ "AND P.Email = C.PatronEmail AND M.MediaID = Med.MediaID;";
		
		DBUtils.printRows(conn, sql, 4);
	}

	public static void execute(Connection conn, Scanner in) {
		menuScreen.display();
		int menuSelection = menuScreen.getOption(in);
		switch (menuSelection) {
		case 1:
			insert(conn, in);
			break;
		case 2:
			view(conn, in);
			break;
		case 3:
			break;
		}
	}
	
	public static void view(Connection conn, Scanner in) {
		selectedMenuScreen.display();
		int menuSelection = selectedMenuScreen.getOption(in);
		String[] ids;
		switch (menuSelection) {
		case 1:
			ids = retrieveCompleted(conn, in);
			if (ids != null) {
				System.out.println("1. Delete checkout\n 2.Back");
				int completedSelection = DBUtils.getValidInput(1, 2, in);
				if (completedSelection == 1) {
					delete(conn, in, ids);
				}
			}
			break;
		case 2:
			markAsReturned(conn, in);
			break;
		case 3:
			break;
		}
	}
		
	private static void markAsReturned(Connection conn, Scanner in) {
		String[] ids = retrieveInProgress(conn, in);
		if (ids != null) {
			System.out.println("1. Mark as returned\n2. Back");
			int completedSelection = DBUtils.getValidInput(1, 2, in);
			if (completedSelection == 1) {
				String mediaInstanceSQL = "SELECT CallNumber, [Digital/Physical], IsAvailable, Location, MediaID "
						+ "FROM Checkout AS C JOIN MediaInstance AS M ON C.Call_Number = M.Call_Number " 
						+ "JOIN Patron AS P ON P.Email = C.Email_Address " 
						+ "JOIN Media AS Med ON M.MediaID = Med.MediaID ";
				
				String date = LocalDate.now().format(dtFormatter).toString();
				String[] miIds = DBUtils.searchAndSelect(conn, in, mediaInstanceSQL, 5, "CallNumber", "[Digital/Physical]", "IsAvailable", "Location", "MediaID");
				DBUtils.editRecord(conn, "Checkout", 3, "CallNumber", "'"+ids[0]+"'", "PatronEmail", "'"+ids[1]+"'", "CheckoutDate", "'"+ids[2]+"'", "ReturnDate", "'"+date+"'");
				DBUtils.editRecord(conn, "MediaInstance", 5, "CallNumber", "'"+miIds[0]+"'", "[Digital/Physical]", "'"+miIds[1]+"'", "IsAvailable", "1","Location", "'"+miIds[3]+"'", "MediaID", "'"+miIds[4]+"'");
			}
		}
	}

}
