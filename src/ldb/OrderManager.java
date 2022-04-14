package ldb;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ldb.dbitem.OrderController;
import ldb.util.DBUtils;

//Utility class - handles orders within the database
public class OrderManager implements UserOption {

	// Constructor
	public OrderManager() {}

	//Gives user option to choose to create or receive an order
	@Override
	public void execute(Connection conn, Scanner s) {
    	System.out.print("Would you like to ORDER or RECEIVE items?");
    	char choice = Character.toLowerCase(s.nextLine().charAt(0));
    	if (choice == 'o') {
        	insertOrder(conn, s);
    	} else {
        	markItemArrived(conn, s);
    	}
	}

	// Performs an insertion for a new order, adding each item to a media instance and assigning a condition
	private void insertOrder(Connection conn, Scanner s) {
    	String orderID = DBUtils.getUniqueID(conn, "[Order]", "Order_Id", 9);
    	boolean arrivalStatus = false;
    	System.out.println("Please enter the price of the order:");
    	String price = s.nextLine();
    	System.out.println("Please enter the quantity of the order:");
    	String numberOrdered = s.nextLine();
    	System.out.println("Please enter the estimated arrival:");
    	String eta = DBUtils.getFormattedDate(s);
    	System.out.println(
            	"Please enter the location of the items when they arrive (1 for"
                    	+ " first floor, 2 for second floor, 3 for third floor):");
    	String orderLocation = s.nextLine();
    	System.out.println("Is the order for digital or physical media?");
    	char digitalPhysical = Character.toLowerCase(s.nextLine().charAt(0));
    	System.out.println("Is the order for an existing media item? Y/N");
    	char choice = Character.toLowerCase(s.nextLine().charAt(0));
    	if (choice == 'n') {
        	execute(conn, s);
    	}
    	System.out.println("What is the media title?");
    	String title = s.nextLine();
    	System.out.println("Please select the corresponding media.");
    	String MediaID = DBUtils.searchAndSelect(conn, s,
            	"SELECT * FROM Media WHERE Title = " + "'"+title+"'", 99, "MediaID")[0];
    	if (MediaID != null) {
    	DBUtils.insertRecord(conn, "[Order]", "'" + orderID + "'",
            	"'" + arrivalStatus + "'", "'" + price + "'",
            	"'" + numberOrdered + "'", eta ,
            	"'" + orderLocation + "'", "'" + digitalPhysical + "'",
            	"'" + MediaID + "'");
    	}
	}

	//Marks an arrived order as received and calls insertNewMediaInstances
	private static void markItemArrived(Connection conn, Scanner s) {
    	System.out.println("Please enter orderID:");
    	String orderID = s.nextLine();
    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	List<String> orderInfo = new ArrayList<>();
    	try {
        	String sql = "UPDATE [Order] SET Arrival_Status = ? WHERE Order_Id = ?;";
        	stmt = conn.prepareStatement(sql);
        	stmt.setString(1, "TRUE");
        	stmt.setString(2, orderID);
        	stmt.executeQuery();
        	sql = "SELECT Digital_Physical, Order_Loc, MediaID, Number_Ordered FROM [Order] WHERE Order_Id = ?;";
        	stmt.setString(1, orderID);
        	rs = stmt.executeQuery();
        	orderInfo.add(rs.getString(0));
        	orderInfo.add(rs.getString(1));
        	orderInfo.add(rs.getString(2));
        	orderInfo.add(rs.getString(3));
        	System.out.print("Please enter the current date for our records.");
        	String date = DBUtils.getFormattedDate(s);
        	for (int i = 0; i < Integer.parseInt(orderInfo.get(3)); i++) {
            	String callNumber = DBUtils.getUniqueID(conn, "Media_Instance",
                    	"Call_Number", 13);
            	DBUtils.insertRecord(conn, "Media_Instance",
                    	"'" + callNumber + "'", "'TRUE'",
                    	"'" + orderInfo.get(0) + "'",
                    	"'" + orderInfo.get(1) + "'",
                    	"'" + orderInfo.get(2) + "'");
            	DBUtils.insertRecord(conn, "Condition", "'" + date + "'",
                    	"'" + callNumber + "'", "'New'", "'FALSE'",
                    	"'Item just received.'");
        	}
    	} catch (SQLException e) {
        	System.out.println(e.getMessage());
    	} finally {
        	if (rs != null) {
            	try {
                	rs.close();
            	} catch (SQLException e) {
                	System.out.println(e.getMessage());
            	}
        	}
        	if (stmt != null) {
            	try {
                	stmt.close();
            	} catch (SQLException e) {
                	System.out.println(e.getMessage());
            	}
        	}
    	}

	}

}

