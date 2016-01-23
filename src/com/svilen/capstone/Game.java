package com.svilen.capstone;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.Timer;

import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalSize;
import com.svilen.fieldComponents.Field;
import com.svilen.fieldComponents.Player;

/**<b>Class: </b> <br>
 * The class contains the logic of the game. <br>
 * There is a resize listener in the constructor to handle the alterations of the terminal.<br>
 * The class is responsible for saving, loading and playing the game.<br>
 * <br>
 * <b>Save: </b> <br>
 * The coordinates of the player are saved in the form 6=0,0 (6 - key for player / 0,0 - value/coordinates of the player).<br>
 * Key taken and lives are the keys to be saved in the property file - show if key has already been taken and how many lives are left.
 * @author Svilen Stefanov
 */
public class Game {
	/**The game field, where the labyrinth is printed.*/
	public static Field field;
	/**Player object - main character. Moved by the keyboard arrows.*/
	public static Player player;
	/** The used object to save the property file. */
	public static Properties gameStatus;
	/** Whether the terminal size has been changed. */
	public static boolean resized;
	/** Whether an old game has been started - by pressing the load button from menu. */
	private String loadedGame = new String();
	/** Whether the game is left by pressing escape. */
	private boolean gameLeft;
	/** If load game is pressed, but nothing loaded, the game is broken and the menu is loaded. */
	private boolean isBroken;
	public Timer timer;

	public Game() {
			//when the terminal is resized, the field is reprinted
		Main.main.terminal.addResizeListener(new Terminal.ResizeListener() {
			public void onResized(TerminalSize e) {
				if(!isBroken){
				resized = true;
				if(!gameLeft){
				boolean isBig = ((field.getWidth() > Main.main.screenWidth) || (field.getHeight() > (Main.main.screenHeight - 2)));
				if(field.isBigField() == false && isBig == true)
					field.changeToNormalCoordinates();
				if(field.isBigField() == true && isBig == false){
					Main.main.screenSize();
					field.centerCoordinates();
					field.changeToSmallCoordinates();
				}
				field.setBigField(isBig);
				if(!field.isBigField()){
				field.changeToNormalCoordinates();
				Main.main.screenSize();		
				field.centerCoordinates();
				
				int coordinateX = field.getCoordinateX();
				int coordinateY = field.getCoordinateY();
					for (int j = 0; j < field.listComponents.size(); j++) {
						coordinateX += field.listComponents.get(j).getX();
						coordinateY += field.listComponents.get(j).getY();
						field.listComponents.get(j).setX(coordinateX);
						field.listComponents.get(j).setY(coordinateY);
						coordinateX = field.getCoordinateX();
						coordinateY = field.getCoordinateY();
					}
					coordinateX += player.getX();
					coordinateY += player.getY();
					player.setX(coordinateX);
					player.setY(coordinateY);
				}
				field.centerCoordinates();
				Main.main.terminal.clearScreen();
				field.printField();
				}
			}}
			});
	}

	/**
	 * Conduct the game.<br>
	 * Print a field with all objects from the properties file.<br>
	 * A timer controls the position change of the dynamic obstacles.<br>
	 * The movePlayer method control the main character and change its position.
	 */
	public void playGame() {
		resized = false;
		gameLeft = false;
	    if(!resized || Main.menu.isLoaded())
			 field = new Field();				//create a field with all objects from the properties on it
		 
		if(resized && !Main.menu.isLoaded() && Main.menu.isReturnToMenu())
			 field = new Field();
		
		if(!isBroken){
		field.printField();
		if (player.getLives() >= 0) {
				//every 500 milliseconds the postions of the dynamic obstacles change
				int delay = 0;
				if(!field.isBigField())
				  delay = 500;
				if(field.isBigField())
					delay = 100;
		  		ActionListener taskPerformer = new ActionListener() {
		  			public void actionPerformed(ActionEvent evt){
	  					if (player.getLives() >= 0	&& !player.isExitFound() && !gameLeft || Main.menu.isReturnToMenu()) {
		  				if(!field.isBigField()){
		  					try {		
		  						for (int i = 0; i < field.listDynamicObstacles.size(); i++)
			  						field.listDynamicObstacles.get(i).moveDynamicObstacle();
			  				} catch (NullPointerException e){}
		  				} else {			
		  					for (int i = 0; i < field.listDynamicObstacles.size(); i++) 
		  						field.listDynamicObstacles.get(i).moveDynamicObstacle();	
		  				}
		  				}
		  			}
		  		};
		  		timer = new Timer(delay, taskPerformer);
		  		timer.start();
		  		
			player.movePlayer();
			if (player.getLives() < 0)
				gameOver();
			gameLeft = true;

			Main.menu.setReturnToMenu(true);
			timer.stop();
			saveGame(false);
			resized = false;
			Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
			Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
		} else gameOver();	//no lives left
		}
	}

