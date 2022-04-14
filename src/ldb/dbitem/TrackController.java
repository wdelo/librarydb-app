package ldb.dbitem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;

public class TrackController {

	public static String[] insert(Connection conn, Scanner in, String[] parentIds) {
		System.out.println("Please enter the title of the track:");
		String title = in.nextLine();
		System.out.println("Please enter the length of the track (in seconds):");
		int length = DBUtils.getValidInput(0, 9999, in);
		
		int trackNum = 0;
		String id = "";
		
		System.out.println("Is this track part of an album?\n1. Yes\n2. No");
		int userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			do {
				System.out.println("Is this album already in the database?\n1. Yes\n2. No");
				userChoice = DBUtils.getValidInput(1, 2, in);
				if (userChoice == 1) {
					System.out.println("Let's find it.\n");
					System.out.println("What is the name of the album?");
					String albumName = in.nextLine();
					String sql = "SELECT Title, Genre, Year, AudioID FROM Media JOIN Audio ON MediaID = AudioID "
						+ "WHERE [Album/Audiobook] = 'a' AND Title = $value";
					sql = sql.replace("$value", "'"+albumName+"'");
					String[] potentialID = DBUtils.searchAndSelect(conn, in, sql, 3, "AudioID");
					if (potentialID != null)
						id = potentialID[0];
				} else {
					System.out.println("Let's add it.");
					System.out.println("What is the title of this album?");
					String albumTitle = in.nextLine();
					System.out.println("What genre is this album?");
					String genre = in.nextLine();
					System.out.println("What year was this album released?");
					String year = in.nextLine();
					id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
					DBUtils.insertRecord(conn, "Media", "'"+id+"'", "'"+albumTitle+"'", "'"+genre+"'", "'"+year+"'");
					DBUtils.insertRecord(conn, "Audio", "'"+id+"'", "'a'");
				}
			} while (id == null);
			trackNum = DBUtils.getTrackCount(conn, "'"+id+"'") + 1;
		} else {
			System.out.println("What genre is this track?");
			String genre = in.nextLine();
			System.out.println("What year was this track released?");
			String year = in.nextLine();
			id = DBUtils.getUniqueID(conn, "Media", "MediaID", 9);
			DBUtils.insertRecord(conn, "Media", "'"+id+"'", "'"+title+"'", "'"+genre+"'", "'"+year+"'");
			DBUtils.insertRecord(conn, "Audio", "'"+id+"'", "'a'");
			trackNum = 1;
		}
		
		DBUtils.insertRecord(conn, "Track", "'"+id+"'", ""+trackNum, "'"+title+"'", ""+length);
		
		return new String[] {id, Integer.toString(trackNum)};
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the title of the track:");
		String title = in.nextLine();
		System.out.println("Please enter the length of the track (in seconds):");
		int length = DBUtils.getValidInput(0, 9999, in);
		
		try {					
			String sql = "UPDATE INTO Track SET Track_Title="+title+", Length="+length
			+ "WHERE AudioID=" + ids[0] + " AND Track_Number="+ids[1];    
	        PreparedStatement p = conn.prepareStatement(sql);
	        p.executeUpdate();  	
	       	p.close();
	       	System.out.println("Insertion successful.");
	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {
		DBUtils.deleteRecord(conn, "DELETE FROM Track WHERE AudioID="+"'"+ids[0]+"'"+" AND Track_Number="+"'"+ids[1]+"'");
	}

	public static String[] retrieve(Connection conn, Scanner in, String[] parentIds) {
		System.out.println("What would you like to search by?");
        System.out.println("1. Title\n2. Album Name\n");
        int userChoice = DBUtils.getValidInput(1, 2, in);
		
        String userInput = "";
        String sql = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Track_Title, Length, AudioID, Track_Number FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID "
	        		+"WHERE [Album/Audiobook] = 'a' AND Track_Title = $value;";
			System.out.println("Please enter a track title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Track_Title, Length, AudioID, Track_Number FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID JOIN Media ON A.AudioID = MediaID "
	        		+"WHERE [Album/Audiobook] = 'a' AND $attribute = $value;";
			System.out.println("Please enter an album name to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$attribute", "Title");
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, in, sql, 2, "AudioID", "Track_Number");
	}
	
	public static void execute(Connection conn, Scanner in, String[] parentIds) {
		// TODO Auto-generated method stub
		
	}
	
	public static void view(Connection conn, Scanner in, String[] parentIds) {
		
	}


}
