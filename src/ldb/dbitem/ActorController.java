package ldb.dbitem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;

public class ActorController {
	
	
	
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
		
		//DBUtils.editRecord(conn, "Contributor", "'"+ids[0]+"'", "'"+name+"'", dob, "'Actor'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Contributor WHERE ContributorID="+"'"+ids[0]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in) {
		System.out.println("Please enter the name of the actor to search for:");
        String userInput = in.nextLine();
        
        String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        return DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");
	}
	
	public static void execute(Connection conn, Scanner in) {
		// TODO Auto-generated method stub
		
	}
	
	public static void view(Connection conn, Scanner in) {
		
	}

	public static String[] insert(Connection conn, Scanner in, String[] parentIds) {

		String actorId = insert(conn, in)[0];
		DBUtils.insertRecord(conn, "Contributes_To", parentIds[0], "'"+actorId+"'", "'Actor'");
		
		return new String[] { actorId };
	}

	public static void edit(Connection conn, Scanner in, String[] ids, String[] parentIds) {
		// TODO Auto-generated method stub
		// only edit for actor's role not anything else
	}
	
	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		// TODO search and select on parentIds and select an Actor from the results
		
		System.out.println("Please enter the name of the actor to search for:");
        String userInput = in.nextLine();
        
        String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
        sql = sql.replace("$value", "'"+userInput+"'");
        
        return DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");
	}


	public static void execute(Connection conn, Scanner in, String[] parentIds) {
		// TODO Auto-generated method stub
		
	}
	
	public static void view(Connection conn, Scanner in, String[] parentIds) {
		
	}

}
