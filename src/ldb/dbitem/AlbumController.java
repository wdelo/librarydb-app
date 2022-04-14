
package ldb.dbitem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class AlbumController {
	
	private static String movieMenuPrompt = "What would you like to manage with albums?";	
	private static String[] movieMenuScreenOptions = {
    		"Add an album", 
    		"Select an album",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this album?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this album",
			"Edit this album",
			"View condition log",
			"View reviews",
			"View authors",
			"Back",
	};
	
	private static String conditionMenuPrompt = "What changes would you like to make to these conditions?";
	private static String[] conditionMenuScreenOptions = {
			"Add a condition",
			"Delete a condition",
			"Edit a condition",
			"Back",
	};
	
	private static String reviewMenuPrompt = "What changes would you like to make to these reviews?";
	private static String[] reviewMenuScreenOptions = {
			"Add a review",
			"Delete a review",
			"Edit a review",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(movieMenuPrompt, movieMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	private static MenuScreen conditionMenuScreen = new MenuScreen(conditionMenuPrompt, conditionMenuScreenOptions);
	private static MenuScreen reviewMenuScreen = new MenuScreen(reviewMenuPrompt, reviewMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in) {
		System.out.println("Please enter the title of the album:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the album:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the album:");
		String year = in.nextLine();
		String id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
			
		ArrayList<String> artistIds = new ArrayList<String>();
			
		System.out.println("Are there any artists of this album that are already in the database?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's find some.\n");
			boolean done = false;
			do {
				System.out.println("What is the artist's name?");
				String artistName = in.nextLine();
				String sql = "SELECT Name, DOB, ContributorID FROM Contributor WHERE PrimaryRole = 'Artist' AND Name = $value;";
			    sql = sql.replace("$value", "'"+artistName+"'");
				String[] artistId = DBUtils.searchAndSelect(conn, in, sql, 2, "ContributorID");
				if (artistId != null) {
					artistIds.add(artistId[0]);
				}
				System.out.println("Are there any more artists of this album that are already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
			
		System.out.println("Are there any artists of this album that AREN'T already in the database?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			System.out.println("Let's add some.\n");
			boolean done = false;
			do {
				String artistId = ArtistController.insert(conn, in)[0];
				if (artistId != null) {
					artistIds.add(artistId);
				}
				System.out.println("Are there any more artists of this album that AREN'T already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2)
					done = true;
			} while (!done);
		}
				
		DBUtils.insertRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		DBUtils.insertRecord(conn, "Audio", id, "'a'");
			
		for (int i = 0; i < artistIds.size(); i++)
			DBUtils.insertRecord(conn, "Contributes_To", id, "'"+artistIds.get(i)+"'", "'Artist'");
		
		System.out.println("Would you like to add tracks to this album?\n1. Yes\n2. No");
		userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			boolean done = false;
			int count = 1;
			do {
				System.out.println("Please enter the title of the track:");
				String trackTitle = in.nextLine();
				System.out.println("Please enter the length of the track (in seconds):");
				int length = DBUtils.getValidInput(0, 9999, in);
				DBUtils.insertRecord(conn, "Track", "'"+id+"'", ""+count, "'"+trackTitle+"'", ""+length);
				count++;
				System.out.println("Would you like to add another?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 2) {
					done = true;
				}
			} while (!done);
		}
		return new String[] {id};
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the title of the album:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the album:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the album:");
		String year = in.nextLine();
		
		DBUtils.editRecord(conn, "Media", 1, "Media", ids[0], "Title", "'"+title+"'", "Genre", "'"+genre+"'", "Year", "'"+year+"'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Media WHERE MediaID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Audio WHERE AudioID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Contributes_To WHERE MediaID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Media_Instance WHERE MediaID="+ids[0]);
		DBUtils.deleteRecord(conn, "DELETE FROM Checkout WHERE MediaID="+ids[0]);
	}

	public static String[] retrieve(Connection conn, Scanner in) {

        String userInput = "";
        String sql = "";
        String id = "";
        

		sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Title = $value;";
		System.out.println("Please enter an album title to search for:");
		userInput = in.nextLine();
		sql = sql.replace("$value", "'"+userInput+"'");
	
		return  DBUtils.searchAndSelect(conn, in, sql, 3, "AudioID");
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
			// search and select album instances
			String sqlCond = "SELECT Title, CallNumber, [Digital/Physical], IsAvailable, Location FROM "
							+ "Media AS M JOIN MediaInstance AS I ON M.MediaID = I.MediaID WHERE M.MediaID = '"+ids[0]+"'"
							+ "AND [Digital/Physical] = 'p'";
			String[] instanceCondId = DBUtils.searchAndSelect(conn, in, sqlCond, 5, "CallNumber");
			// then execute condition using movie instances
			ConditionController.execute(conn, in, instanceCondId); // change ids to movie instance ids
			break;
		case 4:
			ReviewController.execute(conn, in, ids);
			break;
		case 5:
			ArtistController.execute(conn, in, ids);
			break;
		case 6:
			TrackController.execute(conn, in, ids);
			break;
		case 7:
			//do nothing
			break;
			}
	} 

}