	/**
	 * Save the current game.
	 * @param withName <br> - true - the user save the game intentionally from the menu<br>
	 * - false - default save (named Game) after going back to the menu 
	 */
	public void saveGame(boolean withName) {
		gameStatus = new Properties();
			
			String fileName = new String();		//the name of the file to be saved
			fileName = "";
			if (withName) {
				Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
				Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
				Main.main.terminal.clearScreen();
				Main.main.terminal.moveCursor(Main.main.screenWidth / 2 - 10, Main.main.screenHeight / 2);
				Main.menu.printMessage("Set a file name: ");
				
				Key key = Main.main.terminal.readInput();
					//waiting for input from the user to determine the name of the file to be saved
				while (key == null || true) {
					key = Main.main.terminal.readInput();
						//add the pressed letter to the file name
					if (key != null && key.getKind() == Key.Kind.NormalKey) {
						Main.main.terminal.moveCursor(Main.main.screenWidth / 2 + 7, Main.main.screenHeight / 2);
						fileName += key.getCharacter();
						Main.menu.printMessage(fileName);
					}
						//delete the latest pressed letter 
					if (key != null && key.getKind() == Key.Kind.Backspace && fileName.length() > 0) {
						Main.main.terminal.moveCursor(Main.main.screenWidth / 2 + 7, Main.main.screenHeight / 2);
						for (int i = 0; i < fileName.length(); i++)
							Main.main.terminal.putCharacter(' ');
	
						Main.main.terminal.moveCursor(Main.main.screenWidth / 2 + 7, Main.main.screenHeight / 2);
						fileName = fileName.substring(0, fileName.length() - 1);
						Main.menu.printMessage(fileName);
					}
						//save the file with the following name
					if (key != null && key.getKind() == Key.Kind.Enter)
						break;
						//going back to the menu
					if(key != null && key.getKind() == Key.Kind.Escape)
						return;
				}
					//save the game if a game is started (cannot save a game, which hasn't been started yet
				if(field != null){
				Main.main.terminal.moveCursor(Main.main.screenWidth / 2 - 4, Main.main.screenHeight / 2 + 1);
				Main.menu.printMessage("Game saved!");
	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Main.menu.printMessage("Error! Please try again!");
				}
				}
			} else fileName = "Game";	//default save game name
				//save the current lives, key taken and coordinates, only if a game has been started
				if(field != null){
			gameStatus.setProperty("Height", "" + field.getHeight());
			gameStatus.setProperty("Width", "" + field.getWidth());
			int objectValue = -1;
			if(!field.isBigField()){
				int coordinateWidth = field.getCoordinateX();
				int coordinateHeight = field.getCoordinateY();
				for (int k = 0; k < field.listComponents.size(); k++) {
					switch (field.listComponents.get(k).getSymbol()) {
					case 'X':
						objectValue = 0;
						break;
					case 'E':
						objectValue = 1;
						break;
					case 'O':
						objectValue = 2;
						break;
					case '\u262C':
						objectValue = 3;
						break;
					case '\u2620':
						objectValue = 4;
						break;
					case '\u2625':
						objectValue = 5;
						break;
					}
						//change the coordinates only by default save
					if(!withName){
						coordinateWidth = field.listComponents.get(k).getX() - coordinateWidth;
						coordinateHeight = field.listComponents.get(k).getY() - coordinateHeight;
						field.listComponents.get(k).setX(coordinateWidth);
						field.listComponents.get(k).setY(coordinateHeight);
						coordinateWidth =  field.getCoordinateX();
						coordinateHeight = field.getCoordinateY();
					}
					
					gameStatus.setProperty(field.listComponents.get(k).getX() + ","	+ field.listComponents.get(k).getY(), "" + objectValue);
				}
				gameStatus.setProperty("" + 6, (player.getX() - coordinateWidth) + "," + (player.getY() - coordinateHeight));
			} else {
				field.componentPositions[field.getCoordinateYEntrance()][field.getCoordinateXEntrance()] = 'E';
				for (int i = 0; i < field.componentPositions.length; i++) {
					for (int j = 0; j < field.componentPositions[i].length; j++) {
						switch (field.componentPositions[i][j]) {
						case 'X':
							objectValue = 0;
							gameStatus.setProperty(j + ","	+ i, "" + objectValue);
							break;
						case 'E':
							objectValue = 1;
							gameStatus.setProperty(j + ","	+ i, "" + objectValue);
							break;
						case 'O':
							objectValue = 2;
							gameStatus.setProperty(j + ","	+ i, "" + objectValue);
							break;
						case '\u262C':
							objectValue = 3;
							gameStatus.setProperty(j + ","	+ i, "" + objectValue);
							break;
						case '\u2620':			
							objectValue = 4;
							//gameStatus.setProperty(j + ","	+ i, "" + objectValue);
							break;
						case '\u2625':
							objectValue = 5;
							gameStatus.setProperty(j + ","	+ i, "" + objectValue);
							break;
						}	
					}
				}		
				
				for (int i = 0; i < field.listDynamicObstacles.size(); i++) 
					gameStatus.setProperty(field.listDynamicObstacles.get(i).getX() + ","	+ field.listDynamicObstacles.get(i).getY(), "" + 4);
				
				gameStatus.setProperty("" + 6, player.getX() + "," + player.getY() );
			}
			gameStatus.setProperty("key taken", "" + player.isKeyTaken());
			gameStatus.setProperty("lives", "" + player.getLives());
			
			try {
				gameStatus.store(new FileOutputStream(fileName), " Saved game ");
			} catch (IOException ioe) {
				System.out.println("IO Error");
			}
		
			} else {		// no game has been started yet
				Main.main.terminal.clearScreen();
				Main.main.terminal.moveCursor(Main.main.screenWidth / 2 - 10, Main.main.screenHeight / 2);
				Main.menu.printMessage("Game cannot be saved, try again! ");
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					Main.menu.printMessage("Error! Please try again!");
				}
			}
	}
	/**
	 * Load a game.
	 * @param withName <br> - 0 - for loading a default saved game (by pressing continue) <br>
	 * - 1 - intentionally loading a game - name of the loaded game is required <br>
	 * - (-1) (or other) - load labyrinth from the file (created by the generator)
	 */
	public void loadGame(int withName) {
		gameStatus = new Properties();

		if (withName == 0) // default save game name
			loadedGame = "Game";
		else if (withName == 1) { // with a name defined by the user (called directly via load button)
				Main.main.terminal.clearScreen();
				Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
				Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
				Main.main.terminal.moveCursor(Main.main.screenWidth / 2 - 10, Main.main.screenHeight / 2);
				Main.menu.printMessage("Load file: ");
	
				Key key = Main.main.terminal.readInput();
					//waiting for input from the user to determine the name of the file to be loaded
				while (key == null || true) {
					key = Main.main.terminal.readInput();
						//add the pressed letter to the file name
					if (key != null && key.getKind() == Key.Kind.NormalKey) {
						Main.main.terminal.moveCursor(Main.main.screenWidth / 2 + 1, Main.main.screenHeight / 2);
						loadedGame += key.getCharacter();
						Main.menu.printMessage(loadedGame);
					}
						//delete the latest pressed letter 
					if (key != null && key.getKind() == Key.Kind.Backspace && loadedGame.length() > 0) {
						Main.main.terminal.moveCursor(Main.main.screenWidth / 2 + 1, Main.main.screenHeight / 2);
						for (int i = 0; i < loadedGame.length(); i++)
							Main.main.terminal.putCharacter(' ');
	
						Main.main.terminal.moveCursor(Main.main.screenWidth / 2 + 1, Main.main.screenHeight / 2);
						loadedGame = loadedGame.substring(0, loadedGame.length() - 1);
						Main.menu.printMessage(loadedGame);
					}
						//search for a file with the following name
					if (key != null && key.getKind() == Key.Kind.Enter)
						break;
						//go back to menu
					if(key != null && key.getKind() == Key.Kind.Escape){
						isBroken = true;
						if(!Main.menu.isReturnToMenu()){
							Main.menu.setStarted(false);
						}
						return;
					}
				}
		} else loadedGame = "level_big_dense.properties"; // change default file name here  
	
		try {
			gameStatus.load(new FileInputStream(loadedGame));
		} catch (IOException ioe) {
			Main.main.terminal.clearScreen();
			Main.main.terminal.moveCursor(Main.main.screenWidth / 2 - 8, Main.main.screenHeight / 2);
			Main.menu.printMessage("Game not found");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Main.menu.printMessage("Error! Please try again!");
			}
			if(!Main.menu.isReturnToMenu())
				Main.menu.setStarted(false);
				//if game not found, go back to menu
			isBroken = true;
		}
	}
	
	/** Print "Game over" message on the screen. */
	public void gameOver(){
		timer.stop();
		Main.main.terminal.clearScreen();
		Main.menu.setStarted(false);
		Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
		Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
		Main.main.terminal.clearScreen();
		Main.main.terminal.moveCursor(Main.main.screenWidth / 2 - 8, Main.main.screenHeight / 2 - 1);
		Main.menu.printMessage("You've been eaten!");
		Main.main.terminal.moveCursor(Main.main.screenWidth / 2 - 4, Main.main.screenHeight / 2);
		Main.menu.printMessage("Game over");
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
		    Main.menu.printMessage("Error! Please try again!");
		}
		Main.main.terminal.exitPrivateMode();
		System.exit(0);
	}
	/** Print "You win!!!" message on the screen. */
	public void gameWon(){
		timer.stop();
		Main.main.terminal.clearScreen();
		Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
	    Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
	    Main.main.terminal.moveCursor(Main.main.screenWidth/2 - 5,  Main.main.screenHeight/2);
	    Main.menu.printMessage("You Win!!!");
			
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Main.menu.printMessage("Error! Please restart the game!");
		}			
		Main.main.terminal.exitPrivateMode();
		System.exit(0);
	}
	/** @return - if load game is pressed, but nothing loaded, the game is broken and the menu is loaded. */
	public boolean isBroken() {
		return isBroken;
	}

	public void setBroken(boolean isBroken) {
		this.isBroken = isBroken;
	}

	/** @return - is the game being loaded */
	public String getLoadedGame() {
		return loadedGame;
	}
}