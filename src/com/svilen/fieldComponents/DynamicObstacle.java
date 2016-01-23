package com.svilen.fieldComponents;
import java.util.Random;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.svilen.capstone.Game;
import com.svilen.capstone.Main;

/**<b>Class: </b> <br>
 * Dynamic obstacle class, which inherit the x and y variables, implements and overrides the getSymbol method from the parent class. <br>
 * The class is responsible for the movement of the dynamic obstacles.
 * @author Svilen Stefanov
 */
public class DynamicObstacle extends FieldComponent{
	/** The character, which represents a dynamic obstacle in the labyrinth. */ 
	private char dynamicObstacleSymbol;
	/** Whether an obstacle has eaten the player or the player bumped into an dynamic/static obstacle*/
	public static boolean eatenPlayer;
	
	public DynamicObstacle(int x, int y) {
		super(x, y);
		this.dynamicObstacleSymbol = '\u2620';
	}
	/** Control the movement of the dynamic obstacles - the new position is chosen random.
	 * Before moving the dynamic obstacle to the new position, the method check by calling moveToRightCoordinate whether the new coordinates are right. */
	public void moveDynamicObstacle(){
		int xCoordinate = new Random().nextInt(3);
		int yCoordinate = new Random().nextInt(3);
		
		if(1 - xCoordinate != 0){
			xCoordinate = 1 - xCoordinate;
			yCoordinate = 0;
		}
		else {
			xCoordinate = 0;
			yCoordinate = 1 - yCoordinate;
		}
		if(moveToRightCoordinate(x + xCoordinate, y + yCoordinate)){
		if(!Game.field.isBigField()){
			if(eatenPlayer){
				Game.player.setLives(Game.player.getLives() - 1);
				if(Game.player.getLives() >= 0){
				changeSmallCooridnates(xCoordinate, yCoordinate);
				
				Main.menu.game.saveGame(false);	
				eatenPlayer = true;
				int coordinateeX = Game.field.getCoordinateX();
				int coordinateeY = Game.field.getCoordinateY();
				
				for (int k = 0; k < Game.field.listComponents.size(); k++) {
					coordinateeX += Game.field.listComponents.get(k).getX();
					coordinateeY += Game.field.listComponents.get(k).getY();
					Game.field.listComponents.get(k).setX(coordinateeX);
					Game.field.listComponents.get(k).setY(coordinateeY);
					coordinateeX = Game.field.getCoordinateX();
					coordinateeY = Game.field.getCoordinateY();
				}
				Game.field.centerCoordinates();
				Game.player.setX(Game.field.getCoordinateXEntrance() + Game.field.getCoordinateX());
				Game.player.setY(Game.field.getCoordinateYEntrance() + Game.field.getCoordinateY());
				Game.field.printField();
				} else return;
			}
			changeSmallCooridnates(xCoordinate, yCoordinate);
		} else {
			if(eatenPlayer){
				Game.player.setLives(Game.player.getLives() - 1);
				if(Game.player.getLives() >= 0){
					changeBigCoordinates(xCoordinate, yCoordinate);
					Main.menu.game.saveGame(false);
					eatenPlayer = true;
					Game.player.setX(Game.field.getCoordinateXEntrance());
					Game.player.setY(Game.field.getCoordinateYEntrance());
					Game.field.printField();
				} else return;
			}
			changeBigCoordinates(xCoordinate, yCoordinate);
			}
		}
	}
	/**
	 * Move and draw the obstacle to the new coordinates on a big field.
	 * @param xCoordinate - shift on the X axis.
	 * @param yCoordinate - shift on the Y axis.
	 */
	private void changeBigCoordinates (int xCoordinate, int yCoordinate){
		clearOldBigFieldPosition();
		x += xCoordinate;
		y += yCoordinate;
		Game.field.componentPositions[y][x] = dynamicObstacleSymbol;	//write the right coordinates of the obstacle in the array (with coordinate)
		if(Game.field.mapXCoordinatesToScreen(x) <= Main.main.screenWidth &&  Game.field.mapXCoordinatesToScreen(x) > 0 &&
				Game.field.mapYCoordinatesToScreen(y) <= Main.main.screenHeight &&  Game.field.mapYCoordinatesToScreen(y) >= 2){
			Main.main.terminal.moveCursor(Game.field.mapXCoordinatesToScreen(x), Game.field.mapYCoordinatesToScreen(y));
			Main.main.terminal.applyBackgroundColor(0);
			Main.main.terminal.applyForegroundColor(Terminal.Color.RED);
			Main.main.terminal.putCharacter(dynamicObstacleSymbol);
		}
	}
	
