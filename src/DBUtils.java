

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

// Utility class - holds some useful methods used throughout the rest of the project
// Methods are organized in alphabetical order
public class DBUtils 
{
	// Class cannot be instantiated
	private DBUtils () {}
	
	// Returns an array containing all the attributes (in order) of a particular table
	// Note that a null value is returned if a table somehow has no attributes (columns)
	public static String[] getAttributes(Connection conn, String table) {
		try {			
			String sql = "SELECT * FROM $tableName;";
        	sql = sql.replace("$tableName", table);
        
        	PreparedStatement p = conn.prepareStatement(sql);      
        	ResultSet rs = p.executeQuery();
        	ResultSetMetaData rsmd = rs.getMetaData();
        	
        	int numAttributes = rsmd.getColumnCount();
        	String attributes[] = new String[numAttributes];       	
        	for (int i = 1; i < numAttributes+1; i++) {
        		attributes[i-1] = rsmd.getColumnName(i);
        	}
		
        	rs.close();      	
        	p.close();
        	
        	return attributes;
        } catch (SQLException e) {
            System.out.println("Error in getAttributes");
        }
		return null;
	}
	
	// Returns an array containing the SQL data types of each respective attribute
	// Note that the JDBC assigns a unique integer value to each SQL data type; this is what's returned
	// Useful in conjunction with the getAttributes method; array entries from both of these methods correspond to each other
	// Note that a null value is returned if a table somehow has no attributes (columns)
	public static int[] getAttributeTypes(Connection conn, String table) {
		try {			
			String sql = "SELECT * FROM $tableName;";
        	sql = sql.replace("$tableName", table);
        
        	PreparedStatement p = conn.prepareStatement(sql);      
        	ResultSet rs = p.executeQuery();
        	ResultSetMetaData rsmd = rs.getMetaData();
        	
        	int numAttributes = rsmd.getColumnCount();
        	int attributeTypes[] = new int[numAttributes];       	
        	for (int i = 1; i < numAttributes+1; i++) {
        		attributeTypes[i-1] = rsmd.getColumnType(i);
        	}
		
        	rs.close();      	
        	p.close();
        	
        	return attributeTypes;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return null;
	}
	
	// Returns a formatted date in the form year-month-day (mainly for use in insertion)
	public static String getFormattedDate(Scanner s) {
		System.out.println("Please enter the year, or 0 if unknown:");
		int year = getValidInput(0, 9999, s);
		System.out.println("Please enter the month (1-12), or 0 if unknown:");
		int month = getValidInput(0, 12, s);
		System.out.println("Please enter the day (1-31), or 0 if unknown:");
		int day = 0;
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = getValidInput(0, 30, s);
		} else if (month == 2) {
			day = getValidInput(0, 29, s);
		} else {
			day = getValidInput(0, 31, s);
		}
		String dob = "";
		if (year != 0) {
			dob += year;
		}
		if (month != 0) {
			if (!dob.isEmpty())
				dob += '-';
			if (month < 10)
				dob += '0';
			dob += month;
		}
		if (day != 0) {
			if (!dob.isEmpty())
				dob += '-';
			if (day < 10)
				dob += '0';
			dob += day;
		}
		if (dob.isEmpty())
			return "NULL";
		else
			return "'"+dob+"'";
	}
	
	// Returns the number of tracks/chapters an album/audiobook has
	public static int getTrackCount(Connection conn, String audioId) {
		int count = 0;
		try {			
			String sql = "SELECT COUNT(*) FROM Audio AS A JOIN Track AS T ON A.AudioID = T.AudioID WHERE A.AudioID = $value"; 
	        sql = sql.replace("$value", audioId);
	        
	        PreparedStatement p = conn.prepareStatement(sql);       	
	        ResultSet rs = p.executeQuery();
	        count = rs.getInt(1);
	        	
			rs.close();
	        p.close();
	    } catch (SQLException e) {
	    	System.out.println(e.getMessage());
	    }
		return count;
	}
	
	// Returns a unique ID meant to be used as a MediaID or CallNumber
	// Format is '123456789'
	public static String getUniqueID(Connection conn, String table, String attributeName, int numChars) {
		String s = "";
		Random r = new Random();
		do {
			for (int i = 0; i < numChars; i++) {
				s += r.nextInt(9);
			}
		} while (valueExists(conn, table, attributeName, s));
		return s;
	}
	
