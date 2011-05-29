import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

/**
 * Actual game (one level)
 * 
 * @author Laurynas Sukys
 *
 */
public class Game {
	// scores
	private final static int GHOST_SCORE = 100;
	private final static int FOOD_SCORE = 10;
	private final static int POWERUP_SCORE = 30;
	
	
	private final static int SUPERMODE_DURATION = 10000;
	// supermode flashing duration
	private final static int SM_FLASH_DURATION = 4000;
	// flashing interval
	private final static int SM_FLASH_INTERVAL = 500;
	
	
	
	private GameMap map;
	private Pacman pacman;
	private Ghost[] ghosts;

	// rotate map depending on pacman position
	private float rotateX = 0.0f;
	private float rotateY = 0.0f;
	
	
	private int score = 0;
	private int lives = 3;
	private int level = 1;
	
	// is game finished (no lives or all food consumed)
	private boolean finished = false;
	
	// timer to stop super mode
	private Timer timer;

	/**
	 * 
	 * @param mapFile file to load map from
	 * @param level level number
	 * @param score initial score (when going to next level)
	 */
	public Game(String mapFile, int level, int score) {
		this.map = new GameMap(mapFile);
		this.score = score;
		this.level = level;
		
		pacman = new Pacman(map);
		ghosts = new Ghost[4];
		float[] color1 = { 1.0f, 0.4f, 0.0f };
		ghosts[0] = new Ghost(map, color1);
		float[] color2 = { 1.0f, 0.0f, 0.0f };
		ghosts[1] = new Ghost(map, color2);
		float[] color3 = { 1.0f, 0.75f, 0.79f };
		ghosts[2] = new Ghost(map, color3);
		float[] color4 = { 0.0f, 1.0f, 1.0f };
		ghosts[3] = new Ghost(map, color4);

		map.initMap();
		updateSpeed();
	}

	
	/**
	 * Tilt maze depending on pacman position
	 */
	private void tiltMaze() {
		rotateX = pacman.getX() * 2;
		rotateY = -pacman.getY() * 2;
	}

	/**
	 * Update moving speed according to FPS
	 * 
	 * Not used
	 */
	public void updateSpeedByFPS(int fps) {
		// 60 FPS - 0.1f per step
		// ghost's speed = 8/10 pacman's speed

		float speed;
		if (fps <= 0) {
			speed = 0.1f;
		} else {
			speed = 0.1f * (60.0f / fps);
		}
		pacman.setSpeed(speed);
		for (int i = 0; i < ghosts.length; i++) {
			ghosts[i].setSpeed(speed * 0.8f);

		}
	}
	
	/**
	 * Set speed depending on level
	 * 
	 * Pacman always has speed 0.1f
	 * 
	 * Ghosts are faster in higher level
	 */
	public void updateSpeed() {

		pacman.setSpeed(0.1f);
		for (int i = 0; i < ghosts.length; i++) {
			ghosts[i].setSpeed(level*0.01f+ 0.06f);

		}
	}

	/**
	 * Keyboard control
	 */
	public void handleInput() {
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			pacman.goDown();
		} else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			pacman.goUp();
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			pacman.goLeft();
		} else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			pacman.goRight();
		}
	}

	/**
	 * Update game step
	 */
	public void update() {
		tiltMaze();
		
		pacman.move();
		for (int i = 0; i < ghosts.length; i++) {
			ghosts[i].move();
			if (ghosts[i].inMiddleOfSquare()) {
				// check for portal
				float[] newSquare = map.checkForPortal(ghosts[i].getX(), ghosts[i].getY());
				if (newSquare != null) {
					ghosts[i].setPosition(newSquare[0], newSquare[1]);
				}
			}
		}
		
		// can pacman eat a food item
		checkPacmanSquare();
		
		// does pacman collide with ghost
		checkForCollisions();
		
	}
	

	private void checkPacmanSquare() {
		// check only if pacman is in middle of square
		if (pacman.inMiddleOfSquare()) {
			
			// actual map square indices
			int x = map.coordToSquareX(pacman.getX());
			int y = map.coordToSquareY(pacman.getY());
			
			if (x >= 0 && x < map.getWidth() && y >= 0 && y < map.getHeight()) {
				// in bounds
				int square = map.getSquare(x, y);

				if (square == GameMap.FOOD || square == GameMap.POWERUP) {
					// eat food item
					int remainingFood = map.eatFood(x, y);
					score += square == GameMap.FOOD ? FOOD_SCORE : POWERUP_SCORE;
					
					if (remainingFood == 0) {
						finished = true;
						// all food items consumed
					} else if (square == GameMap.POWERUP) {
						// turn super mode on
						pacman.setSuperMode(true);
						for (int i = 0; i < ghosts.length; i++) {
							ghosts[i].setEdible(true);

						}
						
						// schedule timer to start flashing after some time and then turn the super mode off
						if (timer != null) {
							timer.cancel();
						}
						timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								pacman.setSuperMode(false);
								for (int i = 0; i < ghosts.length; i++) {
									ghosts[i].setEdible(false);
									if (timer != null) {
										timer.cancel();
									}
									timer = null;
								}
							}
						}, SUPERMODE_DURATION);
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								for (int i = 0; i < ghosts.length; i++) {
									ghosts[i].flash();
								}
							}
						}, SUPERMODE_DURATION-SM_FLASH_DURATION, SM_FLASH_INTERVAL);
					}
				}
			}

			// check for portal
			float[] newSquare = map.checkForPortal(pacman.getX(), pacman.getY());
			if (newSquare != null) {
				pacman.setPosition(newSquare[0], newSquare[1]);
			}

		}

	}
	
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Check if to objects are very near each other.
	 * 
	 * Used to check if pacman collides with ghost
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private boolean veryNear(float x1, float y1, float x2, float y2) {
		final float COLLISION_DISTANCE = 0.2f;
		
		return (Math.abs(x2-x1) <= COLLISION_DISTANCE && Math.abs(y2-y1) <= COLLISION_DISTANCE);
		
	}
	
	/**
	 * Check if pacman collides with a ghost
	 */
	private void checkForCollisions() {
		float x = pacman.getX();
		float y = pacman.getY();
		
		for (int i = 0; i < ghosts.length; i++) {
			if (veryNear(x, y, ghosts[i].getX(), ghosts[i].getY())) {
				// collides
				if (pacman.inSuperMode() && ghosts[i].isEdable()) {
					// pacman eats ghost
					ghosts[i].reset();
					score += GHOST_SCORE;
				} else {
					// ghost eats pacman
					pacman.reset();

					for (int j = 0; j < ghosts.length; j++) {
						ghosts[j].reset();

					}
					lives--;
					if (lives == 0) {
						finished = true;
						// GAME OVER
						
					}
				}
				return;
			}
		}
	}

	public void render() {
		GL11.glRotatef(rotateX, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(rotateY, 1.0f, 0.0f, 0.0f);

		map.render();

		for (int i = 0; i < ghosts.length; i++) {
			ghosts[i].render();
		}
		pacman.render();
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the lives
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * @param lives the lives to set
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
}
