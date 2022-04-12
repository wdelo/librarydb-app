package ldb.dbitem;

import java.sql.Connection;
import java.util.Scanner;

public interface DBItemController {
	
	/* NOTE FOR DEVELOPMENT:
	 * All the queries for each method should be up here for each implementing class
	 * (including the queries for every possible combination of how we should search for a DBItem 
	 * if there is more than one way)
	 * The only things we can programmatically change are the parameter values; we cannot change the tables 
	 * or dynamically add onto a query 
	 */
	
	/*
	 * ANOTHER NOTE: 
	 * Some DBItem controllers will require references to other controllers (e.g. when an MovieController wants to insert an Actor)
	 * So we will either need to pass references into either constructors and save the reference that way or into methods
	 */
	
	/*
	 * LAST NOTE: 
	 * We may want to create DBItemManager superclasses (e.g. Contributor, Media, Audio)
	 */
	
	public String[] insert(Connection conn, Scanner in);
	
	public void edit(Connection conn, Scanner in, String[] ids);
	
	public void delete(Connection conn, Scanner in, String[] ids);
	
	public void search(Connection conn, Scanner in);	
	
	public String[] retrieve(Connection conn, Scanner in);
	
}
