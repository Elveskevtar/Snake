package com.elveskevtar.snake;

import com.elveskevtar.snake.gfx.Frame;

/**
 * A classic Snake game.
 * 
 * @author Elveskevtar
 * @version 1.0
 * @since 1.0
 */
public class Driver {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            Unused since the game will not be run from the console and
	 *            hence, does not need starting parameters.
	 */
	public static void main(String[] args) {
		/* creates a new Frame objects width a size width x height */
		new Frame(1280, 720);
	}
}