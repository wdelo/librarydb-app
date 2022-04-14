package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class ConditionController {

	private static String conditionMenuPrompt = "What would you like to manage with this condition log?";	
	private static String[] conditionMenuScreenOptions = {
    		"Add a log entry", 
    		"Select a log entry",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this log entry?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this entry",
			"Edit this entry",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(conditionMenuPrompt, conditionMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static void insert(Connection conn, Scanner in, String[] parentIds) {
		
		System.out.println("Let's get the date for this log entry.");
		String date = DBUtils.getFormattedDate(in);
		while (DBUtils.valueExists(conn, "Condition", "Date", date, "CallNumber", "'"+parentIds[0]+"'")) {
			System.out.println("Duplicate entry - please provide a different date.");
			date = DBUtils.getFormattedDate(in);
		}
		System.out.println("What condition is this piece of media in? (Good, poor, etc.);");
		String condition = in.nextLine();
		System.out.println("Is this piece of media reported as missing?\n1. Yes\n2. No");
		int missing = DBUtils.getValidInput(1, 2, in);
		if (missing == 2)
			missing = 0;
		DBUtils.insertRecord(conn, "Condition", date, "'"+condition+"'", ""+missing, "'"+parentIds[0]+"'");
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("What condition is this piece of media in? (Good, poor, etc.);");
		String condition = in.nextLine();
		System.out.println("Is this piece of media reported as missing?\n1. Yes\n2. No");
		int missing = DBUtils.getValidInput(1, 2, in);
		if (missing == 2)
			missing = 0;
		DBUtils.editRecord(conn, "Condition", 2, "Date", "'"+ids[0]+"'", "CallNumber", "'"+ids[1]+"'", "Damage", "'"+condition+"'", "IsMissing", ""+missing );
	}
	
	public static void delete(Connection conn, Scanner in, String[] ids) {	
		DBUtils.deleteRecord(conn, "DELETE FROM Condition WHERE Date="+"'"+ids[0]+"'"+" AND CallNumber="+"'"+ids[1]+"'");
	}
	
	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		
		String sql = "SELECT Title, C.CallNumber, Damage, IsMissing, Date FROM Condition AS C JOIN MediaInstance AS M ON C.CallNumber = M.CallNumber"
				+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE C.CallNumber = "+"'"+parentIds[0]+"'";

		return DBUtils.searchAndSelect(conn, in, sql, 5, "Date", "CallNumber");
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
			System.out.println("No condition logs :(\nType \"1\" to continue to the main menu.");
			DBUtils.getValidInput(1, 1, in);
		}
	}
}
