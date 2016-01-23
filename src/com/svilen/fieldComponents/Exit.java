package com.svilen.fieldComponents;
/**
 * <b>Capstone projekt </b> <br> <br>
 * 
 * Class for exit from the labyrinth.<br>
 * The class inherit the x and y variables, implements and overrides the getSymbol method from the parent class.
 * @author Svilen Stefanov
 */
public class Exit extends FieldComponent{
	/** The character, which represents an exit of the labyrinth. */ 
	private char exitSymbol;
	
	public Exit(int x, int y) {
		super(x, y);
		this.exitSymbol = 'O';
	}

	/** The character, which represents an exit of the labyrinth. */
	public char getSymbol() {
		return exitSymbol;
	}

}
