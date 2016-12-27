package com.elveskevtar.snake.gfx;

import javax.swing.JFrame;

/**
 * The JFrame object that holds all of the graphical aspects of the game.
 * 
 * @author Elveskevtar
 * @since 1.0
 */
public class Frame extends JFrame {

	private static final long serialVersionUID = 8695325619069488171L;

	/**
	 * Creates the JFrame object, sets its specifications, and adds a Game
	 * object as a child component.
	 */
	public Frame(int width, int height) {
		/* calls the superconstructor for JFrame and sets the frame's title */
		super("Snake");

		/* sets the JFrame to not resizable; must be called before pack() */
		this.setResizable(false);

		/* packs the frame and sets the sides with insets in mind */
		this.pack();
		this.setSize(width + getInsets().left + getInsets().right, height + getInsets().top + getInsets().bottom);
		this.setPreferredSize(getSize());

		/* sets the exit method, manual layout, and packs the frame */
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.pack();

		/* centers the frame on the monitor */
		this.setLocationRelativeTo(null);

		/*
		 * creates a new game object, adds it to the frame, and makes the frame
		 * visible
		 */
		this.add(new Game(this));
		this.setVisible(true);
	}
}