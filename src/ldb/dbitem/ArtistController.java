package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class ArtistController {

	private static String artistMenuPrompt = "What would you like to manage with artists?";	
	private static String[] artistMenuScreenOptions = {
    		"Add an artist", 
    		"Select an artist",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this artists?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this artist",
			"Edit this artist",
			"Back",
	};
	
	private static String selectedMenuPrompt2 = "What would you like to do with this artists?";
	private static String[] selectedMenuScreenOptions2 = {
			"Delete this artist",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(artistMenuPrompt, artistMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	private static MenuScreen selectedMenuScreen2 = new MenuScreen(selectedMenuPrompt2, selectedMenuScreenOptions2);
	
	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the artist:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Artist'");
		return new String[]{id};
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the name of the artist:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		
		DBUtils.editRecord(conn, "Contributor", 1, "ContributorID", "'"+ids[0]+"'", "Name", "'"+name+"'", "Birthday", dob, "PrimaryRole", "'Artist'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Contributor WHERE ContributorID="+"'"+ids[0]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the artist to search for:");
        String userInput = in.nextLine();
        
        String sql = "SELECT Name, Birthday, ContributorID FROM Contributor WHERE PrimaryRole = 'Artist' AND Name = $value;";
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
		String[] artistId;
		System.out.println("Is the artist already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's find them.\n");		
			System.out.println("What is the artist's name?");
			String artistName = in.nextLine();
			String sql = "SELECT Name, Birthday, ContributorID FROM Contributor WHERE PrimaryRole = 'Artist' AND Name = $value;";
		    sql = sql.replace("$value", "'"+artistName+"'");
		    artistId = DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");	
		} else {
			System.out.println("Let's add them.\n");
			artistId = ArtistController.insert(conn, in);
		}
		DBUtils.insertRecord(conn, "ContributesTo", parentIds[0], "'"+artistId[0]+"'", "'Artist'");
	}
	
	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		// TODO search and select on parentIds and select an artist from the results
		
		String sql = "SELECT Name, Birthday, Role, C.ContributorID FROM Contributor AS C JOIN ContributesTo AS T ON"
        		+ " C.ContributorID = T.ContributorID JOIN Media AS M ON M.MediaID = T.MediaID WHERE M.MediaID = '"+parentIds[0]+"' "
        				+ "AND Role = 'Artist';";
        
        return DBUtils.searchAndSelect(conn, in, sql, 3, "ContributorID");
	}
	
	public static void execute(Connection conn, Scanner in, String[] parentIds) {
		menuScreen.display();
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
			selectedMenuScreen2.display();
			int menuSelection = selectedMenuScreen2.getOption(in);
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
