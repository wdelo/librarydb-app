package ldb.dbitem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import ldb.util.DBUtils;

public class BookController implements DBItemController {

	private AuthorController authorController;
	
	public BookController(AuthorController author) {
		authorController = author;
	}

	@Override
	public String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter the title of the book:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the book:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the book:");
		String year = in.nextLine();
		String id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
			
		ArrayList<String> authorIds = new ArrayList<String>();
			
		System.out.println("Are there any authors of this book that are already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's find some.\n");
			boolean done = false;
			do {
				System.out.println("What is the author's name?");
				String authorName = in.nextLine();
				String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Author' AND Name = $value;";
			    sql = sql.replace("$value", "'"+authorName+"'");
				String authorId = DBUtils.searchAndSelect(conn, in, sql, "ContributorID", 2);
				if (authorId != null) {
					authorIds.add(authorId);
				}
				System.out.println("Are there any more authors of this book that are already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
			
		System.out.println("Are there any authors of this book that AREN'T already in the database?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's add some.\n");
			boolean done = false;
			do {
				String authorId = authorController.insert(conn, in)[0];
				if (authorId != null) {
					authorIds.add(authorId);
				}
				System.out.println("Are there any more authors of this book that AREN'T already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
				
		DBUtils.insertRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.insertRecord(conn, "Audio", id, "'b'");
			
		for (int i = 0; i < authorIds.size(); i++)
			DBUtils.insertRecord(conn, "Contributes_To", id, "'"+authorIds.get(i)+"'", "'Author'");
		
		return new String[] {id};
	}

	@Override
	public void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the title of the book:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the book:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the book:");
		String year = in.nextLine();
		
		DBUtils.editRecord(conn, "Media", ids[0], "'"+title+"'", "'"+genre+"'", "'"+year+"'");
	}

	@Override
	public void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Media WHERE MediaID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Audio WHERE AudioID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Contributes_To WHERE MediaID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Media_Instance WHERE MediaID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE MediaID="+ids[0]);
	}

	@Override
	public void search(Connection conn, Scanner in) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Author Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Title = $value;";
			System.out.println("Please enter a book title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 2:	
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Genre = $value;";
			System.out.println("Please enter a book genre to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 3:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Year = $value;";
			System.out.println("Please enter a book year to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.retrieveRows(conn, sql);
			break;
		case 4:
			sql = "SELECT Title, Genre, Year FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+ "WHERE [Album/Audiobook] = 'b' AND Con.Name = $value;";
			System.out.println("Please enter an author name to search for their books:");
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
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Author Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Title = $value;";
			System.out.println("Please enter a book title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:	
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Genre = $value;";
			System.out.println("Please enter a book genre to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 3:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'b' AND Year = $value;";
			System.out.println("Please enter a book year to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+ "WHERE [Album/Audiobook] = 'b' AND Con.Name = $value;";
			System.out.println("Please enter an author name to search for their books:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return new String[] {DBUtils.searchAndSelect(conn, in, sql, "AudioID", 3)};
	}

}
