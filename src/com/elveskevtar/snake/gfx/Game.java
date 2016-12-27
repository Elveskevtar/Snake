package com.elveskevtar.snake.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The Game object is a JComponent that holds all of the processes that make the
 * game function. Holds all variables, lists, constructors, methods, and
 * subthreads. Implements KeyListener to gather input from the user.
 * 
 * @author Elveskevtar
 * @since 1.0
 */
public class Game extends JPanel implements KeyListener {

	private static final long serialVersionUID = 3889445397253402117L;

	/**
	 * The number of milliseconds that the repaint and input subthreads sleep
	 * before iterating in the loop.
	 */
	private static final int REFRESH_RATE = 16;

	/**
	 * The number of milliseconds that the snake's update subthreads sleep
	 * before iterating in the loop.
	 */
	private static final int SPEED = 100;

	/** The width and height of the boxes that the screen is split up into. */
	private static final int BOXWIDTH = 20;

	/**
	 * The number of pixels of padding that adds distinction between the various
	 * segments of the snake.
	 */
	private static final int PADDING = 1;

	/** The state of the game. Flag for loops of subthreads. */
	private boolean running;

	/**
	 * The directional movement of the snake.
	 * <ul>
	 * <li>If direction = -1, at rest</li>
	 * <li>If direction = 0, up</li>
	 * <li>If direction = 1, down</li>
	 * <li>If direction = 2, left</li>
	 * <li>If direction = 3, right</li>
	 * </ul>
	 */
	private int direction;

	/**
	 * An ArrayList of Point2D objects which stores x and y coordinates for all
	 * of the segments of the snake.
	 */
	private ArrayList<Point2D> snake;

	/**
	 * An ArrayList of integers that holds the key codes of all of the keys
	 * being typed at any one moment in time.
	 */
	private ArrayList<Integer> keys;

	/** The Point2D object that holds the x and y coordinates for the food. */
	private Point2D food;

	/**
	 * The main and only constructor for the Game object. Sets the
	 * specifications for the JPanel, initializes all lists and variables, and
	 * starts the subthreads that update the game.
	 */
	public Game(JFrame frame) {
		/* calls the superconstructor for the JPanel component */
		super();

		/* initializes the keys ArrayList */
		this.setKeys(new ArrayList<Integer>());

		/*
		 * sets the size of the component with insets in mind, sets it to double
		 * buffered, sets it to focusable, and adds the keylistener that this
		 * object implements
		 */
		this.setSize(frame.getWidth() - frame.getInsets().left - frame.getInsets().right,
				frame.getHeight() - frame.getInsets().top - frame.getInsets().bottom);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.addKeyListener(this);

		/* calls the resetGame() method */
		this.resetGame();

		/* sets the game to 'run' mode and starts the subthreads */
		this.setRunning(true);
		new Thread(new Update()).start();
		new Thread(new Input()).start();
		new Thread(new Repaint()).start();
	}

	/**
	 * The overriden <code>public void paint(Graphics g)</code> method which
	 * paints all of the aspects of the game.
	 * 
	 * @param g
	 *            The Graphics object used to paint the game.
	 */
	@Override
	public void paint(Graphics g) {
		/* creates a Graphics2D object from the passed Graphics object */
		Graphics2D g2d = (Graphics2D) g;

		/* draws the background of the game */
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		/* draws the snake */
		g2d.setColor(Color.GREEN);
		for (Point2D point : snake) {
			g2d.fillRect((int) point.getX() * BOXWIDTH + PADDING, (int) point.getY() * BOXWIDTH + PADDING,
					BOXWIDTH - PADDING, BOXWIDTH - PADDING);
		}

		/* draws the food */
		g2d.setColor(Color.RED);
		g2d.fillRect((int) food.getX() * BOXWIDTH + PADDING, (int) food.getY() * BOXWIDTH + PADDING, BOXWIDTH - PADDING,
				BOXWIDTH - PADDING);
	}

