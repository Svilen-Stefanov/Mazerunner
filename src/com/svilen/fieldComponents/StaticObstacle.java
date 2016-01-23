package com.svilen.fieldComponents;
/**
 * <b>Capstone projekt </b> <br> <br>
 * 
 * Class for static obstacles in the labyrinth, that you might bump into, which will take you a life.<br>
 * Be careful! <br>
 * The class inherit the x and y variables, implements and overrides the getSymbol method from the parent class.
 * @author Svilen Stefanov
 */
public class StaticObstacle extends FieldComponent{
	/** The character, which represents a static obstacle in the labyrinth. */ 
	private char staticObstacleSymbol;
	
	public StaticObstacle(int x, int y) {
		super(x, y);
		this.staticObstacleSymbol = '\u262C';
	}

	/** The character, which represents a static obstacle in the labyrinth. */ 
	public char getSymbol() {
		return staticObstacleSymbol;
	}
}
