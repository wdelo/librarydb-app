package ldb.util;

import java.util.Scanner;

public class MenuScreen {

	private String prompt;
	private String options[];
	
	public MenuScreen(String prompt, String[] options)
	{
		this.prompt = prompt;
		this.options = options;
	}
	
	public MenuScreen(MenuScreen menu)
	{
		this.prompt = menu.prompt;
		this.options = new String[menu.options.length];
		for (int i = 0; i < options.length; i++) {
			this.options[i] = menu.options[i];
		}
	}
	
	public void display()
	{
		System.out.println(prompt);
		for (int i = 0; i < options.length; i++) {
			System.out.println( (i+1) +". "+options[i]);
		}
	}
	
	public int getOption(Scanner in) {
		return DBUtils.getValidInput(1, options.length, in);
	}
	
}
