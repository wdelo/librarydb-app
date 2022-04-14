package ldb.dbitem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class MovieController {
	
	private static String movieMenuPrompt = "What would you like to manage with movies?";	
	private static String[] movieMenuScreenOptions = {
    		"Add a movie", 
    		"Select a movie",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this movie?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this movie",
			"Edit this movie",
			"View condition logs",
			"View reviews",
			"View actors",
			"View director(s)",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(movieMenuPrompt, movieMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in) {
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
				String sql = "SELECT Name, Birthday, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
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
				String actorId = ActorController.insert(conn, in)[0];
				if (actorId != null) {
					actorIds.add(actorId);
				}
				System.out.println("Are there any more actors in this movie that AREN'T already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
		
		for (int i = 0; i < actorIds.size(); i++)
			DBUtils.insertRecord(conn, "ContributesTo", id, "'"+actorIds.get(i)+"'", "'Actor'");
		
		DBUtils.insertRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.insertRecord(conn, "Movie", id, "'"+cr+"'", ""+minutes);
		
		return new String[] {id};
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the title of the movie:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the movie:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the movie:");
		String year = in.nextLine();
		System.out.println("Please enter the content rating of the movie (PG-13, R, etc.):");
		String cr = in.nextLine();		System.out.println("Please enter the length of the movie (whole number of minutes):");
		int minutes = DBUtils.getValidInput(0, 999, in);
		
		DBUtils.editRecord(conn, "Media", 1, "MediaID", ids[0], "Title", "'"+title+"'", "Genre", "'"+genre+"'", "Year", "'"+year+"'");
		DBUtils.editRecord(conn, "Movie", 1, "MovieID", ids[0], "ContentRating", "'"+cr+"'", "Length", ""+minutes);
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Media WHERE MediaID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Audio WHERE AudioID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Contributes_To WHERE MediaID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Media_Instance WHERE MediaID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE MediaID="+ids[0]);
	}

	public static String[] retrieve(Connection conn, Scanner in) {
		System.out.println("Would you like to:\n1. View all movies\n2. Search for a movie");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		
		String sql = "SELECT Title, Genre, Year, ContentRating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID;";
		if (userChoice == 2) {
			System.out.println("Please enter a movie title to search for:");
			String userInput = in.nextLine();
			sql = sql.replace(";", " WHERE Title = '"+userInput+"';");
		}       
		
        DBUtils.blank();
	
		return DBUtils.searchAndSelect(conn, in, sql, 5, "MovieID");
	}

	public static void execute(Connection conn, Scanner in) {
		menuScreen.displayBlank();
		int menuSelection = menuScreen.getOption(in);
		switch (menuSelection) {
		case 1:
			insert(conn, in);
			break;
		case 2:
			view(conn, in);
			break;
		}
		
	}

	public static void view(Connection conn, Scanner in) {
		String[] ids = retrieve(conn, in);
		if (ids != null) {
			selectedMenuScreen.display();
			int menuSelection = selectedMenuScreen.getOption(in);
			switch (menuSelection) {
			case 1:
				delete(conn, in, ids);
				break;
			case 2:
				edit(conn, in, ids);
				break;
			case 3:
				String sql = "SELECT Title, CallNumber, [Digital/Physical], IsAvailable, Location FROM "
						+ "Media AS M JOIN MediaInstance AS I ON M.MediaID = I.MediaID WHERE M.MediaID = '"+ids[0]+"'"
								+ "AND [Digital/Physical] = 'p'";
				System.out.println("Which specific copy of this movie would you like to manage the condition log for?");
				String[] callNumber = DBUtils.searchAndSelect(conn, in, sql, 5, "CallNumber");
				
				if (callNumber != null)
					ConditionController.execute(conn, in, callNumber);
				else {
					DBUtils.blank();
					System.out.println("No condition logs for this movie :(\nType \"1\" to continue to the main menu.");
					DBUtils.getValidInput(1, 1, in);
				}
				break;
			case 4:
				ReviewController.execute(conn, in, ids);
				break;
			case 5:
				// actors
				ActorController.execute(conn, in, ids);
				break;
			case 6:
				// directors
				DirectorController.execute(conn, in, ids);
				break;
			}
		} else {
			System.out.println("Couldn't find that movie :(");
		}
		
	}

}
