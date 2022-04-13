package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.util.DBUtils;

public class ConditionController implements DBItemController {

	@Override
	public String[] insert(Connection conn, Scanner in) {
		System.out.println("Let's find the piece of media this condition will be applied to. Please enter its title:");
		String title = in.nextLine();
		
		String sql = "SELECT Call_Number, Title, Genre, Year, Availability FROM Media_Instance AS MI JOIN Media AS M ON MI.MediaID = M.MediaID "
				+ "WHERE Title = '"+title+"';";
		String[] callNum = DBUtils.searchAndSelect(conn, in, sql, 4, "Call_Number");
		
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
			return new String[] {date, callNum[0]};
		} else {
			System.out.println("Error inserting: nonexistant media OR no available media");
			return null;
		}
	}

	@Override
	public void edit(Connection conn, Scanner in, String[] ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Connection conn, Scanner in, String[] ids) {	
		DBUtils.deleteRecord(conn, "DELETE FROM Condition WHERE Date="+ids[0]+" AND Call_Number="+ids[1]);
	}

	@Override
	public void search(Connection conn, Scanner in) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Damage\n3. Missing Status");
        int userChoice = DBUtils.getValidInput(1, 3, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Damage = $value;";
			System.out.println("Please enter a damage condition (Good, Poor, etc.) to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;	
		case 3: 
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Missing_Status = $value;";
			System.out.println("Would you like to search for:\n1. Missing pieces of media\n2. Non-missing pieces of media");
			int missing = DBUtils.getValidInput(1, 2, in);
			sql = sql.replace("$value", "'"+Math.abs(missing-2)+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		default:
			break;
		}
	}

	@Override
	public String[] retrieve(Connection conn, Scanner in) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Damage\n3. Missing Status");
        int userChoice = DBUtils.getValidInput(1, 3, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status, Date FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status, Date FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Damage = $value;";
			System.out.println("Please enter a damage condition (Good, Poor, etc.) to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;	
		case 3: 
			sql = "SELECT Title, C.Call_Number, Damage, Missing_Status, Date FROM Condition AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number"
					+ " JOIN Media AS Med ON M.MediaID = Med.MediaID WHERE Missing_Status = $value;";
			System.out.println("Would you like to search for:\n1. Missing pieces of media\n2. Non-missing pieces of media");
			int missing = DBUtils.getValidInput(1, 2, in);
			sql = sql.replace("$value", "'"+Math.abs(missing-2)+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, in, sql, 3, "Date", "Call_Number");
	}

}
