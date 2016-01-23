package com.svilen.fieldComponents;
/**
 * <b>Capstone projekt </b> <br> <br>
 * 
 * Class for entrance in the labyrinth. <br>
 * The class inherit the x and y variables, implements and overrides the getSymbol method from the parent class.
 * @author Svilen Stefanov
 */
public class Entrance extends FieldComponent{
	/** The character, which represents the entrance of the labyrinth. */
	private char entranceSymbol;
	
	public Entrance(int x, int y) {
		super(x, y);
		this.entranceSymbol = 'E';
	}
	
	/** The character, which represents the entrance of the labyrinth. */
	public char getSymbol(){
		return entranceSymbol;
	}
	
}