	/**
	 * Resets the parameters for the game. It does not create a new Game object
	 * but rather, it resets the direction, snake, and food.
	 */
	private void resetGame() {
		/* sets the snake to be at rest */
		setDirection(-1);

		/* resets the snake to a random point on the grid */
		setSnake(new ArrayList<Point2D>());
		getSnake().add(new Point((int) (Math.random() * getWidth() / BOXWIDTH),
				(int) (Math.random() * getHeight() / BOXWIDTH)));

		/* resets the food and calls the createFood() method */
		setFood(new Point());
		createFood();
	}

	/** Essentially resets the food position. */
	private void createFood() {
		/* declares temporary variables */
		int x;
		int y;
		boolean flag;

		do {
			/* resets the flag */
			flag = false;

			/* sets a random coordinate for the temporary variables */
			x = (int) (Math.random() * getWidth() / BOXWIDTH);
			y = (int) (Math.random() * getHeight() / BOXWIDTH);

			/*
			 * iterates through the snake's segments and checks to see if the
			 * temporary coordinate intersects with any of the segments
			 */
			for (Point2D point : snake)
				if (point.distance(x, y) == 0)
					flag = true;

			/* continue looping if the temporary coordinate intersects */
		} while (flag);

		/* sets the food to the temporary coordinate */
		this.getFood().setLocation(x, y);
	}

