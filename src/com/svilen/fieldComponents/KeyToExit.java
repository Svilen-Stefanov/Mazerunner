package com.svilen.fieldComponents;
/**
 * <b>Capstone projekt </b> <br> <br>
 * 
 * Class for keys to unlock the exit of the labyrinth.<br>
 * The class inherit the x and y variables, implements and overrides the getSymbol method from the parent class.
 * @author Svilen Stefanov
 */
public class KeyToExit extends FieldComponent{
	/** The character, which represents a key in the labyrinth.*/ 
	private char keySymbol;
	
	public KeyToExit(int x, int y) {
		super(x, y);
		this.keySymbol = '\u2625';
	}
	
	/** The character, which represents a key in the labyrinth.*/ 
	public char getSymbol() {
		return keySymbol;
	}

}
