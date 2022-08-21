package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class PatronController {

	private static String menuPrompt = "What would you like to manage with patrons?";	
	private static String[] menuScreenOptions = {
    		"Add a patron", 
    		"Select a patron",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this patron?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this patron",
			"Edit this patron",
			"View patron's checkouts",
			"View patron's reviews",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(menuPrompt, menuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter an email address:");
		String email = "";
		boolean emailIsUnique = false;
		do {
			email = in.nextLine();
			emailIsUnique = !DBUtils.valueExists(conn, "Patron", "Email", "'"+email+"'");
			if (!emailIsUnique) {
				System.out.println("That email is taken. Please enter a different one:");
			}
		} while (!emailIsUnique);
		System.out.println("Please enter a city name:");
		String city = in.nextLine();
		System.out.println("Please enter a state:");
		String state = in.nextLine();
		System.out.println("Please enter a street address:");
		String addr = in.nextLine();
		System.out.println("Please enter a first name:");
		String fname = in.nextLine();
		System.out.println("Please enter a last name:");
		String lname = in.nextLine();
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Patron", "'"+email+"'", "'"+city+"'", "'"+state+"'", "'"+addr+"'", "'"+fname+"'", "'"+lname+"'");
		return new String[] {id};
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter a city name:");
		String city = in.nextLine();
		System.out.println("Please enter a state:");
		String state = in.nextLine();
		System.out.println("Please enter a street address:");
		String addr = in.nextLine();
		System.out.println("Please enter a first name:");
		String fname = in.nextLine();
		System.out.println("Please enter a last name:");
		String lname = in.nextLine();
		
		DBUtils.editRecord(conn, "Patron", 1, "Email", "'"+ids[0]+"'", "City", "'"+city+"'", "State", "'"+state+"'", "Address","'"+addr+"'","Fname","'"+fname+"'","Lname","'"+lname+"'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Patron WHERE Email="+"'"+ids[0]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in) {
        String userInput = "";
        String sql = "SELECT Email, City, State, Address, Fname, Lname FROM Patron WHERE Email = $value;";
	
		System.out.println("Please enter an email address to search for:");
		userInput = in.nextLine();
		sql = sql.replace("$value", "'"+userInput+"'");
		
		return DBUtils.searchAndSelect(conn, in, sql, 6, "Email");
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
		String[] ids = retrieve(conn, in);
		if (ids == null) {
			System.out.println("Looks like we couldn't find that patron.");
			return;
		}
		selectedMenuScreen.display();
		int menuSelection = selectedMenuScreen.getOption(in);
		
		switch (menuSelection) {
		case 1:
			delete(conn, in, ids);
			break;
		case 2:
			edit(conn, in, ids);
			break;
		case 3:
			CheckoutController.retrieveByPatron(conn, in, ids);
			System.out.println("Type \"1\" to go back.");
			DBUtils.getValidInput(1, 1, in);
			break;
		case 4:
			ReviewController.retrieveByPatron(conn, in, ids);
			System.out.println("Type \"1\" to go back.");
			DBUtils.getValidInput(1, 1, in);
			break;
		case 5:
			break;
		} 
	}
	
}
