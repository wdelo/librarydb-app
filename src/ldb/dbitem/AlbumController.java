package ldb.dbitem;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;

import ldb.util.DBUtils;

public class AlbumController implements DBItemController {

	private ArtistController artistController;
	
	public AlbumController(ArtistController artist) {
		artistController = artist;
	}

	@Override
	public String[] insert(Connection conn, Scanner in) {
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
				String artistId = artistController.insert(conn, in)[0];
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

	@Override
	public void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Please enter the title of the album:");
		String title = in.nextLine();
		System.out.println("Please enter the genre of the album:");
		String genre = in.nextLine();
		System.out.println("Please enter the year of the album:");
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
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Artist Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "";
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Title = $value;";
			System.out.println("Please enter an album title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		case 2:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Genre = $value;";
			System.out.println("Please enter an album genre to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		case 3:
			sql = "SELECT Title, Genre, Year FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Year = $value;";
			System.out.println("Please enter an album year to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			DBUtils.printRows(conn, sql, 99);
			break;
		case 4:
			sql = "SELECT Title, Genre, Year FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID"
					+ " WHERE A.[Album/Audiobook] = 'a' AND Con.Name = $value;";
			System.out.println("Please enter an artist name to search for their albums:");
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
        System.out.println("1. Title\n2. Genre\n3. Year\n4. Artist Name\n");
        int userChoice = DBUtils.getValidInput(1, 4, in);
		
        String userInput = "";
        String sql = "";
        String id = "";
        
		switch (userChoice) {
		case 1:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Title = $value;";
			System.out.println("Please enter an album title to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 2:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Genre = $value;";
			System.out.println("Please enter an album genre to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 3:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio JOIN Media ON MediaID = AudioID WHERE [Album/Audiobook] = 'a' AND Year = $value;";
			System.out.println("Please enter an album year to search for:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		case 4:
			sql = "SELECT Title, Genre, Year, AudioID FROM Audio AS A JOIN Media AS M ON M.MediaID = A.AudioID "
					+ "JOIN Contributes_To AS C ON C.MediaID = A.AudioID JOIN Contributor AS Con ON C.ContributorID = Con.ContributorID"
					+ " WHERE A.[Album/Audiobook] = 'a' AND Con.Name = $value;";
			System.out.println("Please enter an artist name to search for their albums:");
			userInput = in.nextLine();
			sql = sql.replace("$value", "'"+userInput+"'");
			break;
		default:
			break;
		}
		return DBUtils.searchAndSelect(conn, in, sql, 3, "AudioID");
	}

}
