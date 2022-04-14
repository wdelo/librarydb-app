package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;

public class AuthorController {

	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the author:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		String id = DBUtils.getUniqueID(conn, "Contributor", "ContributorID", 9);
		
		DBUtils.insertRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Author'");
		return new String[] {id};
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the name of the author:");
		String name = in.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(in);
		
		//DBUtils.editRecord(conn, "Contributor", "'"+ids[0]+"'", "'"+name+"'", dob, "'Author'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Contributor WHERE ContributorID="+"'"+ids[0]+"'");
		DBUtils.deleteRecord(conn, "DELETE FROM Contributes_To WHERE ContributorID="+"'"+ids[0]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the author to search for:");
        String userInput = in.nextLine();
        
        String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Author' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        return DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");
	}
        

	public static void execute(Connection conn, Scanner in) {
		// TODO Auto-generated method stub
		
	}
	
	public static void view(Connection conn, Scanner in) {
		
	}
	
	public static String[] insert(Connection conn, Scanner in, String[] parentIds) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		// TODO search and select on parentIds and select an author from the results
		
        return null;
	}

	public static void execute(Connection conn, Scanner in, String[] parentIds) {
		// TODO Auto-generated method stub
		
	}
	
	
	public static void view(Connection conn, Scanner in, String[] parentIds) {
		
	}
}
