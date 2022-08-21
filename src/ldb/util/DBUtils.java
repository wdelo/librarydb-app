package ldb.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

// Utility class - holds some useful methods used throughout the rest of the project
// Methods are organized in alphabetical order
public class DBUtils 
{
	private static final int ENTRYSTRINGLENGTH = 30;
	
	// Class cannot be instantiated
	private DBUtils () {}
	
	public static void blank() {
		for (int i = 0; i < 100; i++)
			System.out.println();
	}
	
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
	
	// Returns a formatted date in the form year-month-day for insertion purposes
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
	
	// Deletes a record
	public static void deleteRecord(Connection conn, String sql) {
		try {			
			PreparedStatement p = conn.prepareStatement(sql);
			p.executeUpdate();
			p.close();
			System.out.println("Deletion successful.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	// edits a record
	public static void editRecord(Connection conn, String table, int numConstraints, String... tablesAndValues) {
		try {					
			String sql = "UPDATE $tableName SET ";
	        sql = sql.replace("$tableName", table);
	        for (int i = numConstraints*2; i < tablesAndValues.length; i+=2) {
	        	sql = sql + tablesAndValues[i] + "=" + tablesAndValues[i+1] + ", ";
	        }
	        sql = sql.substring(0, sql.lastIndexOf(','));
	        sql = sql + " WHERE " + tablesAndValues[0] + "=" + tablesAndValues[1] + ";";
	        
	        for (int i = 2; i < numConstraints*2; i++) {
	        	sql = sql + " AND " + tablesAndValues[i] + "=" + tablesAndValues[i+1] + ";";
	        }
	                 
	        PreparedStatement p = conn.prepareStatement(sql);
	        p.executeUpdate();  	
	       	p.close();
	       	System.out.println("Update successful.");
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

	public static void printString(String s, int length) {
		if (s == null) s = "NULL";
		boolean tooLong = s.length() > length;
		for (int i = 0; i < length; i++) {	
			if (tooLong && i >= length - 3)
				System.out.print(".");
			else if (i < s.length())
				System.out.print(s.charAt(i));
			else
				System.out.print(" ");
		}
	}
	
	public static void printRow(Connection conn, String sql, int inclusionBoundary) {
		try {
			PreparedStatement p = conn.prepareStatement(sql);
			ResultSet rs = p.executeQuery();	
			ResultSetMetaData rsmd = rs.getMetaData();
			rs.next();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (i <= inclusionBoundary) {
					printString(rsmd.getColumnName(i), ENTRYSTRINGLENGTH);				
					System.out.print(":\t");
					System.out.print(rs.getString(i)+"\n");
				}
			}
			
			rs.close();
			p.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int printRows(Connection conn, String sql, int inclusionBoundary) {
		int entryNum = 0;
		try {
			PreparedStatement p = conn.prepareStatement(sql);
			ResultSet rs = p.executeQuery();	
			ResultSetMetaData rsmd = rs.getMetaData();
			
			System.out.print("\t");
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (i <= inclusionBoundary) {
					printString(rsmd.getColumnName(i), ENTRYSTRINGLENGTH);				
					System.out.print("\t");	
				}
			}
			System.out.print("\n\n");
			
			while (rs.next()) {
				entryNum++;
				System.out.print(entryNum+".\t");
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (i <= inclusionBoundary) {
						printString(rs.getString(i), ENTRYSTRINGLENGTH);		
						System.out.print("\t");
					}				
				}
				System.out.println();
			}
			
			rs.close();
			p.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return entryNum;
	}	
	
	// First, Prints out rows based on the [sql] query, but only the attributes up to and including row number [inclusionBoundary]
	// Then, the user is able to select one of the rows that has been printed out
	// Finally, returns an array of the values of each [attribute[i]] in that row, in the order passed to the procedure 
	public static String[] searchAndSelect(Connection conn, Scanner s, String sql, int inclusionBoundary, String... attributes) {
		try {  	
			String values[] = new String[attributes.length];
			
			int numRows = printRows(conn, sql, inclusionBoundary);
			
			if (numRows > 0) {
				System.out.println("\nPick one of the entries (1-"+numRows+"):");
				int selection = getValidInput(1, numRows, s);
				
				PreparedStatement select = conn.prepareStatement(sql);
				ResultSet rsSelect = select.executeQuery();
				ResultSetMetaData rsmd = rsSelect.getMetaData();
				
				for (int i = 0; i < selection; i++)
					rsSelect.next();
				
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					for (int j = 0; j < attributes.length; j++) {
						if (rsmd.getColumnName(i).equals(attributes[j])) {
							values[j] = rsSelect.getString(i);
							break;
						}
					}
				}
				
				rsSelect.close();
				select.close();
				
				return values;
			} else {
				System.out.println("Oops! Looks like there aren't any rows for those specifications.");
			}
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
	public static boolean valueExists(Connection conn, String table, String... attributesAndValues) {
		boolean exists = false;
		try {			
			String sql = "SELECT COUNT(*) FROM $tableName WHERE $attributeName = $value;";
	        sql = sql.replace("$tableName", table);
	        sql = sql.replace("$attributeName", attributesAndValues[0]);   
	        sql = sql.replace("$value", attributesAndValues[1]);
	        
	        for (int i = 2; i < attributesAndValues.length; i++) {
	        	sql.replace(";", " AND "+attributesAndValues[i]+" = "+attributesAndValues+";");
	        }
	        
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







