package osu.cse3241;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

// Utility class - handles modifying (editing and deleting) within the database
public class ModifyManager {

	// Class cannot be instantiated
	private ModifyManager() {}
	
	private static void modifyActor(Connection conn, Scanner s) {
		String id = SearchManager.retrieveActor(conn, s);
		System.out.println("Would you like to edit or delete this actor?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editActor(conn, s, id);
		} else if (userChoice == 2) {
			deleteActor(conn, s, id);
		}
	}
	
	private static void editActor(Connection conn, Scanner s, String id) {
		System.out.println("Please enter the name of the actor:");
		String name = s.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(s);
		
		editRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Actor'");
	}
	
	private static void deleteActor(Connection conn, Scanner s, String id) {
		deleteContributor(conn, id);
	}
	

	private static void modifyAlbum(Connection conn, Scanner s) {
		String id = SearchManager.retrieveAlbum(conn, s);
		System.out.println("Would you like to edit or delete this album?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editAlbum(conn, s, id);
		} else if (userChoice == 2) {
			deleteAlbum(conn, s, id);
		}
		
	}
	
	private static void editAlbum(Connection conn, Scanner s, String id) {
		System.out.println("Please enter the title of the album:");
		String title = s.nextLine();
		System.out.println("Please enter the genre of the album:");
		String genre = s.nextLine();
		System.out.println("Please enter the year of the album:");
		String year = s.nextLine();
		
		editRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
	}
	
	private static void deleteAlbum(Connection conn, Scanner s, String id) {
		deleteMedia(conn, id);
	}
	
	private static void modifyArtist(Connection conn, Scanner s) {
		String id = SearchManager.retrieveArtist(conn, s);
		System.out.println("Would you like to edit or delete this artist?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editArtist(conn, s, id);
		} else if (userChoice == 2) {
			deleteArtist(conn, s, id);
		}
		
	}
	
	private static void editArtist(Connection conn, Scanner s, String id) {
		System.out.println("Please enter the name of the artist:");
		String name = s.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(s);
		
		editRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Artist'");
	}
	
	private static void deleteArtist(Connection conn, Scanner s, String id) {
		deleteContributor(conn, id);
	}
	
	private static void modifyAuthor(Connection conn, Scanner s) {
		String id = SearchManager.retrieveAuthor(conn, s);
		System.out.println("Would you like to edit or delete this author?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editAuthor(conn, s, id);
		} else if (userChoice == 2) {
			deleteAuthor(conn, s, id);
		}
		
	}
	
	private static void editAuthor(Connection conn, Scanner s, String id) {
		System.out.println("Please enter the name of the author:");
		String name = s.nextLine();
		System.out.println("Let's get a date of birth.");
		String dob = DBUtils.getFormattedDate(s);
		
		editRecord(conn, "Contributor", "'"+id+"'", "'"+name+"'", dob, "'Author'");
	}
	
	private static void deleteAuthor(Connection conn, Scanner s, String id) {
		deleteContributor(conn, id);
	}
	
	private static void modifyBook(Connection conn, Scanner s) {
		String id = SearchManager.retrieveBook(conn, s);
		System.out.println("Would you like to edit or delete this audiobook?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editBook(conn, s, id);
		} else if (userChoice == 2) {
			deleteBook(conn, s, id);
		}
		
	}
	
	private static void editBook(Connection conn, Scanner s, String id) {
		System.out.println("Please enter the title of the book:");
		String title = s.nextLine();
		System.out.println("Please enter the genre of the book:");
		String genre = s.nextLine();
		System.out.println("Please enter the year of the book:");
		String year = s.nextLine();
		
		editRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
	}
	
	private static void deleteBook(Connection conn, Scanner s, String id) {
		deleteMedia(conn, id);
	}
	
	private static void modifyMovie(Connection conn, Scanner s) {
		String id = SearchManager.retrieveMovie(conn, s);
		System.out.println("Would you like to edit or delete this movie?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editMovie(conn, s, id);
		} else if (userChoice == 2) {
			deleteMovie(conn, s, id);
		}
		
	}
	
