package ldb.dbitem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class ActorController {
	
	private static String actorMenuPrompt = "What would you like to manage with actors?";	
	private static String[] actorMenuScreenOptions = {
    		"Add an actor", 
    		"Select an actor",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this actor?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this actor",
			"Edit this actor",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(actorMenuPrompt, actorMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the actor:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Actor'");
		return new String[]{ id };
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the name of the actor:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		
		DBUtils.editRecord(conn, "Contributor", 1, "ContributorID", "'"+ids[0]+"'", "Name", "'"+name+"'", "Birthday", dob, "PrimaryRole", "'Actor'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Contributor WHERE ContributorID="+"'"+ids[0]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the actor to search for:");
        String userInput = in.nextLine();
        
        String sql = "SELECT Name, Birthday, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        return DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");
	}
	
	public static void execute(Connection conn, Scanner in) {
		menuScreen.displayBlank();
		int menuSelection = menuScreen.getOption(in);
		switch (menuSelection) {
		case 1:
			insert(conn, in);
			break;
		case 2:
			view(conn, in);
			break;
		default:
			break;
		}		
	}
	
	public static void view(Connection conn, Scanner in) {
		String[] ids = retrieve(conn, in);
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
		}
	}
	
	public static void delete(Connection conn, Scanner in, String[] ids, String[] parentIds) {
		
		String sql = "DELETE FROM ContributesTo WHERE ContributorID = "+ids[0]+" AND MediaID = "+parentIds[0]+";";
		DBUtils.deleteRecord(conn, sql);
		
	}

	public static void insert(Connection conn, Scanner in, String[] parentIds) {

		String[] actorId;
		System.out.println("Is the actor already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's find them.\n");		
			System.out.println("What is the actor's name?");
			String actorName = in.nextLine();
			String sql = "SELECT Name, Birthday, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
		    sql = sql.replace("$value", "'"+actorName+"'");
			actorId = DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");	
		} else {
			System.out.println("Let's add them.\n");
			actorId = ActorController.insert(conn, in);
		}
		if (actorId != null)
		System.out.println("Is this actor a:\n1. Main actor\n2. Supporting Actor");
		userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1)
			DBUtils.insertRecord(conn, "ContributesTo", parentIds[0], "'"+actorId[0]+"'", "'Main Actor'");
		else
			DBUtils.insertRecord(conn, "ContributesTo", parentIds[0], "'"+actorId[0]+"'", "'Supporting Actor'");
		
	}

	public static void edit(Connection conn, Scanner in, String[] ids, String[] parentIds) {
		System.out.println("Please enter the actor's role:");
		String role = in.nextLine();

		DBUtils.editRecord(conn, "ContributesTo", 2, "ContributorID", "'"+ids[0]+"'", "MediaID", "'"+ids[0]+"'",
				"Role", "'"+role+"'");
	}
	
	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		// TODO search and select on parentIds and select an Actor from the results
        
        String sql = "SELECT Name, Birthday, Role, C.ContributorID FROM Contributor AS C JOIN ContributesTo AS T ON"
        		+ " C.ContributorID = T.ContributorID JOIN Media AS M ON M.MediaID = T.MediaID WHERE M.MediaID = '"+parentIds[0]+"' "
        				+ "AND Role LIKE '%Actor';";
        
        return DBUtils.searchAndSelect(conn, in, sql, 3, "ContributorID");
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
				delete(conn, in, ids, parentIds);
				break;
			case 2:
				edit(conn, in, ids, parentIds);
				break;
			default:
				break;
			}
		}
	}

}
