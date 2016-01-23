package com.svilen.capstone;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonActionListener;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;

/**<b>Class: </b> <br>
 * The class is responsible for controlling the menu.
 * @author Svilen Stefanov
 */
public class Menu {
		//Strings to be displayed in the menu
	private final String playGame = "Play";
	private final String continueGame = "Continue";
	private final String saveGame = "Save game";
	private final String loadGame = "Load game";
	private final String instructions = "Instructions";
	private final String exitGame = "Exit";
	/** show whether the game has already been started */
	private boolean isStarted; 	
	/** show whether escape is pressed after a game has been started  */
	private boolean returnToMenu;		//Continue instead of Play is displayed in the menu
	/** true - the game is being loaded (or continued), false - a new game is started */
	private boolean isLoaded;		
	/** determines the current button */
	private int onButton = 0;	
	/** The game object. */
	public Game game;	
	
	public Menu() {
	}
	
	/**Control the menu:<br>
	 * If up or down arrow is pressed, focus the button above or below the current button.<br>
	 * If enter is pressed, the corresponding action is taken.<br>
	 * If escape is pressed, when the user is in game mode or descriptions are shown, the menu is shown again.<br>
	 * The game terminates, when the exit button is pressed.
	 */
	public void controlMenu() {
		//center the menu when the terminal is resized
		Main.main.terminal.addResizeListener(new Terminal.ResizeListener() {
			public void onResized(TerminalSize e) {
				showButton();
			}
		});
		Main.main.terminal.clearScreen();
		Key key = Main.main.terminal.readInput();
		showButton();	
		while (true) { 
			key = Main.main.terminal.readInput();
			if (key != null && key.getKind() == Key.Kind.ArrowDown)
				arrowDownPressed();
			if (key != null && key.getKind() == Key.Kind.ArrowUp)
				arrowUpPressed();
			if (key != null && key.getKind() == Key.Kind.Enter) {
				if (onButton == 4) {
					Main.main.terminal.exitPrivateMode();
					System.exit(0);
				} else {
					enterPressed();
					showButton();
				}
			}
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				String problem = "Error! Please try again!";
				for (int i = 0; i < problem.length(); i++)
					Main.main.terminal.putCharacter(problem.charAt(i));
			}
		}
	}
	
	/** Call the corresponding methods depending on the pressed button. */
	private void enterPressed() {
		game = new Game();
		switch (onButton) {
		case 0:
			game.setBroken(false);
			if(!isStarted && !returnToMenu){
			isStarted = true;
			isLoaded = false;
			Main.main.screenSize();
			game.playGame();
			} else if(returnToMenu){
				isLoaded = false;
				game.playGame();
			}		
			break;
		case 1:
			game.saveGame(true);
			break;
		case 2:
			isLoaded = true;
			isStarted = true;
			game.playGame();
			break;
		case 3:
			displayInstruktions();
			break;
		}
	}
	/** Print the menu with the cursor on the lower button. */
	private void arrowDownPressed() {
		onButton = (++onButton % 5);	//moves the current button to the index of the lower one
		switch (onButton % 5) {
		case 0:
			onPlayButton();
			break;
		case 1:
			onSaveButton();
			break;
		case 2:
			onLoadButton();
			break;
		case 3:
			onInstruktionsButton();
			break;
		case 4:
			onExitButtorn();
			break;
		}
	}
	/** Print the menu with the cursor on the upper button. */
	private void arrowUpPressed() {
		onButton = ((5 + --onButton) % 5);		//moves the current button to the index of the upper one
		switch (onButton % 5) {
		case 0:
			onPlayButton();
			break;
		case 1:
			onSaveButton();
			break;
		case 2:
			onLoadButton();
			break;
		case 3:
			onInstruktionsButton();
			break;
		case 4:
			onExitButtorn();
			break;
		}
	}
	
	/** Print the menu with the cursor on the right button. */
	public void showButton(){
		switch (onButton % 5) {
		case 0:
			onPlayButton();
			break;
		case 1:
			onSaveButton();
			break;
		case 2:
			onLoadButton();
			break;
		case 3:
			onInstruktionsButton();
			break;
		case 4:
			onExitButtorn();
			break;
		}
	}

	/** Display a brief description of the game. <br>
	 * Going back to the menu by pressing escape. */
	private void displayInstruktions() {
		Key key = Main.main.terminal.readInput();
		onButton = 0;	
		Main.main.terminal.clearScreen();
		Main.main.screenSize();
			//By pressing escape - go back to menu
		while (key == null || key.getKind() != Key.Kind.Escape) {
			key = Main.main.terminal.readInput();
			 //place the cursor where Controls: should be written
			Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 4 , Main.main.screenHeight/2 - 8);
			Main.main.terminal.applyBackgroundColor(0);
				//Text in instructions
			String controls = "Controls:"
					+ "- Navigation - arrows(up, down, left, right)"
					+ "- Escape - return to menu";
			int controls1 = controls.indexOf(":");
			int controls2 = controls.indexOf(")");
			int j = 0;
			for (int i = 0; i < controls.length(); i++) {
				if (i > controls1 && i < controls2) {
					 //place the cursor where "- Navigation - arrows(up, down, left, right)" should be written
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 14 + j, Main.main.screenHeight/2 - 7);
					j++;
				}
				if (i == (controls2 - 1))
					j = 0;
				if (i > controls2) {
					//place the cursor where "- Escape - return to menu" should be written
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 14 + j, Main.main.screenHeight/2 - 6);
					j++;
				}
				Main.main.terminal.putCharacter(controls.charAt(i));
			}
				//Text in instructions
			String legend = "Legend:" + "X - wall" + "O - exit"
					+ '\u262C' + " - static obstacle" + '\u2620' + " - dynamic obstacle"
					+ '\u2625' + " - key";
				//place the cursor where "- Escape - return to menu" should be written
			Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 3, Main.main.screenHeight/2 - 4);
				//place the cursor on a new line for every type of object described
			for (int i = 0; i < legend.length(); i++) {
				if(i==legend.indexOf(":") + 1)
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 6, Main.main.screenHeight/2 - 3);
				if(i==legend.indexOf("O"))
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 6, Main.main.screenHeight/2 - 2);
				if(i==legend.indexOf('\u262C'))
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 6, Main.main.screenHeight/2 - 1);
				if(i==legend.indexOf('\u2620'))
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 6, Main.main.screenHeight/2);
				if(i==legend.indexOf('\u2625'))
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 6, Main.main.screenHeight/2 + 1);
				Main.main.terminal.putCharacter(legend.charAt(i));
			}

			Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 6, Main.main.screenHeight/2 + 3);
				//Text in instructions
			String instructionHeading = "Instructions:"
					+ "The aim of the game is to find the exit of the labyrinth!"
					+ "In order to win the game you should first collect a key,go successfullyto the exit,"
					+ "without being eaten, and exit the labyrinth!"
					+ "Have fun :)";
			int indexOfInstructions = instructionHeading.indexOf(":");
			int indexOfText1 = instructionHeading.indexOf("!");
			int indexOfText2 = instructionHeading.lastIndexOf("!");
				//print the instructions in instructionHeading
			for (int i = 0; i < instructionHeading.length(); i++) {
				if (i == indexOfInstructions + 1)
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 28, Main.main.screenHeight/2 + 4);
				if (i == indexOfText1 + 1)
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 28, Main.main.screenHeight/2 + 5);
				if (i > indexOfText1 + 1 && i < indexOfText2 + 1) {
					if (i % 71 == 70)
						Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 28, Main.main.screenHeight/2 + 6);
				}
				if (i == indexOfText2 + 1)
					Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 4, Main.main.screenHeight/2 + 7);
				Main.main.terminal.putCharacter(instructionHeading.charAt(i));
			}

			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				printMessage("Error! Please restart the game!");
			}
		}
		Main.main.terminal.clearScreen();
	}

	/** Print the menu with the cursor on play button - coloring it in blue.  */
	private void onPlayButton() {
		printButtons(1);
	}

	/** Print the menu with the cursor on save button - coloring it in blue.  */
	private void onSaveButton() {
		printButtons(2);
	}

	/** Print the menu with the cursor on load button - coloring it in blue.  */
	private void onLoadButton() {
		printButtons(3);
	}

	/** Print the menu with the cursor on instructions button - coloring it in blue.  */
	private void onInstruktionsButton() {
		printButtons(4);
	}

	/** Print the menu with the cursor on exit button - coloring it in blue. */
	private void onExitButtorn() {
		printButtons(5);
	}
	
	/** Print all buttons on the screen with applied background color for the button at the specified index.
	 * @param i - index of the button to place place the cursor at */
	private void printButtons(int i){
		Main.main.terminal.clearScreen();
		Main.main.screenSize();
		
		if(isStarted){
			Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 4 , Main.main.screenHeight/2 - 4);
			if(i==1){Main.main.terminal.applyBackgroundColor(20);} 
			else {Main.main.terminal.applyBackgroundColor(0);}
			printMessage(continueGame);
		} else { 
			Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 2 , Main.main.screenHeight/2 - 4);
			if(i==1){Main.main.terminal.applyBackgroundColor(20);} 
			else {Main.main.terminal.applyBackgroundColor(0);}
			printMessage(playGame);
		}

		Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 4, Main.main.screenHeight/2 - 2);
		if(i==2){Main.main.terminal.applyBackgroundColor(20);} 
		else {Main.main.terminal.applyBackgroundColor(0);}
		printMessage(saveGame);

		Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 4, Main.main.screenHeight/2);
		if(i==3){Main.main.terminal.applyBackgroundColor(20);} 
		else {Main.main.terminal.applyBackgroundColor(0);}
		printMessage(loadGame);

		Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 6, Main.main.screenHeight/2 + 2);
		if(i==4){Main.main.terminal.applyBackgroundColor(20);} 
		else {Main.main.terminal.applyBackgroundColor(0);}
		printMessage(instructions);

		Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 2, Main.main.screenHeight/2 + 4);
		if(i==5){Main.main.terminal.applyBackgroundColor(20);} 
		else {Main.main.terminal.applyBackgroundColor(0);}
		printMessage(exitGame);
	}
	
	/**
	 * Print the message on the position, where the cursor is placed in the time of the calling.
	 * @param message - the text (String) to be printed
	 */
	public void printMessage(String message){
		for (int i = 0; i < message.length(); i++)
			Main.main.terminal.putCharacter(message.charAt(i));
	}
	
	/** @return - show whether escape is pressed after a game has been started  */
	public boolean isReturnToMenu() {
		return returnToMenu;
	}
	
	public void setReturnToMenu(boolean returnToMenu) {
		this.returnToMenu = returnToMenu;
	}
	
	/** @return - true - the game is being loaded (or continued), false - a new game is started */
	public boolean isLoaded() {
		return isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	/**@return - show whether the game has already been started */
	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}	
}