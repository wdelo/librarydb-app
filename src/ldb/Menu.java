package ldb;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ldb.util.MenuScreen;

public class Menu implements UserOption {

	private Map<Integer, UserOption> optionMap;
	private MenuScreen menuScreen;
	
	private boolean menuExited;
	
	public Menu(UserOption[] options, MenuScreen menuScreen) {
		optionMap = new HashMap<>();
		for (int i = 0; i < options.length; i++) {
			optionMap.put(i+1, options[i]);
		}
		optionMap.put(options.length, null);
		this.menuScreen = menuScreen;
		this.menuExited = false;
	}
	
	@Override
	public void execute(Connection conn, Scanner in) {
		menuScreen.displayBlank();
		int menuSelection = menuScreen.getOption(in);
		
		UserOption option = optionMap.get(menuSelection);
		if (option != null) {
			option.execute(conn, in);
		} else {
			menuExited = true;
		}
	}
	
	public boolean isExited()
	{
		return menuExited;
	}

}