	/**
	 * Move and draw the obstacle to the new coordinates on a small field.
	 * @param xCoordinate - shift on the X axis.
	 * @param yCoordinate - shift on the Y axis.
	 */
	private void changeSmallCooridnates(int xCoordinate, int yCoordinate){
		clearOldPosition();	
		x += xCoordinate;
		y += yCoordinate;
		Main.main.terminal.moveCursor(x, y);
		Main.main.terminal.applyBackgroundColor(0);
		Main.main.terminal.applyForegroundColor(Terminal.Color.RED);
		Main.main.terminal.putCharacter(dynamicObstacleSymbol);
	}
	
	/** Delete the old position of the dynamic obstacle from the screen and from the array - used for a big field. */
	private void clearOldBigFieldPosition(){
		Main.main.terminal.moveCursor(Game.field.mapXCoordinatesToScreen(x), Game.field.mapYCoordinatesToScreen(y));
		Game.field.componentPositions[y][x] = ' ';
		if(Game.field.mapXCoordinatesToScreen(x) <= Main.main.screenWidth && Game.field.mapXCoordinatesToScreen(x) > 0 &&
				Game.field.mapYCoordinatesToScreen(y) <= Main.main.screenHeight	&&  Game.field.mapYCoordinatesToScreen(y) >= 2){
			Main.main.terminal.applyBackgroundColor(Color.DEFAULT);
			Main.main.terminal.applyForegroundColor(Color.DEFAULT);
			Main.main.terminal.putCharacter(' ');
		}
	}
	/** Delete the old position of the dynamic obstacle from the screen - used for a small field. */
	private void clearOldPosition(){
		Main.main.terminal.moveCursor(x, y);
		Main.main.terminal.applyBackgroundColor(Color.DEFAULT);
		Main.main.terminal.applyForegroundColor(Color.DEFAULT);
		Main.main.terminal.putCharacter(' ');
	}
	
	/**
	 * Check whether the new coordinates are admissible, before moving the dynamic obstacle to the new position.
	 * @param x - the new X coordinate
	 * @param y - the new Y coordinate
	 * @return - true, if admissible, otherwise - false
	 */
	private boolean moveToRightCoordinate(int x, int y){
		Main.main.terminal.setCursorVisible(false);	

		char objectType;
		if(!Game.field.isBigField()){
			for (int i = 0; i < Game.field.listComponents.size(); i++) {
				int coordinateX = Game.field.listComponents.get(i).getX();
				int coordinateY = Game.field.listComponents.get(i).getY();
				objectType = Game.field.listComponents.get(i).getSymbol();
				if(objectType == dynamicObstacleSymbol && (this.x == coordinateX && this.y == coordinateY)){
					continue;
				} else if (x == Game.player.getX() && y == Game.player.getY()){
					eatenPlayer = true;
					return true;
				} else {
				if((x == coordinateX && y == coordinateY) && (objectType == 'X' || objectType == 'O' || objectType == '\u2620' || objectType == '\u262C' || objectType == '\u2625') 
						|| x < Game.field.getCoordinateX() || x > (Game.field.getCoordinateX() + Game.field.getWidth() - 1) || y < Game.field.getCoordinateY() || y > (Game.field.getCoordinateY() + Game.field.getHeight() - 1)){
					return false;
				}
				coordinateX = Game.field.getCoordinateX();
				coordinateY = Game.field.getCoordinateY();
				} 
			}	return true;
		} else {
				objectType = Game.field.componentPositions[y][x];
				if (x == Game.player.getX() && y == Game.player.getY()){
						eatenPlayer = true;
						return true;
					} else {
						eatenPlayer = false;
						if(objectType == 'X' || objectType == 'O' || objectType == '\u2620' || objectType == '\u262C' || objectType == '\u2625' || x <= 0
								|| x > Game.field.getWidth() || y < 0 || y > Game.field.getHeight())
							return false;			
				}	
		} return true;

	}
	
	/** @return - the character, which represents a dynamic obstacle in the labyrinth. */ 
	public char getSymbol() {
		return dynamicObstacleSymbol;
	}
}
