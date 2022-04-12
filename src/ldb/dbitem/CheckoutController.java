package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.util.DBUtils;

public class CheckoutController implements DBItemController {

	@Override
	public String[] insert(Connection conn, Scanner in) {
		System.out.println("Let's find the patron who is checking out. Please enter their last name:");
		String lname = in.nextLine();
		
		String sql = "SELECT Email_Address, Fname, Lname FROM Patron WHERE Lname = '"+lname+"';";
		String email = DBUtils.searchAndSelect(conn, in, sql, "Email_Address", 3);
		
		System.out.println("Let's find the piece of media this patron is checking out. Please enter its title:");
		String title = in.nextLine();
		
		sql = "SELECT Call_Number, Title, Genre, Year FROM Media_Instance AS MI JOIN Media AS M ON MI.MediaID = M.MediaID "
				+ "WHERE Title = '"+title+"' AND Availability = 1;";
		String callNum = DBUtils.searchAndSelect(conn, in, sql, "Call_Number", 4);
		
		if (email != null && callNum != null) {
			System.out.println("Let's get an initial checkout date.");
			String date = DBUtils.getFormattedDate(in);
			System.out.println("Has this checkout been returned yet?\n1. Yes\n2. No");
			int userChoice = DBUtils.getValidInput(1, 2, in);
			if (userChoice == 1) {
				System.out.println("Let's get a return date.");
				String returnDate = DBUtils.getFormattedDate(in);
				DBUtils.insertRecord(conn, "Checkout", "'"+callNum+"'", "'"+email+"'", date, returnDate);
			} else {
				DBUtils.insertRecord(conn, "Checkout", "'"+callNum+"'", "'"+email+"'", date, "NULL");
			}	
			
			return new String[] { callNum, email, date };
		} else {
			System.out.println("Error inserting: nonexistant patron or media OR no available media");
			
			return null;
		}

	}

	@Override
	public void edit(Connection conn, Scanner in, String[] ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE Call_Number="+ids[0]+" AND Email_Address="+ids[1]+" AND Checkout_date="+ids[2]);
	}

	@Override
	public void search(Connection conn, Scanner in) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Patron Email\n");
        int userChoice = DBUtils.getValidInput(1, 2, in);
		
        String userInput = "";
        String sql = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE C.Email_Address = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;		
		default:
			break;
		}
	}

	@Override
	public String[] retrieve(Connection conn, Scanner in) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Media Title\n2. Patron Email\n");
        int userChoice = DBUtils.getValidInput(1, 2, in);
		
        String userInput = "";
        String sql = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, C.Call_Number, C.Email_Address, Checkout_date, Return_Date "
					+ "FROM Checkout AS C JOIN Media_Instance AS M ON C.Call_Number = M.Call_Number "
					+ "JOIN Patron AS P ON P.Email_Address = C.Email_Address "
					+ "JOIN Media AS Med ON M.MediaID = Med.MediaID "
					+ "WHERE C.Email_Address = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;		
		default:
			break;
		}
		return DBUtils.searchAndSelect3(conn, in, sql, "Call_Number", "Email_Address", "Checkout_date", 4);
	}

}
