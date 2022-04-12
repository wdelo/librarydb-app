package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.util.DBUtils;

public class ReviewController implements DBItemController {

	@Override
	public String[] insert(Connection conn, Scanner in) {
		System.out.println("Let's find the patron who is submitting this review. Please enter their last name:");
		String lname = in.nextLine();
		
		String sql = "SELECT Email_Address, Fname, Lname FROM Patron WHERE Lname = '"+lname+"';";
		String email = DBUtils.searchAndSelect(conn, in, sql, "Email_Address", 3);
		
		System.out.println("Let's find the piece of media this patron is reviewing. Please enter its title:");
		String title = in.nextLine();
		
		sql = "SELECT Title, Genre, Year, MediaID FROM Media WHERE Title = '"+title+"';";
		String id = DBUtils.searchAndSelect(conn, in, sql, "MediaID", 3);
		
		if (email != null && id != null) {
			System.out.println("Please enter the rating (1-10) the review gave:");
			int rating = DBUtils.getValidInput(1, 10, in);
			System.out.println("Please enter the content of this review:");
			String content = in.nextLine();
			
			DBUtils.insertRecord(conn, "Review", "'"+email+"'", "'"+id+"'", ""+rating, "'"+content+"'");
			return new String[] { email, id };
		} else {
			System.out.println("Error inserting: nonexistant patron or media");
			return null;
		}
	}

	@Override
	public void edit(Connection conn, Scanner in, String[] ids) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Review WHERE Patron_Email="+ids[0]+" AND MediaID="+ids[1]);
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
			sql = "SELECT Title, Patron_Email, Rating, Review FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:
			sql = "SELECT Title, Patron_Email, Rating, Review FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE R.Patron_Email = $value;";
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
			sql = "SELECT Title, Patron_Email, Rating, Review, R.MediaID FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a media title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, Patron_Email, Rating, Review, R.MediaID FROM Review AS R JOIN Media AS M ON R.MediaID = M.MediaID "
					+ "JOIN Patron AS P ON P.Email_Address = R.Patron_Email WHERE Title = $value;";
			System.out.println("Please enter a patron email to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;		
		default:
			break;
		}
		return DBUtils.searchAndSelect2(conn, in, sql, "Patron_Email", "MediaID", 4);
	}

}
