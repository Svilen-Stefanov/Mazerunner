package com.svilen.fieldComponents;
import java.util.LinkedList;

import com.googlecode.lanterna.terminal.Terminal;
import com.svilen.capstone.Game;
import com.svilen.capstone.Main;
/**<b>Class: </b> <br>
 *The class is responsible for printing the labyrinth.<br>
 *For a small field with the size of maximum the terminal size, all objects are saved in a separate linked list.<br>
 *For a big field, due to the big amount of objects(including the walls), only the moving are saved in a linked list (dynamic obstacles). <br>
 * @author Svilen Stefanov
 */
public class Field {
	/** List of all objects. */
	public LinkedList<FieldComponent> listComponents;
	/** List of Dynamic Obstacles*/
	public LinkedList<DynamicObstacle> listDynamicObstacles;	
	/** Array, which helps the field to be printed. */
	public char[][] componentPositions;
	/** The width of the field.*/
	private int width;
	/** The height of the field.*/ 
	private int height;
	/** The changed X coordinate of the objects, when a small field is printed, so that the field could be centered. */
	private int coordinateX;
	/** The changed Y coordinate of the objects, when a small field is printed, so that the field could be centered. */
	private int coordinateY;
	/** Coordinates of the player on the big field. */
	private int playerStartCoordinateX, playerStartCoordinateY;		
	/** The point from where the field is printed. */
	int fieldStartX, fieldStartY;
	/** Coordinates of the entrance, where the player is placed at the beginning.*/
	private int coordinateXEntrance, coordinateYEntrance;
	private final int WHITECOLOR = 0;
	private final int BLUE = 21;
	private final int VIOLET = 140;
	/** Whether the field is bigger then the screen. */
	private boolean isBigField;
	/** Whether the screen was scrolled. */
	private boolean pageScrolled;
	/**Check whether properties without the coordinates of the player are loaded. */
	private boolean loadWithoutPlayerCoordinates;
	/**Counts the number of entrances - help to place the player on only one of them. */
	private int entranceCount;
	
	public Field() {
		init();
	}
	
	/** Initialize a field by reading the properties file and saving its elements in array/list.<br>
	 * It also sets height and width of the field, which makes printing of a field possible. */
	private void init(){
		if ((Main.menu.isReturnToMenu()) && !Main.menu.isLoaded())
			Main.menu.game.loadGame(0);
		else if (Main.menu.isLoaded() && Main.menu.isStarted())
			Main.menu.game.loadGame(1);	
		else if (!Main.menu.isLoaded() && !Main.menu.isReturnToMenu())
			Main.menu.game.loadGame(-1);

		//will not be initialized only if the user has clicked the load button, but hasn't given a proper name of a file   
		if(!Main.menu.game.isBroken()){
		height = Integer.parseInt(Game.gameStatus.getProperty("Height"));
		width = Integer.parseInt(Game.gameStatus.getProperty("Width"));
		
		listComponents = new LinkedList<FieldComponent>();
		listDynamicObstacles = new LinkedList<DynamicObstacle>();
		componentPositions = new char[height][width];
		String position = new String();
		isBigField = ((width > Main.main.screenWidth) || (height > (Main.main.screenHeight - 2)));
		
		for (int i = 0; i < componentPositions.length; i++) {		
			for (int j = 0; j < componentPositions[i].length; j++) {
				position = j + "," + i;
				String objectInfo = Game.gameStatus.getProperty(position);
				int objectType;
				if (objectInfo != null) {
					objectType = Integer.parseInt(Game.gameStatus.getProperty(position));
					switch (objectType) {
					case 0: Wall wall = new Wall(j, i);
							componentPositions[i][j] = wall.getSymbol();
							listComponents.addLast(wall);
							break;
					case 1: Entrance entrance = new Entrance(j, i);
							entranceCount++;
							if(entranceCount == 1){
								componentPositions[i][j] = entrance.getSymbol();
								if(!Main.menu.isReturnToMenu() || !Main.menu.isLoaded()){
								playerStartCoordinateX = j;	
								playerStartCoordinateY = i;
								}
								coordinateXEntrance = j;
								coordinateYEntrance = i;
							}
							listComponents.addLast(entrance);
							break;
					case 2: Exit exit = new Exit(j, i);
							componentPositions[i][j] = exit.getSymbol();
							listComponents.addLast(exit);
							break;
					case 3: StaticObstacle staticObstacle = new StaticObstacle(j, i);
							componentPositions[i][j] = staticObstacle.getSymbol();
							listComponents.addLast(staticObstacle);
							break;
					case 4: DynamicObstacle dynamicObstacle = new DynamicObstacle(j, i);
							componentPositions[i][j] = dynamicObstacle.getSymbol();
							listComponents.addLast(dynamicObstacle);
							listDynamicObstacles.addLast(dynamicObstacle);
							break;
					case 5: KeyToExit key = new KeyToExit(j, i);
							componentPositions[i][j] = key.getSymbol();
							listComponents.addLast(key);
							break;
					}
				}
			}
		}
		
		if(!isBigField){
			centerCoordinates();
			for (int i = 0; i < listComponents.size(); i++) {
				coordinateX += listComponents.get(i).getX();
				coordinateY += listComponents.get(i).getY();
				listComponents.get(i).setX(coordinateX);
				listComponents.get(i).setY(coordinateY);
				centerCoordinates();
			}
		}
		}
	}
	
