

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ldb.dbitem.DBItemController;
import ldb.util.DBUtils;

// Utility class - handles modifying (editing and deleting) within the database
public class ModifyManager implements UserOption {

	private Map<Integer, DBItemController> dbItemMap;
	
	// Constructor
	public ModifyManager(List<DBItemController> dbItems) {
		dbItemMap = new HashMap<>();
		for (int i = 0; i < dbItems.size(); i++) {
			dbItemMap.put(i+1, dbItems.get(i));
		}
		dbItemMap.put(dbItems.size(), null);
	}
	
	private void executeDelete(Connection conn, Scanner in) {
		System.out.println("What kind of record would you like to delete?");
		System.out.println("1. Movie\n2. Album\n3. Track\n4. Audiobook\n");
        System.out.println("5. Actor\n6. Artist\n7. Author\n");
        System.out.println("8. Patron\n9. Review\n10. Checkout\n");
        System.out.println("11. Condition\n");
        System.out.println("12. Go back");
        int userChoice = DBUtils.getValidInput(1, 12, in);
        
        DBItemController dbItem = dbItemMap.get(userChoice);
		
		if (dbItem != null) {
			String[] ids = dbItem.retrieve(conn, in);
			dbItem.delete(conn, null, ids);
		} else {
			execute(conn, in);
		}
	}
	
	private void executeEdit(Connection conn, Scanner in) {
		System.out.println("What kind of record would you like to edit?");
		System.out.println("1. Movie\n2. Album\n3. Track\n4. Audiobook\n");
        System.out.println("5. Actor\n6. Artist\n7. Author\n");
        System.out.println("8. Patron\n9. Review\n10. Checkout\n");
        System.out.println("11. Condition\n");
        System.out.println("12. Go back");
        int userChoice = DBUtils.getValidInput(1, 12, in);
        
        DBItemController dbItem = dbItemMap.get(userChoice);
		
		if (dbItem != null) {
			String[] ids = dbItem.retrieve(conn, in);
			dbItem.edit(conn, in, ids);
		} else {
			execute(conn, in);
		}
	}
	
	// Text-based UI for inserting into the database
	@Override
	public void execute(Connection conn, Scanner in) {
		System.out.println("Would you like to edit or delete an item?");
		System.out.println("1. Edit\n2. Delete\n3. Back");
		int userChoice = DBUtils.getValidInput(1, 3, in);
		if (userChoice == 1) {
			executeEdit(conn, in);
		} else if (userChoice == 2) {
			executeDelete(conn, in);
		}
		
	}

	
}




