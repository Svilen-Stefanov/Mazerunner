package com.svilen.fieldComponents;
/**
 * <b>Capstone projekt </b> <br> <br>
 * 
 * Class for walls in the labyrinth, through which you won't be able to go.<br>
 * The class inherit the x and y variables, implements and overrides the getSymbol method from the parent class.
 * @author Svilen Stefanov
 */
public class Wall extends FieldComponent{
	/** The character, which represents a wall in the labyrinth. */
	private char wallSymbol;
	
	public Wall(int x, int y) {
		super(x, y);
		this.wallSymbol = 'X';
	}

	/** The character, which represents a wall in the labyrinth. */
	public char getSymbol() {
		return wallSymbol;
	}
	
}