	/**Print a field, when a game is started or the terminal is resized.*/
	public void printField(){
		Main.main.screenSize();
		Main.main.terminal.clearScreen();
		Main.main.terminal.moveCursor(0, 0);
		Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
		Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
		Main.menu.printMessage("Lives left: ");
		
		if(Main.main.screenWidth > 45){
		Main.main.terminal.moveCursor(30, 0);
		Main.menu.printMessage("Key taken: ");
		} else {
			Main.main.terminal.moveCursor(0, 1);
			Main.menu.printMessage("Key taken: ");
		}
		
		if(!isBigField){
			char objectType;
			for (int i = 0; i < listComponents.size(); i++) {	
				objectType = listComponents.get(i).getSymbol();
				int x = listComponents.get(i).getX();
				int y = listComponents.get(i).getY();
				Main.main.terminal.moveCursor(x, y);
				
				switch (objectType) {
				case 'X': applyForeAndBackgroundColors(BLUE, WHITECOLOR);
						  break;
				case 'E': applyForeAndBackgroundColors(WHITECOLOR, WHITECOLOR);
						  break;
				case 'O': applyForeAndBackgroundColors(VIOLET, WHITECOLOR);
						  break;
				case '\u262C': Main.main.terminal.applyBackgroundColor(WHITECOLOR);
							   Main.main.terminal.applyForegroundColor(Terminal.Color.MAGENTA);
						       break;
				case '\u2620': Main.main.terminal.applyBackgroundColor(WHITECOLOR);
							   Main.main.terminal.applyForegroundColor(Terminal.Color.RED);
							   break;
				case '\u2625':	Main.main.terminal.applyBackgroundColor(WHITECOLOR);
								Main.main.terminal.applyForegroundColor(Terminal.Color.YELLOW);
								break;
				}
				

				if(objectType == 'E' && !Game.resized && !Main.menu.isReturnToMenu() && !Main.menu.isLoaded() && !DynamicObstacle.eatenPlayer){
					if(entranceCount == 1){
					Game.player = new Player(x, y);
					applyForeAndBackgroundColors(0, 150);
					Main.main.terminal.putCharacter(Game.player.getSymbol());
					if(!Main.menu.isReturnToMenu() && !Game.resized)
						Game.player.setLives(3);
					else {
						int lives = Integer.parseInt(Game.gameStatus.getProperty("lives"));
						Game.player.setLives(lives);
						String isKeyTaken = Game.gameStatus.getProperty("key taken");
						if (isKeyTaken.equals("true"))
							Game.player.setKeyTaken(true); 
						else Game.player.setKeyTaken(false);
					}
					}
				} else if(objectType == 'E' && (Main.menu.isReturnToMenu() || Main.menu.isLoaded()) && !Game.resized && !DynamicObstacle.eatenPlayer){
					Main.main.terminal.putCharacter(' ');
					if (entranceCount == 1){
						String playerCoordinates = Game.gameStatus.getProperty("6");
						if(playerCoordinates != null){
						int indexOfComma = playerCoordinates.indexOf(",");
						x = Integer.parseInt(playerCoordinates.substring(0, indexOfComma)) + coordinateX;
						y = Integer.parseInt(playerCoordinates.substring(indexOfComma + 1, playerCoordinates.length())) + coordinateY;
						Game.player = new Player(x, y);
						Main.main.terminal.moveCursor(x, y);
						applyForeAndBackgroundColors(0, 150);
						Main.main.terminal.putCharacter(Game.player.getSymbol());
						int lives = Integer.parseInt(Game.gameStatus.getProperty("lives"));
						Game.player.setLives(lives);
						String isKeyTaken = Game.gameStatus.getProperty("key taken");
						if (isKeyTaken.equals("true"))
							Game.player.setKeyTaken(true); 
						else
							Game.player.setKeyTaken(false);
						} else {
						Game.player = new Player(x, y);
						applyForeAndBackgroundColors(0, 150);
						Main.main.terminal.moveCursor(x, y);
						Main.main.terminal.putCharacter(Game.player.getSymbol());
						Game.player.setLives(3);}
					}
				} else if(objectType == 'E' && Game.resized && !DynamicObstacle.eatenPlayer){
					Main.main.terminal.putCharacter(' ');
					if(Main.menu.isLoaded()){
						String playerCoordinates = Game.gameStatus.getProperty("6");
						if(playerCoordinates != null){
						int indexOfComma = playerCoordinates.indexOf(",");
						x = Integer.parseInt(playerCoordinates.substring(0, indexOfComma)) + coordinateX;
						y = Integer.parseInt(playerCoordinates.substring(indexOfComma + 1, playerCoordinates.length())) + coordinateY;
						Game.player = new Player(x, y);
						Main.main.terminal.moveCursor(x, y);
						applyForeAndBackgroundColors(0, 150);
						Main.main.terminal.putCharacter(Game.player.getSymbol());
						int lives = Integer.parseInt(Game.gameStatus.getProperty("lives"));
						Game.player.setLives(lives);
						String isKeyTaken = Game.gameStatus.getProperty("key taken");
						if (isKeyTaken.equals("true"))
							Game.player.setKeyTaken(true); 
						else
							Game.player.setKeyTaken(false);
					}} 
					x = Game.player.getX();
					y = Game.player.getY();
					Main.main.terminal.moveCursor(x, y);
					applyForeAndBackgroundColors(0, 150);
					Main.main.terminal.putCharacter(Game.player.getSymbol());
				} else if(objectType == 'E' && DynamicObstacle.eatenPlayer){
					x = Game.player.getX();
					y = Game.player.getY();
					Main.main.terminal.moveCursor(x, y);
					applyForeAndBackgroundColors(0, 150);
					Main.main.terminal.putCharacter(Game.player.getSymbol());
				}else Main.main.terminal.putCharacter(objectType);
			} 
			DynamicObstacle.eatenPlayer = false;
			if(!DynamicObstacle.eatenPlayer)
				Game.resized = false;
			if(Main.menu.isLoaded())	
				Main.menu.setLoaded(false);
		} else printBigField();

		Main.main.terminal.moveCursor(12, 0);
		String numberOfLives = Game.player.getLives() + "";
		Main.main.terminal.applyBackgroundColor(Terminal.Color.DEFAULT);
		Main.main.terminal.applyForegroundColor(Terminal.Color.DEFAULT);
		for (int i = 0; i < numberOfLives.length(); i++) {
			Main.main.terminal.putCharacter(numberOfLives.charAt(i));
		}
		
		if(Main.main.screenWidth > 45){
		Main.main.terminal.moveCursor(41, 0);
		} else {Main.main.terminal.moveCursor(11, 1);}
		String isKeyTaken = Game.player.isKeyTaken() + "";
		if (isKeyTaken.equals("true"))
			isKeyTaken = "yes";
		else isKeyTaken = "no";
		for (int i = 0; i < isKeyTaken.length(); i++) {
			Main.main.terminal.putCharacter(isKeyTaken.charAt(i));
		}
		
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				String problem = "Error! Please restart the game!";
				for (int i = 0; i < problem.length(); i++) {
					Main.main.terminal.putCharacter(problem.charAt(i));
				}
			}
	}
	
	/** Print the field, if the width of the field or the height of the field are bigger then the screen size. */
	public void printBigField(){
		if((Main.menu.isLoaded() || Main.menu.isReturnToMenu()) && !pageScrolled && !DynamicObstacle.eatenPlayer){
			String playerCoordinates = Game.gameStatus.getProperty("6");
			if(playerCoordinates != null){
				int indexOfComma = playerCoordinates.indexOf(",");
				int x = Integer.parseInt(playerCoordinates.substring(0, indexOfComma));
				int y = Integer.parseInt(playerCoordinates.substring(indexOfComma + 1, playerCoordinates.length()));
				if(!Main.menu.isLoaded()){
					Game.player.setX(x);
					Game.player.setY(y);
				}
				if(Main.menu.isLoaded()){
					Game.player = new Player(x, y);
					playerStartCoordinateX = x;
					playerStartCoordinateY = y;
				}			
				int lives = Integer.parseInt(Game.gameStatus.getProperty("lives"));
				Game.player.setLives(lives);
				String isKeyTaken = Game.gameStatus.getProperty("key taken");
				if (isKeyTaken.equals("true"))
					Game.player.setKeyTaken(true); 
				else Game.player.setKeyTaken(false);
			} else {
				if(Main.menu.isLoaded()){
					if(entranceCount == 1){
					Game.player = new Player(coordinateXEntrance, coordinateYEntrance);
					Game.player.setLives(3);
					loadWithoutPlayerCoordinates = true;
					}
				}
			}
		}
		
			if(DynamicObstacle.eatenPlayer){
				int checkFieldStartX = (coordinateXEntrance - Main.main.screenWidth/2 > 0) ? coordinateXEntrance - Main.main.screenWidth/2 : 0;
				int checkFieldStartY = (coordinateYEntrance - Main.main.screenHeight/2 > 0) ? coordinateYEntrance - Main.main.screenHeight/2 : 0;
				setPrintCoordinates(checkFieldStartX, checkFieldStartY);
			} else if(!pageScrolled && !Main.menu.isReturnToMenu()){
				int checkFieldStartX = (playerStartCoordinateX - Main.main.screenWidth/2 > 0) ? playerStartCoordinateX - Main.main.screenWidth/2 : 0;
				int checkFieldStartY = (playerStartCoordinateY - Main.main.screenHeight/2 > 0) ? playerStartCoordinateY - Main.main.screenHeight/2 : 0;
				setPrintCoordinates(checkFieldStartX, checkFieldStartY);
			} else {
				int checkFieldStartX = (Game.player.getX() - Main.main.screenWidth/2 > 0) ? Game.player.getX() - Main.main.screenWidth/2 : 0;
				int checkFieldStartY = (Game.player.getY() - Main.main.screenHeight/2 > 0) ? Game.player.getY() - Main.main.screenHeight/2 : 0;
				setPrintCoordinates(checkFieldStartX, checkFieldStartY);
			}
			
			int checkX = (width > (fieldStartX + Main.main.screenWidth)) ? fieldStartX + Main.main.screenWidth : width - 1;
			int checkY = (height > (fieldStartY + Main.main.screenHeight)) ? fieldStartY + Main.main.screenHeight : height;
			coordinatePrint(checkX, checkY);
				
			if((Main.menu.isReturnToMenu() || Game.resized || Main.menu.isLoaded() || pageScrolled) && !DynamicObstacle.eatenPlayer){
				Main.main.terminal.moveCursor(mapXCoordinatesToScreen(Game.player.getX()), mapYCoordinatesToScreen(Game.player.getY()));
				applyForeAndBackgroundColors(0, 150);
				Main.main.terminal.putCharacter(Game.player.getSymbol());
				if(Main.menu.isLoaded())	
					Main.menu.setLoaded(false);
			}
			if(DynamicObstacle.eatenPlayer)
				DynamicObstacle.eatenPlayer = false;		
	}
	
	/** Set the cursor at the right position on the screen and call the printOnScreen method to print the specified character on it, until the coordinate
	 * of the last point to be printed on the screen is reached.
	 * @param jCondition - the X coordinate of the last point to be printed on the screen.
	 * @param iCondition - the Y coordinate of the last point to be printed on the screen.
	 */
	public void coordinatePrint(int jCondition, int iCondition){
		for(int i = fieldStartY; i <= iCondition - 1; i++) {
			  for(int j = fieldStartX; j <= jCondition; j++) {
				  Main.main.terminal.moveCursor(mapXCoordinatesToScreen(j), mapYCoordinatesToScreen(i));
				  printOnScreen(i, j);
			  }
		}
	}
	/** Set the start point of the big field, from which the labyrinth is painted on the screen. 
	 * @param fieldX - the x coordinate of these start point.
	 * @param fieldY - the y coordinate of these start point.
	 */
	public void setPrintCoordinates(int fieldX, int fieldY){
		fieldStartX = fieldX;
		fieldStartY = fieldY;
	}
	
	/** Convert the X coordinate from the big field, to the field to be printed on the screen.
	 * @param fieldX - the x coordinate from the big field.
	 * @return - the coordinate on the screen.
	 */
	public int mapXCoordinatesToScreen(int fieldX) {
			return fieldX-fieldStartX;
	}
	/** Convert the Y coordinate from the big field, to the field to be printed on the screen.
	 * @param fieldY - the y coordinate from the big field.
	 * @return - the coordinate on the screen.
	 */ 
	public int mapYCoordinatesToScreen(int fieldY) {
		return fieldY-fieldStartY + 2;
	}
	/** Print the characters of the labyrinth at the specified position on the screen.
	 * @param i	- the Y coordinate of the position to be printed.
	 * @param j - the X coordinate of the position to be printed.
	 */
	public void printOnScreen(int i, int j){
		char objectType;
		objectType = componentPositions[i][j];
				switch (objectType) {
				case 'X': applyForeAndBackgroundColors(BLUE, WHITECOLOR);
						  break;
				case 'E': applyForeAndBackgroundColors(WHITECOLOR, WHITECOLOR);
						  break;
				case 'O': applyForeAndBackgroundColors(VIOLET, WHITECOLOR);
						  break;
				case '\u262C': Main.main.terminal.applyBackgroundColor(WHITECOLOR);
							   Main.main.terminal.applyForegroundColor(Terminal.Color.MAGENTA);
						       break;
				case '\u2620': Main.main.terminal.applyBackgroundColor(WHITECOLOR);
							   Main.main.terminal.applyForegroundColor(Terminal.Color.RED);
							   break;
				case '\u2625':	Main.main.terminal.applyBackgroundColor(WHITECOLOR);
								Main.main.terminal.applyForegroundColor(Terminal.Color.YELLOW);
								break;
				default: 		applyForeAndBackgroundColors(WHITECOLOR, WHITECOLOR);
								break;
				}
				if(objectType == 'E' && !Main.menu.isReturnToMenu() && !Main.menu.isLoaded() && !Game.resized && !pageScrolled && !DynamicObstacle.eatenPlayer){
					coordinateXEntrance = j;
					coordinateYEntrance = i;
					Game.player = new Player(j, i); 
					applyForeAndBackgroundColors(0, 150);
					Main.main.terminal.putCharacter(Game.player.getSymbol());
					if(!Main.menu.isReturnToMenu())
						Game.player.setLives(3);
					else {
						int lives = Integer.parseInt(Game.gameStatus.getProperty("lives"));
						Game.player.setLives(lives);
						String isKeyTaken = Game.gameStatus.getProperty("key taken");
						if (isKeyTaken.equals("true"))
							Game.player.setKeyTaken(true); 
						else Game.player.setKeyTaken(false);
					}
				} else if(objectType == 'E' && (Main.menu.isReturnToMenu() || Main.menu.isLoaded()) && !DynamicObstacle.eatenPlayer){
					Main.main.terminal.putCharacter(' ');
					if(entranceCount == 1){
					if(Game.player != null){
						if(!loadWithoutPlayerCoordinates){
						int lives = Integer.parseInt(Game.gameStatus.getProperty("lives"));
						Game.player.setLives(lives);
						String isKeyTaken = Game.gameStatus.getProperty("key taken");
						if (isKeyTaken.equals("true"))
							Game.player.setKeyTaken(true); 
						else Game.player.setKeyTaken(false);
						}
					} else {
						coordinateXEntrance = j;
						coordinateYEntrance = i;
						Game.player = new Player(j, i);
						applyForeAndBackgroundColors(0, 150);
						Main.main.terminal.putCharacter(Game.player.getSymbol());
						Game.player.setLives(3);
					}
					}
				} else if(objectType == 'E' && Game.resized && !DynamicObstacle.eatenPlayer){
					Main.main.terminal.putCharacter(' ');
				} else if(objectType == 'E' && DynamicObstacle.eatenPlayer){
					Game.player.setX(coordinateXEntrance);
					Game.player.setY(coordinateYEntrance);
					applyForeAndBackgroundColors(0, 150);
					Main.main.terminal.putCharacter(Game.player.getSymbol());
					DynamicObstacle.eatenPlayer = false;
				} else Main.main.terminal.putCharacter(componentPositions[i][j]);
	}
	
	/**@param backgroundColor - background color value
	 * @param foregroundColor - foreground color value*/
	protected void applyForeAndBackgroundColors(int backgroundColor, int foregroundColor){
		Main.main.terminal.applyBackgroundColor(backgroundColor);
		Main.main.terminal.applyForegroundColor(foregroundColor);
	}
	/**Center the coordinates of a small field. */
	public void centerCoordinates(){
		coordinateX = Main.main.screenWidth/2 - width/2;
		coordinateY = Main.main.screenHeight/2 - height/2 + 1;
	}
	
	/**Change the coordinates of the small field objects to such, ready to be saved. */
	public void changeToNormalCoordinates(){
		for (int i = 0; i < listComponents.size(); i++) {
			int coordinateNewX = listComponents.get(i).getX() - coordinateX;
			int coordinateNewY = listComponents.get(i).getY() - coordinateY;
			listComponents.get(i).setX(coordinateNewX);
			listComponents.get(i).setY(coordinateNewY);
		}
		int playerCoordinateX = Game.player.getX() - coordinateX;
		int playerCoordinateY = Game.player.getY() - coordinateY;
		Game.player.setX(playerCoordinateX);
		Game.player.setY(playerCoordinateY);
	}
	
	/**Change the coordinates of the small field objects to such, ready to be printed in the center of the screen. */
	public void changeToSmallCoordinates(){
		for (int i = 0; i < listComponents.size(); i++) {
			int coordinateNewX = listComponents.get(i).getX() + coordinateX;
			int coordinateNewY = listComponents.get(i).getY() + coordinateY;
			listComponents.get(i).setX(coordinateNewX);
			listComponents.get(i).setY(coordinateNewY);
		}
		int playerCoordinateX = Game.player.getX() + coordinateX;
		int playerCoordinateY = Game.player.getY() + coordinateY;
		Game.player.setX(playerCoordinateX);
		Game.player.setY(playerCoordinateY);
	}
	
	/** @return - the width of the field. */
	public int getWidth() {
		return width;
	}
	/**@return - the height of the field. */
	public int getHeight() {
		return height;
	}
	/** @return - the changed X coordinate of the objects, when a small field is printed, so that the field could be centered. */
	public int getCoordinateX() {
		return coordinateX;
	}
	/** @return - the changed Y coordinate of the objects, when a small field is printed, so that the field could be centered. */
	public int getCoordinateY() {
		return coordinateY;
	}
	/** @return - whether the field is bigger then the screen.*/
	public boolean isBigField() {
		return isBigField;
	}
	
	public void setBigField(boolean isBigField) {
		this.isBigField = isBigField;
	}

	/** @return - whether the screen was scrolled. */
	public boolean isPageScrolled() {
		return pageScrolled;
	}
	
	public void setPageScrolled(boolean pageScrolled) {
		this.pageScrolled = pageScrolled;
	}

	/** @return - y coordinate of the entrance, where the player is placed at the beginning. */
	public int getCoordinateXEntrance() {
		return coordinateXEntrance;
	}

	/** @return - y coordinate of the entrance, where the player is placed at the beginning. */
	public int getCoordinateYEntrance() {
		return coordinateYEntrance;
	}
}