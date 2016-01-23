package com.svilen.fieldComponents;
/**
 * <b>Capstone projekt </b>
 * @author Svilen Stefanov
 */
public abstract class FieldComponent {
	protected int x;
	protected int y;
	
	public FieldComponent(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/** @return - the x coordinate of the position of the object in the labyrinth. */
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	/** @return - the y coordinate of the position of the object in the labyrinth. */
	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	/** @return - the character, which represents the specified object in the labyrinth. */
	public abstract char getSymbol();
}
