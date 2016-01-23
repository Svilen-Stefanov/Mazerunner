package com.svilen.capstone;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
/**
 * <b>Capstone projekt </b> <br>  <br>
 * <b>Game: </b> <br>
 * This is a maze runner game, where you should take a key,
 * find an exit and go through it, without being eaten. <br>
 * There are dynamic and static obstacles, which can take you a life and kill you if you don't have any lives left. <br>
 * Composed, using Eclipse. <br>
 * <br>
 * Menu - controlled by up/down keyboard arrow (press enter to start/save/load etc. game). <br>
 * Press enter on play button - starts by default level_big_dense.properties (must be in the folder of the project). <br>
 * The name of the default loaded file (when played clicked) could be changed in class Game, line 352. <br>
 * Game from the same directory could also be loaded by pressing enter on load button and entering the (whole) corresponding file name.<br>
 * When no lives left, the game is closed. <br>
 * <br>
 * <b>Features:</b> <br>
 * By pressing backspace, you can delete the last character of the text you've put in the save/load parts of the menu. <br>
 * The menu is being centered on resizing of the terminal. <br>
 * The player and the field are being centered when the terminal is resized. <br>
 * Key taken is written on the second line if the field is too short. <br>
 * <br>
 * <b>Class: </b> <br>
 * The class creates a terminal, which is used to draw the menu and the game on. <br>
 * The first thing to be opened is the menu, which coordinates the rest of the game. <br>
 * The game terminates when exit is pressed. <br>
 * The game ends, when you have no lives left or when you've found an exit! <br>
 * @author Svilen Stefanov
 */
public class Main{
	public static Menu menu = new Menu();
	public Terminal terminal = TerminalFacade.createTerminal();
	/** the width of the screen in pixels */
	public int screenWidth;
	/** the height of the screen in pixels */
	public int screenHeight;
	/**An object of the Main class, which is used to access the terminal. */
	public static Main main = new Main();
	
	public static void main(String[] args) {
		 main.terminal.enterPrivateMode();
		 main.terminal.setCursorVisible(false);
		 
		 main.screenSize();
		 menu.controlMenu();	//start the menu
		 main.terminal.exitPrivateMode(); 		 
	}

	/**Calculate the screenWidth and the screenHeight parameters in the main class,
	 * which are used to determine the screen size.*/
	public void screenSize(){
		TerminalSize screenSize = terminal.getTerminalSize();	
		terminal.setCursorVisible(false);
		
		screenWidth = screenSize.getColumns() - 1;
		screenHeight = screenSize.getRows() - 1;
	}
}