package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;

public class PatronController {

	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter an email address:");
		String email = "";
		boolean emailIsUnique = false;
		do {
			email = in.nextLine();
			emailIsUnique = !DBUtils.valueExists(conn, "Patron", "Email_Address", "'"+email+"'");
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
		
		DBUtils.editRecord(conn, "Patron", "'"+ids[0]+"'", "'"+city+"'", "'"+state+"'", "'"+addr+"'", "'"+fname+"'", "'"+lname+"'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Patron WHERE Email_Address="+"'"+ids[0]+"'");
		DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE Email_Address="+"'"+ids[0]+"'");
		DBUtils.deleteRecord(conn, "DELETE FROM Review WHERE Email_Address="+"'"+ids[0]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in) {
        String userInput = "";
        String sql = "SELECT Email_Address, City, State, Street_Addr, Fname, Lname FROM Patron WHERE $attribute = $value;";
	
		System.out.println("Please enter an email address to search for:");
		userInput = in.nextLine();
		sql = sql.replace("$attribute", "Email_Address");
		sql = sql.replace("$value", "'"+userInput+"'");
		
		return DBUtils.searchAndSelect(conn, in, sql, 6, "Email_Address");
	}

	public static void execute(Connection conn, Scanner in) {
		// TODO Auto-generated method stub
		
	}
	
	public static void view(Connection conn, Scanner in) {
		
	}


}