	private static void editMovie(Connection conn, Scanner s, String id) {
		System.out.println("Please enter the title of the movie:");
		String title = s.nextLine();
		System.out.println("Please enter the genre of the movie:");
		String genre = s.nextLine();
		System.out.println("Please enter the year of the movie:");
		String year = s.nextLine();
		System.out.println("Please enter the content rating of the movie (PG-13, R, etc.):");
		String cr = s.nextLine();
		System.out.println("Please enter the length of the movie (whole number of minutes):");
		int minutes = DBUtils.getValidInput(0, 999, s);
		
		editRecord(conn, "Media", id, "'"+title+"'", "'"+genre+"'", "'"+year+"'");
		editRecord(conn, "Movie", id, "'"+cr+"'", ""+minutes);
		
	}
	
	private static void deleteMovie(Connection conn, Scanner s, String id) {
		deleteMedia(conn, id);
	}
	
	private static void modifyPatron(Connection conn, Scanner s) {
		String id = SearchManager.retrievePatron(conn, s);
		System.out.println("Would you like to edit or delete this patron?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editPatron(conn, s, id);
		} else if (userChoice == 2) {
			deletePatron(conn, s, id);
		}
		
	}
	
	private static void editPatron(Connection conn, Scanner s, String id) {
		System.out.println("Please enter a city name:");
		String city = s.nextLine();
		System.out.println("Please enter a state:");
		String state = s.nextLine();
		System.out.println("Please enter a street address:");
		String addr = s.nextLine();
		System.out.println("Please enter a first name:");
		String fname = s.nextLine();
		System.out.println("Please enter a last name:");
		String lname = s.nextLine();
		
		editRecord(conn, "Patron", "'"+id+"'", "'"+city+"'", "'"+state+"'", "'"+addr+"'", "'"+fname+"'", "'"+lname+"'");
	}
	
	private static void deletePatron(Connection conn, Scanner s, String id) {
		deleteRecord(conn, "DELETE FROM Patron WHERE Email_Address="+id);
		deleteRecord(conn, "DELETE FROM Checkout WHERE Email_Address="+id);
		deleteRecord(conn, "DELETE FROM Review WHERE Email_Address="+id);
	}
	
	private static void modifyTrack(Connection conn, Scanner s) {
		String[] ids = SearchManager.retrieveTrack(conn, s);
		System.out.println("Would you like to edit or delete this track?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editTrack(conn, s, ids);
		} else if (userChoice == 2) {
			deleteTrack(conn, ids);
		}
		
	}
	
