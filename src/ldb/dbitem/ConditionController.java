package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;

public class ConditionController {

	public static String[] insert(Connection conn, Scanner in, String[] parentIds) {
		System.out.println("Let's find the piece of media this condition will be applied to. Please enter its title:");
		String title = in.nextLine();
		
		String sql = "SELECT Call_Number, Title, Genre, Year, Availability FROM Media_Instance AS MI JOIN Media AS M ON MI.MediaID = M.MediaID "
				+ "WHERE Title = '"+title+"';";
		String callNum = DBUtils.searchAndSelect(conn, in, sql, "Call_Number", 4);
		
		if (callNum != null) {
			System.out.println("Let's get the date this condition is associated with.");
			String date = DBUtils.getFormattedDate(in);
			System.out.println("What condition is this piece of media in? (Good, poor, etc.);");
			String condition = in.nextLine();
			System.out.println("Is this piece of media reported as missing?\n1. Yes\n2. No");
			int missing = DBUtils.getValidInput(1, 2, in);
			if (missing == 2)
				missing = 0;
			System.out.println("What is the reason for the current condition of this piece of media?");
			String reason = in.nextLine();
			DBUtils.insertRecord(conn, "Condition", date, "'"+callNum+"'", "'"+condition+"'", ""+missing, "'"+reason+"'");
			return new String[] {date, callNum};
		} else {
			System.out.println("Error inserting: nonexistant media OR no available media");
			return null;
		}
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		// TODO Auto-generated method stub
		
	}
	
	public static void delete(Connection conn, Scanner in, String[] ids) {	
		DBUtils.deleteRecord(conn, "DELETE FROM Condition WHERE Date="+ids[0]+" AND Call_Number="+ids[1]);
	}
	
	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		
		String sql = "SELECT Title, C.Call_Number, Damage, Missing_Status, Date FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
				+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE C.CallNumber = "+parentIds[0];

		return new String[] {DBUtils.searchAndSelect(conn, in, sql, "MovieID", 5)};
	}

	public static void execute(Connection conn, Scanner in, String[] parentIds) {
		//menuScreen.display();
		int menuSelection = 0;//menuScreen.getOption(in);
		switch (menuSelection) {
		case 1:
			insert(conn, in, parentIds);
			break;
		case 2:
			view(conn, in, parentIds);
			break;
		}
		
	}
	
	public static void view(Connection conn, Scanner in, String[] parentIds) {
		String[] ids = retrieve(conn, in, parentIds);
		//selectedMenuScreen.display();
		int menuSelection = 0;//selectedMenuScreen.getOption(in);
		switch (menuSelection) {
		case 1:
			delete(conn, in, ids);
			break;
		case 2:
			edit(conn, in, ids);
			break;
		}
		
	}

}