	/**
	 * The overriden <code>public void keyPressed(KeyEvent e)</code> method that
	 * is part of the KeyListener implementation.
	 * 
	 * @param e
	 *            The KeyEvent object which specifies the key that was pressed.
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		/*
		 * if the keys ArrayList does not contain the key being pressed, add it
		 */
		if (!keys.contains(e.getKeyCode()))
			keys.add((Integer) e.getKeyCode());
	}

	/**
	 * The overriden <code>public void keyReleased(KeyEvent e)</code> method
	 * that is part of the KeyListener implementation.
	 * 
	 * @param e
	 *            The KeyEvent object which specifies the key that was released.
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		/* remove the key being released from the ArrayList */
		keys.remove((Integer) e.getKeyCode());
	}

	/**
	 * The overriden <code>public void keyTyped(KeyEvent e)</code> method that
	 * is part of the KeyListener implementation.
	 * 
	 * @param e
	 *            The KeyEvent object which specifies the key that was typed.
	 */
	@Override
	public void keyTyped(KeyEvent e) {
		/* if the ArrayList contains the escape key, stop the game */
		if (keys.contains(KeyEvent.VK_ESCAPE))
			System.exit(0);
	}

	/* standard get/set methods */
	public ArrayList<Point2D> getSnake() {
		return snake;
	}

	public void setSnake(ArrayList<Point2D> snake) {
		this.snake = snake;
	}

	public Point2D getFood() {
		return food;
	}

	public void setFood(Point2D food) {
		this.food = food;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public ArrayList<Integer> getKeys() {
		return keys;
	}

	public void setKeys(ArrayList<Integer> keys) {
		this.keys = keys;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * The Update subthread runs every <code>SPEED</code> milliseconds. It
	 * handles the movement of the snake, the checks to see if the snake ran
	 * into itself, the checks to see if the snake ran into a wall, and the
	 * checks to see if the snake runs into food.
	 * 
	 * @since 1.0
	 */
	private class Update extends Thread {

		/**
		 * The overriden <code>public void run()</code> method. Creates a loop
		 * for as long as the game is running. Is called when the Thread's
		 * <code>start()</code> method is called.
		 */
		@Override
		public void run() {
			/* creates a loop for as long as the game is running */
			while (running) {
				/*
				 * declares and initializes old variables that track the the
				 * last coordinate that was changed; starts with the leading
				 * segment of the snake
				 */
				int oldX = (int) snake.get(0).getX();
				int oldY = (int) snake.get(0).getY();

				/*
				 * moves the leading segment of the snake in the direction
				 * specified by the variable
				 */
				if (direction == 0)
					snake.get(0).setLocation(oldX, oldY - 1);
				else if (direction == 1)
					snake.get(0).setLocation(oldX, oldY + 1);
				else if (direction == 2)
					snake.get(0).setLocation(oldX - 1, oldY);
				else if (direction == 3)
					snake.get(0).setLocation(oldX + 1, oldY);

				/* moves all of the following segments to their next location */
				for (int i = 1; i < snake.size(); i++) {
					/*
					 * sets temporary variables to the coordinate of the segment
					 * being modified
					 */
					int tempX = (int) snake.get(i).getX();
					int tempY = (int) snake.get(i).getY();

					/*
					 * changes the location of the segment to the old coordinate
					 */
					snake.get(i).setLocation(oldX, oldY);

					/* sets the old variables to the temporary variables */
					oldX = tempX;
					oldY = tempY;
				}

				/* if the leading segment runs into a wall, resets the game */
				if (snake.get(0).getX() < 0 || snake.get(0).getY() < 0 || snake.get(0).getX() >= getWidth() / BOXWIDTH
						|| snake.get(0).getY() >= getHeight() / BOXWIDTH)
					resetGame();

				/*
				 * if the leading segment runs into any other part of the snake,
				 * resets the game
				 */
				for (int i = 1; i < snake.size(); i++) {
					if (snake.get(i).distance(snake.get(0)) == 0) {
						resetGame();
						break;
					}
				}

				/*
				 * if the leading segment runs into food, add a new segment to
				 * the snake and reset the food position
				 */
				if (snake.get(0).distance(food) == 0) {
					snake.add(new Point(oldX, oldY));
					createFood();
				}

				/*
				 * sleep for SPEED milliseconds; makes the loop iterate every
				 * speed milliseconds
				 */
				try {
					Thread.sleep(SPEED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * The Input subthread runs every <code>REFRESH_RATE</code> milliseconds. It
	 * handles the input of the user using the <code>keys</code> ArrayList.
	 * 
	 * @since 1.0
	 */
	private class Input extends Thread {

		/**
		 * The overriden <code>public void run()</code> method. Creates a loop
		 * for as long as the game is running. Is called when the Thread's
		 * <code>start()</code> method is called.
		 */
		@Override
		public void run() {
			/* creates a loop for as long as the game is running */
			while (running) {
				/*
				 * if the ArrayList is not empty, checks to see what keys are
				 * being pressed and sets the direction based on the checks
				 */
				if (!keys.isEmpty())
					if (keys.get(keys.size() - 1) == KeyEvent.VK_W && (direction != 1 || snake.size() == 1))
						direction = 0;
					else if (keys.get(keys.size() - 1) == KeyEvent.VK_S && (direction != 0 || snake.size() == 1))
						direction = 1;
					else if (keys.get(keys.size() - 1) == KeyEvent.VK_A && (direction != 3 || snake.size() == 1))
						direction = 2;
					else if (keys.get(keys.size() - 1) == KeyEvent.VK_D && (direction != 2 || snake.size() == 1))
						direction = 3;

				/*
				 * sleep for REFRESH_RATE milliseconds; makes the loop iterate
				 * every speed milliseconds
				 */
				try {
					Thread.sleep(REFRESH_RATE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * The Repaint subthread runs every <code>REFRESH_RATE</code> milliseconds.
	 * It handles the repainting of the Game JComponent which updates the
	 * graphics on the screen.
	 * 
	 * @since 1.0
	 */
	private class Repaint extends Thread {

		/**
		 * The overriden <code>public void run()</code> method. Creates a loop
		 * for as long as the game is running. Is called when the Thread's
		 * <code>start()</code> method is called.
		 */
		@Override
		public void run() {
			/* creates a loop for as long as the game is running */
			while (running) {
				/* calls the repaint() method to keep the component updated */
				repaint();

				/*
				 * sleep for REFRESH_RATE milliseconds; makes the loop iterate
				 * every speed milliseconds
				 */
				try {
					Thread.sleep(REFRESH_RATE);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}