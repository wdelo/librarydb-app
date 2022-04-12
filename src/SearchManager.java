

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ldb.dbitem.DBItemController;
import ldb.util.DBUtils;

//Utility class - handles searching within the database
//Methods are organized in alphabetical order
public class SearchManager implements UserOption {
	
	private Map<Integer, DBItemController> dbItemMap;
	
	// Constructor
	public SearchManager(List<DBItemController> dbItems) {
		dbItemMap = new HashMap<>();
		for (int i = 0; i < dbItems.size(); i++) {
			dbItemMap.put(i+1, dbItems.get(i));
		}
		dbItemMap.put(dbItems.size(), null);
	}
	
	// Text-based UI for searching within the database
	@Override
	public void execute(Connection conn, Scanner in) {
		System.out.println("What kind of record would you like to search for?");
        System.out.println("1. Movie\n2. Album\n3. Track\n4. Audiobook\n");
        System.out.println("5. Actor\n6. Artist\n7. Author\n");
        System.out.println("8. Patron\n9. Review\n10. Checkout\n");
        System.out.println("11. Condition\n");
        System.out.println("12. Go back");
        int userChoice = DBUtils.getValidInput(1, 12, in);
		
        DBItemController dbItem = dbItemMap.get(userChoice);
		
		if (dbItem != null) {
			dbItem.search(conn, in);
		} else {
			System.out.println("Returning...");
		}

	}
}
	





