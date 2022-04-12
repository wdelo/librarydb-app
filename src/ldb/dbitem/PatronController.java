package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.util.DBUtils;

public class PatronController implements DBItemController {

	@Override
	public String[] insert(Connection conn, Scanner in) {
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

	@Override
	public void edit(Connection conn, Scanner in, String[] ids) {
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

	@Override
	public void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Patron WHERE Email_Address="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE Email_Address="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Review WHERE Email_Address="+ids[0]);
	}

	@Override
	public void search(Connection conn, Scanner in) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Email Address\n2. City\n3. State\n4. Last Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "SELECT Email_Address, City, State, Street_Addr, Fname, Lname FROM Patron WHERE $attribute = $value;";
		switch (userChoice) {
		case 1:
			System.out.println("Please enter an email address to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "Email_Address");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			System.out.println("Please enter a city to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "City");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 3:
			System.out.println("Please enter a state to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "State");
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 4:
			System.out.println("Please enter a last name to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "Lname");
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
        System.out.println("1. Email Address\n2. City\n3. State\n4. Last Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "SELECT Email_Address, City, State, Street_Addr, Fname, Lname FROM Patron WHERE $attribute = $value;";
		switch (userChoice) {
		case 1:
			System.out.println("Please enter an email address to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "Email_Address");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			System.out.println("Please enter a city to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "City");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 3:
			System.out.println("Please enter a state to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "State");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			System.out.println("Please enter a last name to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "Lname");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return new String[] {DBUtils.searchAndSelect(conn, in, sql, "Email_Address", 6)};
	}

}
