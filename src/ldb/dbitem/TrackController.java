package ldb.dbitem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class TrackController {

	private static String menuPrompt = "What would you like to manage with this audio's tracks?";	
	private static String[] menuScreenOptions = {
    		"Add a track", 
    		"View tracks",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this track?";
	private static String[] selectedMenuScreenOptions = {
			"Delete this track",
			"Edit this track",
			"Back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(menuPrompt, menuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in, String[] parentIds) {
		System.out.println("Please enter the title of the track:");
		String title = in.nextLine();
		System.out.println("Please enter the length of the track (in seconds):");
		int length = DBUtils.getValidInput(0, 9999, in);
		int trackNum = DBUtils.getTrackCount(conn, "'"+parentIds[0]+"'") + 1;
		
		DBUtils.insertRecord(conn, "Track", "'"+parentIds[0]+"'", ""+trackNum, "'"+title+"'", ""+length);
		
		return new String[] {parentIds[0], Integer.toString(trackNum)};
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the title of the track:");
		String title = in.nextLine();
		System.out.println("Please enter the length of the track (in seconds):");
		int length = DBUtils.getValidInput(0, 9999, in);
		
		DBUtils.editRecord(conn, "Track", 1, "TrackTitle", "'"+title+"'", "Length","'"+length+"'");
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM 1rack WHERE AudioID="+"'"+ids[0]+"'"+" AND TrackNumber="+"'"+ids[1]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {

        String sql = "SELECT TrackTitle, Length, T.AudioID, TrackNumber FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID "
	        		+"WHERE [Album/Audiobook] = 'a' AND T.AudioID = '"+parentIds[0]+"'";
			
		return DBUtils.searchAndSelect(conn, in, sql, 2, "AudioID", "TrackNumber");
	}
	
	public static void execute(Connection conn, Scanner in, String[] parentIds) {
		menuScreen.display();
		int menuSelection = menuScreen.getOption(in);
		switch (menuSelection) {
		case 1:
			insert(conn, in, parentIds);
			break;
		case 2:
			view(conn, in, parentIds);
			break;
		case 3:
			break;
		}
		
	}
	
	public static void view(Connection conn, Scanner in, String[] parentIds) {
		String[] ids = retrieve(conn, in, parentIds);
		if (ids == null) {
			System.out.println("Looks like there are no tracks for that audio.");
			return;
		}
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
			break;
		}
	}
	
}
