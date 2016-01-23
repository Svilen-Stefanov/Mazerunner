package com.svilen.fieldComponents;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.svilen.capstone.Game;
import com.svilen.capstone.Main;

/**<b>Class: </b> <br>
 *  The class contain the methods responsible for moving the player on the field.<br>
 * The player could be moved by pressing the keyboard arrows in the corresponding direction.<br>
 * By default, there are 3 lives at the beginning. Life is taken, when eaten by a static or a dynamic obstacle.<br>
 * The class contains variables keyTaken and exitFound, which give information about if a key has already been collected and 
 * whether an exit is found (after having taken a key).
 * @author Svilen Stefanov
 *  */
public class Player extends FieldComponent{
	/** The character, which represents the player in the labyrinth*/ 
	private char playerSymbol;
	/** Number of lives the player has*/
	private int lives;
	/** Whether a key has already been taken by the player*/ 
	private boolean keyTaken;
	/** Whether an exit is found and went through, if a key has already been taken. */
	private boolean exitFound;

	public Player(int x, int y) {
		super(x, y);
		this.playerSymbol = '\u263B';
	}
	/**Handle the user input for the movement of the player (via keyboard arrows). */
	public void movePlayer(){
		Key key = Main.main.terminal.readInput();
		
		while(key == null || key.getKind() != Key.Kind.Escape){
			key = Main.main.terminal.readInput();			
			if (key != null && key.getKind() == Key.Kind.ArrowDown)
				changeCoordinates(x, y+ 1);
			if (key != null && key.getKind() == Key.Kind.ArrowUp)
				changeCoordinates(x, y- 1);			
			if (key != null && key.getKind() == Key.Kind.ArrowLeft)
				changeCoordinates(x- 1, y);
			if (key != null && key.getKind() == Key.Kind.ArrowRight)
				changeCoordinates(x+1, y);
			if(DynamicObstacle.eatenPlayer && lives < 0)
				break;
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				Main.menu.printMessage("Error! Please try again!");
			}
		}
	}
	/**
	 * Move the character from the old to the new position
	 * @param x - the new coordinate X of the player
	 * @param y - the new coordinate Y of the player
	 */
	private void changeCoordinates(int x, int y){
		if(isCoordinateRight(x, y)){
			if(!Game.field.isBigField()){
			if(DynamicObstacle.eatenPlayer){
				if(lives >= 0){
				DynamicObstacle.eatenPlayer = true;
				} else Main.menu.game.gameOver();
			} else {
				clearOldPostion();
				this.x = x;
				this.y = y;
				Main.main.terminal.moveCursor(x, y);
				Game.field.applyForeAndBackgroundColors(0, 150);
				Main.main.terminal.putCharacter(playerSymbol);
				if(exitFound){
					Main.menu.game.gameWon();
				}
			}
			} else {
				if(DynamicObstacle.eatenPlayer){
					if(Game.field.mapXCoordinatesToScreen(this.x) < 1 || Game.field.mapYCoordinatesToScreen(this.y) < 3 || //can be reduced to 0, 2, Main.main.screenWidth and  Main.main.screenHeight
							Game.field.mapXCoordinatesToScreen(this.x) > Main.main.screenWidth - 1 || Game.field.mapYCoordinatesToScreen(this.y) > Main.main.screenHeight - 1)
						Game.field.setPageScrolled(true);
					lives -= 1;
					this.x = Game.field.getCoordinateXEntrance();
					this.y = Game.field.getCoordinateYEntrance();
					if(lives >= 0){
					Main.menu.game.saveGame(false);
					DynamicObstacle.eatenPlayer = true;
					Game.field.printField();
					} else Main.menu.game.gameOver();
				} else {
					clearOldPostionBigField();
					this.x = x;
					this.y = y;
					if(Game.field.mapXCoordinatesToScreen(this.x) < 1 || Game.field.mapYCoordinatesToScreen(this.y) < 3 || //can be reduced to 0, 2, Main.main.screenWidth and  Main.main.screenHeight
							Game.field.mapXCoordinatesToScreen(this.x) > Main.main.screenWidth - 1 || Game.field.mapYCoordinatesToScreen(this.y) > Main.main.screenHeight - 1){
						Game.field.setPageScrolled(true);
						Game.field.printField();
					} else { 
					Main.main.terminal.moveCursor(Game.field.mapXCoordinatesToScreen(x), Game.field.mapYCoordinatesToScreen(y));
					Game.field.applyForeAndBackgroundColors(0, 150);
					Main.main.terminal.putCharacter(playerSymbol);
					}
					if(exitFound)
						Main.menu.game.gameWon();
				}
			}
		} 
	}
	/** Delete the old position of the character on a big field. */
	private void clearOldPostionBigField(){
		Main.main.terminal.moveCursor(Game.field.mapXCoordinatesToScreen(x), Game.field.mapYCoordinatesToScreen(y));
		Main.main.terminal.applyBackgroundColor(Color.DEFAULT);
		Main.main.terminal.applyForegroundColor(Color.DEFAULT);
		Main.main.terminal.putCharacter(' ');
	}
	
	/** Delete the old position of the character on a small field. */
	private void clearOldPostion(){
		Main.main.terminal.moveCursor(x, y);
		Main.main.terminal.applyBackgroundColor(Color.DEFAULT);
		Main.main.terminal.applyForegroundColor(Color.DEFAULT);
		Main.main.terminal.putCharacter(' ');
	}
	
	/**
	 * Check, whether the new position is admissible. 
	 * @param x - the coordinate X to be changed, if there is no wall on the way
	 * @param y - the coordinate Y to be changed, if there is no wall on the way
	 * @return - true - the position is admissible / false - the position is inadmissible (waiting for another user input)
	 */
	private boolean isCoordinateRight(int x, int y){
		Main.main.terminal.setCursorVisible(false);

		char objectType;
		if(!Game.field.isBigField()){
		for (int i = 0; i < Game.field.listComponents.size(); i++) {
			int coordinateX = Game.field.listComponents.get(i).getX();
			int coordinateY = Game.field.listComponents.get(i).getY();
			objectType = Game.field.listComponents.get(i).getSymbol();
			if((objectType == '\u2620' || objectType == '\u262C') && (x == coordinateX && y == coordinateY)){
				DynamicObstacle.eatenPlayer = true;
				return true;
			}
			if(!keyTaken){
				if((x == coordinateX && y == coordinateY) && (objectType == 'X' || objectType == 'O') 
						|| x < Game.field.getCoordinateX() || x > (Game.field.getCoordinateX() + Game.field.getWidth() - 1) || y < Game.field.getCoordinateY() || y > (Game.field.getCoordinateY() + Game.field.getHeight() - 1)){
					return false;
				}
				if((x == coordinateX && y == coordinateY) && objectType == '\u2625'){
					keyTaken = true;
					Game.field.listComponents.remove(i);
					printKeyIsTaken();
				}
			} else {
				if(objectType == 'O' && (x == coordinateX && y == coordinateY))
					exitFound = true;
				if((x == coordinateX && y == coordinateY) && (objectType == 'X') 
						|| x < Game.field.getCoordinateX() || x > (Game.field.getCoordinateX() + Game.field.getWidth() - 1) || y < Game.field.getCoordinateY() || y > (Game.field.getCoordinateY() + Game.field.getHeight() - 1)){
					return false;
				}
				if((x == coordinateX && y == coordinateY) && objectType == '\u2625')
					Game.field.listComponents.remove(i);
			}
			coordinateX = Game.field.getCoordinateX();
			coordinateY = Game.field.getCoordinateY();
		}
		return true;
		} else {
			for (int i = 0; i < Game.field.componentPositions.length; i++) {
				for (int j = 0; j < Game.field.componentPositions[i].length; j++) {
					objectType = Game.field.componentPositions[i][j];
					if((objectType == '\u2620' || objectType == '\u262C') && (x == j && y == i))
						DynamicObstacle.eatenPlayer = true;
					if(!keyTaken){
						if((x == j && y == i) && (objectType == 'X' || objectType == 'O') 
								|| x < 0 || x > Game.field.getWidth() - 1 || y < 0 || y > Game.field.getHeight() - 1){
							return false;
						}
						if((x == j && y == i) && objectType == '\u2625'){
							keyTaken = true;
							Game.field.componentPositions[i][j] = ' ';
							printKeyIsTaken();
						}
					} else {
						if(objectType == 'O' && (x == j && y == i))
							exitFound = true;
						if((x == j && y == i) && (objectType == 'X') 
								|| x < 0 || x > Game.field.getWidth() - 1 || y < 0 || y > Game.field.getHeight() - 1){
							return false;
						}
						if((x == j && y == i) && objectType == '\u2625')
							Game.field.componentPositions[i][j] = ' ';
					}
				}		
			} return true;
		}
	}
	
	/**@return - whether a key has been already taken, which can open an exit to the labyrinth.*/
	public boolean isKeyTaken() {
		return keyTaken;
	}
	
	/** Change the key taken status (text) on the screen to "yes". */ 
	private void printKeyIsTaken(){
		Main.main.terminal.moveCursor(41, 0);
		Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
	    Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
		String isKeyTaken = "yes";
		for (int k = 0; k < isKeyTaken.length(); k++) {
			Main.main.terminal.putCharacter(isKeyTaken.charAt(k));
		}
	}
	
	public void setKeyTaken(boolean keyTaken) {
		this.keyTaken = keyTaken;
	}
	/** @return - the number of lives left.*/
	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}
	/**@return - the character, which represents the player in the labyrinth. */ 
	public char getSymbol() {
		return playerSymbol;
	}

	/** @return - whether an exit is found and went through, if a key has already been taken. */
	public boolean isExitFound() {
		return exitFound;
	}	
}