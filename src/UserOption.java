import java.sql.Connection;
import java.util.Scanner;

public interface UserOption {

	public void execute(Connection conn, Scanner in);
	
}