	private static void editTrack(Connection conn, Scanner s, String[] ids) {
		System.out.println("Please enter the title of the track:");
		String title = s.nextLine();
		System.out.println("Please enter the length of the track (in seconds):");
		int length = DBUtils.getValidInput(0, 9999, s);
		
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
	
	private static void deleteTrack(Connection conn, String ids[]) {
		deleteRecord(conn, "DELETE FROM Track WHERE AudioID="+ids[0]+" AND Track_Number="+ids[1]);
	}
	
	private static void modifyReview(Connection conn, Scanner s) {
		String[] ids = SearchManager.retrieveReview(conn, s);
		System.out.println("Would you like to edit or delete this review?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editReview(conn, s, ids);
		} else if (userChoice == 2) {
			deleteReview(conn, ids);
		}			
	}
	
	private static void editReview(Connection conn, Scanner s, String[] ids) {
		// TODO Auto-generated method stub
		
	}

	private static void deleteReview(Connection conn, String[] ids) {
		deleteRecord(conn, "DELETE FROM Review WHERE Patron_Email="+ids[0]+" AND MediaID="+ids[1]);
	}
	
	private static void modifyCheckout(Connection conn, Scanner s) {
		String[] ids = SearchManager.retrieveCheckout(conn, s);
		System.out.println("Would you like to edit or delete this checkout?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editCheckout(conn, s, ids);
		} else if (userChoice == 2) {
			deleteCheckout(conn, ids);
		}
	
	}
	
	private static void editCheckout(Connection conn, Scanner s, String[] ids) {
		// TODO Auto-generated method stub
		
	}
	
	private static void deleteCheckout(Connection conn, String[] ids) {
		deleteRecord(conn, "DELETE FROM Checkout WHERE Call_Number="+ids[0]+" AND Email_Address="+ids[1]+" AND Checkout_date="+ids[2]);
	}
	
	private static void modifyCondition(Connection conn, Scanner s) {
		String[] ids = SearchManager.retrieveCondition(conn, s);
		System.out.println("Would you like to edit or delete this condition?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, s);
		if (userChoice == 1) {
			editCondition(conn, s, ids);
		} else if (userChoice == 2) {
			deleteCondition(conn, ids);
		}
		
	}
	
	private static void editCondition(Connection conn, Scanner s, String[] ids) {
		// TODO Auto-generated method stub
		
	}	
	
	private static void deleteCondition(Connection conn, String[] ids) {
		deleteRecord(conn, "DELETE FROM Condition WHERE Date="+ids[0]+" AND Call_Number="+ids[1]);
	}
	
	private static void deleteMedia(Connection conn, String id) {
		deleteRecord(conn, "DELETE FROM Media WHERE MediaID="+id);
		deleteRecord(conn, "DELETE FROM Audio WHERE AudioID="+id);
		deleteRecord(conn, "DELETE FROM Contributes_To WHERE MediaID="+id);
		deleteRecord(conn, "DELETE FROM Media_Instance WHERE MediaID="+id);
		deleteRecord(conn, "DELETE FROM Checkout WHERE MediaID="+id);
	}
	
	private static void deleteContributor(Connection conn, String id) {
		deleteRecord(conn, "DELETE FROM Contributor WHERE ContributorID="+id);
		deleteRecord(conn, "DELETE FROM Contributes_To WHERE ContributorID="+id);
	}

	public static void editRecord(Connection conn, String table, String... values) {
		try {			
			String[] attributes = DBUtils.getAttributes(conn, table);
			
			
			String sql = "UPDATE $tableName SET ";
	        sql = sql.replace("$tableName", table);
	        for (int i = 0; i < values.length; i++) {
	        	sql = sql + attributes[i] + "=" + values[i] + ", ";
	        }   
	        sql = sql.substring(0, sql.lastIndexOf(',')); 
	        sql = sql + " WHERE " + attributes[0] + "=" + values[0] + ";";
	        
	        PreparedStatement p = conn.prepareStatement(sql);
	        p.executeUpdate();  	
	       	p.close();
	       	System.out.println("Update successful.");
	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	private static void deleteRecord(Connection conn, String sql) {
		try {			
			PreparedStatement p = conn.prepareStatement(sql);
			p.executeUpdate();
			p.close();
			System.out.println("Deletion successful.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	// Text-based UI for inserting into the database
		public static void userModify(Connection conn, Scanner s) {
			System.out.println("What kind of record would you like to modify?");
			System.out.println("1. Movie\n2. Album\n3. Track\n4. Audiobook\n");
	        System.out.println("5. Actor\n6. Artist\n7. Author\n");
	        System.out.println("8. Patron\n9. Review\n10. Checkout\n");
	        System.out.println("11. Condition\n");
	        System.out.println("12. Go back");
	        int userChoice = DBUtils.getValidInput(1, 12, s);
			
			switch (userChoice) {
			case 1:
				modifyMovie(conn, s);
				break;
			case 2:
				modifyAlbum(conn, s);
				break;
			case 3:
				modifyTrack(conn, s);
				break;
			case 4:
				modifyBook(conn, s);
				break;
			case 5:
				modifyActor(conn, s);
				break;
			case 6:
			    modifyArtist(conn, s);
				break;
			case 7:
				modifyAuthor(conn, s);
				break;
			case 8:
				modifyPatron(conn, s);
				break;
			case 9:
				modifyReview(conn, s);
				break;
			case 10:
				modifyCheckout(conn, s);
				break;
			case 11:
				modifyCondition(conn, s);
				break;
			case 12:
				
			default:
				break;
			}
		}

	
}