	// Returns an integer between minOption and maxOption representing a user choice
	public static int getValidInput(int minOption, int maxOption, Scanner in) {
		String userInput = in.nextLine();
		boolean valid = false;
		while (!valid) {
			if (isNumeric(userInput) && Integer.parseInt(userInput) >= minOption && Integer.parseInt(userInput) <= maxOption) {
				valid = true;
			} else {
				System.out.println("Invalid input: enter a number between " + minOption +" and " + maxOption + ". Try again:");
				userInput = in.nextLine();
			}
		}
		return Integer.parseInt(userInput);
	}
	
	// Inserts a new record into a given table
	// Note that values must be exactly as you'd write them in SQL code
	//
	// Example call: insertRecord(conn, "Movie", "'100000000'", "'PG-13'", "126");
	public static void insertRecord(Connection conn, String table, String... values) {
		try {			
			String sql = "INSERT INTO $tableName VALUES (";
	        sql = sql.replace("$tableName", table);
	        for (int i = 0; i < values.length; i++) {
	        	sql = sql + values[i] + ", ";
	        }   
	        sql = sql.substring(0, sql.lastIndexOf(','));
	        sql = sql + ");";   
	        
	        PreparedStatement p = conn.prepareStatement(sql);
	        p.executeUpdate();  	
	       	p.close();
	       	System.out.println("Insertion successful.");
	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	}
	
	// Returns whether a given input string is a number (can be parsed into an integer)
	public static boolean isNumeric(String s) {
	    if (s == null) {
	        return false;
	    }
	    try {
	        Integer.parseInt(s);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	// Retrieves and prints out rows based on a given SQL statement
	public static void retrieveRows(Connection conn, String sql) {
		try {
			PreparedStatement p = conn.prepareStatement(sql);
			ResultSet rs = p.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
    	
			int numAttributes = rsmd.getColumnCount();
			for (int i = 1; i <= numAttributes; i++) {
				System.out.printf("%30s", rsmd.getColumnName(i));
			}
    	
			System.out.print("\n");
			while (rs.next()) {
				for (int i = 1; i <= numAttributes; i++) {
					String attributeValue = rs.getString(i);
					System.out.printf("%30s", attributeValue);
				}
				System.out.println();
			}
    	
			rs.close();
			p.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// Does a normal search, then prompts the user to choose one of the results
	// The value of attributeName of the user-chosen row is then returned
	// maxColumn is the max column number that will be printed when displaying rows. Useful if you want to hide the output of a certain
	// attribute but still be able to select it (for example, MediaID).
	public static String searchAndSelect(Connection conn, Scanner s, String sql, String attributeName, int maxColumn) {
		try {
			PreparedStatement p = conn.prepareStatement(sql);
			ResultSet rs = p.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
    	
			int colNum = 0;
			
			System.out.println("    ");
			int numAttributes = rsmd.getColumnCount();
			if (maxColumn < 0 && maxColumn > numAttributes) {
				maxColumn = numAttributes;
			}
			for (int i = 1; i <= numAttributes; i++) {
				String colName = rsmd.getColumnName(i);
				if (i <= maxColumn ) {
					System.out.printf("%30s", colName);
				}
				if (colName.equals(attributeName)) {
					colNum = i;
				}
			}
			
			ArrayList<String> values = new ArrayList<String>();
    	
			int counter = 0;
			System.out.print("\n");
			while (rs.next()) {
				System.out.print(counter+1);
				for (int i = 1; i <= numAttributes; i++) {
					String attributeValue = rs.getString(i);
					if (i <= maxColumn) {
						System.out.printf("%30s", attributeValue);
					}				
					if (i == colNum) {
						values.add(attributeValue);
					}
				}
				System.out.println();
				counter++;
			}
			
			int selection = 0;
			if (counter > 0) {
				System.out.println("Pick one of the entries (1-"+counter+"):");
				selection = getValidInput(1, counter, s);
				return values.get(selection-1);
			} else {
				System.out.println("Oops! Looks like there aren't any rows for those specifications.");
			}
    	
			rs.close();
			p.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	// The glory of searchAndSelect but with 2 attributes!!!!!!!!!!!!!
	public static String[] searchAndSelect2(Connection conn, Scanner s, String sql, String attributeName1, String attributeName2, int maxColumn) {
		try {
			PreparedStatement p = conn.prepareStatement(sql);
			ResultSet rs = p.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
    	
			int colNum1 = 0;
			int colNum2 = 0;
			
			System.out.println("    ");
			int numAttributes = rsmd.getColumnCount();
			if (maxColumn < 0 && maxColumn > numAttributes) {
				maxColumn = numAttributes;
			}
			for (int i = 1; i <= numAttributes; i++) {
				String colName = rsmd.getColumnName(i);
				if (i <= maxColumn ) {
					System.out.printf("%30s", colName);
				}
				if (colName.equals(attributeName1)) {
					colNum1 = i;
				}
				if (colName.equals(attributeName2)) {
					colNum2 = i;
				}
			}
			
			ArrayList<String> values1 = new ArrayList<String>();
			ArrayList<String> values2 = new ArrayList<String>();
    	
			int counter = 0;
			System.out.print("\n");
			while (rs.next()) {
				System.out.print(counter+1);
				for (int i = 1; i <= numAttributes; i++) {
					String attributeValue = rs.getString(i);
					if (i <= maxColumn) {
						System.out.printf("%30s", attributeValue);
					}				
					if (i == colNum1) {
						values1.add(attributeValue);
					}
					if (i == colNum2) {
						values2.add(attributeValue);
					}
				}
				System.out.println();
				counter++;
			}
			
			int selection = 0;
			if (counter > 0) {
				System.out.println("Pick one of the entries (1-"+counter+"):");
				selection = getValidInput(1, counter, s);
				String attributes[] = {values1.get(selection-1), values2.get(selection-1)};
				return attributes;
			} else {
				System.out.println("Oops! Looks like there aren't any rows for those specifications.");
			}
    	
			rs.close();
			p.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	// I'm lazy and don't wanna generalize this right now so searchAndSelect with 3 attributes!!!!!!!!!!!
	public static String[] searchAndSelect3(Connection conn, Scanner s, String sql, String attributeName1, String attributeName2,
			String attributeName3,int maxColumn) {
		try {
			PreparedStatement p = conn.prepareStatement(sql);
			ResultSet rs = p.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
    	
			int colNum1 = 0;
			int colNum2 = 0;
			int colNum3 = 0;
			
			System.out.println("    ");
			int numAttributes = rsmd.getColumnCount();
			if (maxColumn < 0 && maxColumn > numAttributes) {
				maxColumn = numAttributes;
			}
			for (int i = 1; i <= numAttributes; i++) {
				String colName = rsmd.getColumnName(i);
				if (i <= maxColumn ) {
					System.out.printf("%30s", colName);
				}
				if (colName.equals(attributeName1)) {
					colNum1 = i;
				}
				if (colName.equals(attributeName2)) {
					colNum2 = i;
				}
				if (colName.equals(attributeName3)) {
					colNum3 = i;
				}
			}
			
			ArrayList<String> values1 = new ArrayList<String>();
			ArrayList<String> values2 = new ArrayList<String>();
			ArrayList<String> values3 = new ArrayList<String>();
    	
			int counter = 0;
			System.out.print("\n");
			while (rs.next()) {
				System.out.print(counter+1);
				for (int i = 1; i <= numAttributes; i++) {
					String attributeValue = rs.getString(i);
					if (i <= maxColumn) {
						System.out.printf("%30s", attributeValue);
					}				
					if (i == colNum1) {
						values1.add(attributeValue);
					}
					if (i == colNum2) {
						values2.add(attributeValue);
					}
					if (i == colNum3) {
						values3.add(attributeValue);
					}
				}
				System.out.println();
				counter++;
			}
			
			int selection = 0;
			if (counter > 0) {
				System.out.println("Pick one of the entries (1-"+counter+"):");
				selection = getValidInput(1, counter, s);
				String attributes[] = {values1.get(selection-1), values2.get(selection-1), values3.get(selection-1)};
				return attributes;
			} else {
				System.out.println("Oops! Looks like there aren't any rows for those specifications.");
			}
    	
			rs.close();
			p.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	// Returns whether a given value in a table exists for that attribute (i.e. there exists at least one
	// row which has that value for that attribute)
	// Note that inputs must be exactly how you would write them in SQL
	// Useful for maintaining uniqueness of values (MovieID, for example)
	//
	// Example call: valueIsDuplicate(conn, "[Order]", "Number_Ordered", "9");
	// Example call: valueIsDuplicate(conn, "Movie", "MovieID", "'123456789'");
	public static boolean valueExists(Connection conn, String table, String attributeName, String value) {
		boolean exists = false;
		try {			
			String sql = "SELECT COUNT(*) FROM $tableName WHERE $attributeName = $value";
	        sql = sql.replace("$tableName", table);
	        sql = sql.replace("$attributeName", attributeName);   
	        sql = sql.replace("$value", value);
	        
	        PreparedStatement p = conn.prepareStatement(sql);       	
	        ResultSet rs = p.executeQuery();
	        exists = rs.getInt(1) > 1;
	        	
			rs.close();
	        p.close();
	    } catch (SQLException e) {
	    	System.out.println(e.getMessage());
	    }
		return exists;
	}
}







