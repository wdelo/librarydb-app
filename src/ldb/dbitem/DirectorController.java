package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class DirectorController{

	private static String directorMenuPrompt = "What would you like to manage with directors?";	
	private static String[] directorMenuScreenOptions = {
    		"Add an director", 
    		"Select an director",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this director?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this director",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(directorMenuPrompt, directorMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the director:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Director'");
		return new String[]{ id };
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the name of the actor:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		
		DBUtils.editRecord(conn, "Contributor", 1, "ContributorID", "'"+ids[0]+"'", "Name", "'"+name+"'", "Birthday", dob, "PrimaryRole", "'Director'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Contributor WHERE ContributorID="+"'"+ids[0]+"'");
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

	public static String[] retrieve(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the director to search for:");
        String userInput = in.nextLine();
        
        String sql = "SELECT Name, Birthday, ContributorID FROM Contributor WHERE PrimaryRole = 'Director' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        return DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");
	}

	public static void insert(Connection conn, Scanner in, String[] parentIds) {
		String[] directorId;
		System.out.println("Is the actor already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's find them.\n");		
			System.out.println("What is the director's name?");
			String directorName = in.nextLine();
			String sql = "SELECT Name, Birthday, ContributorID FROM Contributor WHERE PrimaryRole = 'Director' AND Name = $value;";
		    sql = sql.replace("$value", "'"+directorName+"'");
		    directorId = DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");	
		} else {
			System.out.println("Let's add them.\n");
			directorId = DirectorController.insert(conn, in);
		}
		DBUtils.insertRecord(conn, "ContributesTo", parentIds[0], "'"+directorId[0]+"'", "'Director'");
	}
	
	public static void delete(Connection conn, Scanner in, String[] ids, String[] parentIds) {
		
		String sql = "DELETE FROM ContributesTo WHERE ContributorID = "+ids[0]+" AND MediaID = "+parentIds[0]+";";
		DBUtils.deleteRecord(conn, sql);
		
	}
	
	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		String sql = "SELECT Name, Birthday, Role, C.ContributorID FROM Contributor AS C JOIN ContributesTo AS T ON"
        		+ " C.ContributorID = T.ContributorID JOIN Media AS M ON M.MediaID = T.MediaID WHERE M.MediaID = '"+parentIds[0]+"' "
        				+ "AND Role = 'Director';";
        
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
			default:
				break;
			}
		}
	}
	


}
