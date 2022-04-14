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
			"View condition log",
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
				String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Actor' AND Name = $value;";
		        sql = sql.replace("$value", "'"+actorName+"'");
				String actorId = DBUtils.searchAndSelect(conn, in, sql, "ContributorID", 2);
				if (actorId != null) {
					actorIds.add(actorId);
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
				String actorId = insert(conn, in)[0];
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
			DBUtils.insertRecord(conn, "Contributes_To", id, "'"+actorIds.get(i)+"'", "'Actor'");
		
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
		
		DBUtils.editRecord(conn, "Media", ids[0], "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.editRecord(conn, "Movie", ids[0], "'"+cr+"'", ""+minutes);
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Media WHERE MediaID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Audio WHERE AudioID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Contributes_To WHERE MediaID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Media_Instance WHERE MediaID="+ids[0]);
		//DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE MediaID="+ids[0]);
	}

	public static String[] retrieve(Connection conn, Scanner in) {
        String sql = "SELECT Title, Genre, Year, Content_Rating, Length, MovieID FROM Movie JOIN Media ON MediaID = MovieID WHERE Title = $value;";
		System.out.println("Please enter a movie title to search for:");
		String userInput = in.nextLine();
		sql = sql.replace("$value", "'"+userInput+"'");
		
		return new String[] {DBUtils.searchAndSelect(conn, in, sql, "MovieID", 5)};
	}

	public static void execute(Connection conn, Scanner in) {
		menuScreen.display();
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
			// condition log
			// search and select movie instances
			
			// then execute condition using movie instances
			ConditionController.execute(conn, in, ids); // change ids to movie instance ids
			break;
		case 4:
			// reviews
			// search and select review using selected movie id
			// ReviewController.insert(conn, in, ids);
			break;
		case 5:
			// actors
			ActorController.execute(conn, in, ids);
			break;
		case 6:
			// directors
	
			break;
		}
		
	}

}
