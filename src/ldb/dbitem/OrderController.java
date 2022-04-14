package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

import ldb.UserOption;
import ldb.util.DBUtils;
import ldb.util.MenuScreen;

public class OrderController {

	private static String movieMenuPrompt = "What would you like to manage with orders?";	
	private static String[] movieMenuScreenOptions = {
    		"Add an order", 
    		"View orders",
    		"Back",
	};
	
	private static String selectedMenuPrompt = "What would you like to do with this movie?";
	private static String[] selectedMenuScreenOptions = {
			"Edit this order",
			"Mark this order as arrived",
			"Go back",
	};
	
	private static MenuScreen menuScreen = new MenuScreen(movieMenuPrompt, movieMenuScreenOptions);
	private static MenuScreen selectedMenuScreen = new MenuScreen(selectedMenuPrompt, selectedMenuScreenOptions);
	
	public static String[] insert(Connection conn, Scanner in) {
		String[] mediaID;
		System.out.println("Are you ordering a:\n1. Movie\n2. Album\n3. Audiobook");
		int userChoice = DBUtils.getValidInput(1, 3, in);
		System.out.println("Is this media already in the database?\n1. Yes\n2. No");
		int userChoice2 = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			if (userChoice2 == 1)
				mediaID = MovieController.retrieve(conn, in);
			else
				mediaID = MovieController.insert(conn, in);
		} else if (userChoice == 2) {
			if (userChoice2 == 1)
				mediaID = AlbumController.retrieve(conn, in);
			else
				mediaID = AlbumController.insert(conn, in);
		} else {
			if (userChoice2 == 1)
				mediaID = BookController.retrieve(conn, in);
			else
				mediaID = BookController.insert(conn, in);
		}
		System.out.println("How many copies are you ordering? You may order up to 10 in one order.");
		int num = DBUtils.getValidInput(1, 10, in);
		System.out.println("How much does this order cost in total?");
		double price = Double.parseDouble(in.nextLine());
		System.out.println("What is the ETA of this order?");
		String date = DBUtils.getFormattedDate(in);
		String dp = "d";
		System.out.println("Is this order for:\n1. Physical copies\n2. Digital copies");
		userChoice = DBUtils.getValidInput(1, 2, in);
		if (userChoice == 1) {
			dp = "p";
		}
		String orderID = DBUtils.getUniqueID(conn, "[Order]", "OrderId", 9);
		DBUtils.insertRecord(conn, "[Order]", "'"+orderID+"'", ""+price, ""+num, date, "'"+dp+"'", "'"+mediaID[0]+"'");
		
		return null;
	}

	public static void edit(Connection conn, Scanner in, String[] ids) {
		System.out.println("Let's get a new ETA for this order.");
		String date = DBUtils.getFormattedDate(in);
		
		DBUtils.editRecord(conn, "[Order]", 1, "OrderId", ids[0], "ETA", date);
	}

	public static void delete(Connection conn, Scanner in, String[] ids) {	
		String location = "Online";
		if (ids[3].equals("p")) {
			System.out.println("Where will these copies be stored in the library?");
			location = in.nextLine();
		}
		for (int i = 0; i < Integer.parseInt(ids[2]); i++) {
			String callNumber = DBUtils.getUniqueID(conn, "MediaInstance", "CallNumber", 13);
			DBUtils.insertRecord(conn, "MediaInstance", "'"+callNumber+"'", "'"+ids[3]+"'", ""+1, "'"+location+"'", "'"+ids[1]+"'");
		}
		String sql = "DELETE FROM [Order] WHERE OrderId = '"+ids[0]+"';";
		DBUtils.deleteRecord(conn, sql);
	}

	public static String[] retrieve(Connection conn, Scanner in) {
			
		String sql = "SELECT Title, [Digital/Physical], ETA, NumCopies, Price, OrderID, M.MediaID FROM [Order] AS O JOIN Media AS M ON O.MediaID = M.MediaID;";
		     
        DBUtils.blank();
	
		return DBUtils.searchAndSelect(conn, in, sql, 5, "OrderId", "MediaID", "NumCopies", "Digital/Physical");
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
			default:
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
				edit(conn, in, ids);
				break;
			case 2:
				delete(conn, in, ids);
				break;
			}
		}
	}


}
