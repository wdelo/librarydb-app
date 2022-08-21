package ldb;

import java.sql.Connection;
import java.util.Scanner;

@FunctionalInterface
public interface UserOption {

	public void execute(Connection conn, Scanner in);
	
}
