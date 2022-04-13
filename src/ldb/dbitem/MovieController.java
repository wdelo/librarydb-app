package ldb.dbitem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import ldb.util.DBUtils;

public class MovieController implements DBItemController {

	private ActorController actorController;
	
	public MovieController(ActorController actor) {
		actorController = actor;
	}

	@Override
	public String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter the title of the movie:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the movie:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the movie:");
		String year = in.nextLine();
		System.out.println("Please enter the content rating of the movie (PG-13, R, etc.):");
		String cr = in.nextLine();
		System.out.println("Please enter the length of the movie (whole number of minutes):");
		int minutes = DBUtils.getValidInput(0, 999, in);
		String id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
		
		ArrayList<String> actorIds = new ArrayList<String>();
		
		System.out.println("Are there any actors in this movie that are already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's find some.\n");
			boolean done = false;
			do {
				System.out.println("What is the actor's name?");
				String actorName = in.nextLine();
				String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
		        sql = sql.replace("$value", "'"+actorName+"'");
				String[] actorId = DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");
				if (actorId != null) {
					actorIds.add(actorId[0]);
				}
				System.out.println("Are there any more actors in this movie that are already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
		
		System.out.println("Are there any actors in this movie that AREN'T already in the database?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's add some.\n");
			boolean done = false;
			do {
				String actorId = actorController.insert(conn, in)[0];
				if (actorId != null) {
					actorIds.add(actorId);
				}
				System.out.println("Are there any more actors in this movie that AREN'T already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
			
		DBUtils.insertRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.insertRecord(conn, "Movie", id, "'"+cr+"'", ""+minutes);
		
		for (int i = 0; i < actorIds.size(); i++)
			DBUtils.insertRecord(conn, "Contributes_To", id, "'"+actorIds.get(i)+"'", "'Actor'");
		
		return new String[] {id};
	}

	@Override
	public void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the title of the movie:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the movie:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the movie:");
		String year = in.nextLine();
		System.out.println("Please enter the content rating of the movie (PG-13, R, etc.):");
		String cr = in.nextLine();
		System.out.println("Please enter the length of the movie (whole number of minutes):");
		int minutes = DBUtils.getValidInput(0, 999, in);
		
		DBUtils.editRecord(conn, "Media", ids[0], "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.editRecord(conn, "Movie", ids[0], "'"+cr+"'", ""+minutes);
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
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Actor Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, ContentRating, Length FROM Movie JOIN Media ON MediaID = MovieID WHERE Title = $value;";
			System.out.println("Please enter a movie title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		case 2:
			sql = "SELECT Title, Genre, Year, ContentRating, Length FROM Movie JOIN Media ON MediaID = MovieID WHERE Genre = $value;";
			System.out.println("Please enter a movie genre to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;		
		case 3:
			sql = "SELECT Title, Genre, Year, ContentRating, Length FROM Movie JOIN Media ON MediaID = MovieID WHERE Year = $value;";
			System.out.println("Please enter a movie year to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, ContentRating, Length FROM Movie AS M JOIN Media AS Med ON Med.MediaID = M.MovieID "
				+"JOIN ContributesTo AS C ON M.MovieID = C.MediaID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+"WHERE Con.Name = $value;";
			System.out.println("Please enter an actor name to search for their movies:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		default:
			break;
		}
	}

	@Override
	public String[] retrieve(Connection conn, Scanner in) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Actor Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID WHERE Title = $value;";
			System.out.println("Please enter a movie title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID WHERE Genre = $value;";
			System.out.println("Please enter a movie genre to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;		
		case 3:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID WHERE Year = $value;";
			System.out.println("Please enter a movie year to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie AS M JOIN Media AS Med ON Med.MediaID = M.MovieID "
				+"JOIN Contributes_To AS C ON M.MovieID = C.MediaID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID "
					+"WHERE Con.Name = $value;";
			System.out.println("Please enter an actor name to search for their movies:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, in, sql, 5, "MovieID");
	}

}
