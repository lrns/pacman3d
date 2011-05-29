import org.lwjgl.opengl.GL11;


/**
 * Class represent object (character) moving in a maze
 * @author Laurynas Sukys
 *
 */
abstract class MovingCharacter {

	// coordinates of current position in OpenGL units

	protected float x = 1.0f;
	protected float y = 1.0f;

	protected GameMap map;
	
	// How fast object is going
	private float SPEED = 0.1f;
	
	// small values (1/3) of speed to help calculation positions
	private float SMALL_VALUE;

	// moving direction
	protected int moveX = 0;
	protected int moveY = 0;
	
	// next moving direction
	protected int nextX = 0;
	protected int nextY = 0;
	
	//  rotation
	protected float rotate = 0.0f;
	
	
	protected int displayListID;

	// No movement
	public boolean notMoving() {
		return moveX == 0 && moveY == 0;
	}
	
	/**
	 * 
	 * @param map Game map
	 */
	public MovingCharacter(GameMap map) {
		this.map = map;
		SMALL_VALUE = SPEED / 3;
	}
	
	
	/**
	 * Update speed
	 * @param speed
	 */
	public void setSpeed(float speed) {
		SPEED = speed;
		SMALL_VALUE = SPEED / 3;
	}

	/**
	 * Set new positions
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Change movement direction
	 * 
	 * Analyse next direction coordinates
	 */
	protected void changeDirection() {
		// change direction
		if (nextX != 0 || nextY != 0) {
			if (moveX == nextX && moveY == nextY) {
				// same direction, no changes needed
				nextX = 0;
				nextY = 0;
			} else if ((moveX == nextX && moveY == -nextY) || (moveX == -nextX && moveY == nextY)) {
				// just opposite direction, turn around immediately
				moveX = nextX;
				moveY = nextY;
				nextX = 0;
				nextY = 0;
				updateRotation();
			} else {
				// wait until the middle of square
				// and turn if square is empty
				
				// object is not moving
				if (moveX == 0 && moveY == 0) { 
					moveX = nextX;
					moveY = nextY;
					nextX = 0;
					nextY = 0;
					updateRotation();
				} else {
					// can turn - the destination square is empty
					if (inMiddleOfSquare() && map.isSquareEmpty(x + nextX * (GameMap.SQUARE_WIDTH / 2 + SMALL_VALUE), y + nextY * (GameMap.SQUARE_WIDTH/2 + SMALL_VALUE))) {
						moveX = nextX;
						moveY = nextY;
						nextX = 0;
						nextY = 0;
						updateRotation();
					}
				}
			}
		}
	}
	
	/**
	 * Single move of an object
	 */
	abstract void move();

	/**
	 * Go one step
	 * @return true if moved
	 */
	protected boolean moveForward() {
		if (moveX != 0 || moveY != 0) {
			
			// go forward to specified direction
			x += moveX * SPEED;
			y += moveY * SPEED;
			
			// check if new position is valid (edge of object is not in non-empty square)
			if (!map.isSquareEmpty(x + moveX * (GameMap.SQUARE_WIDTH / 2) + ((-moveX) * SMALL_VALUE), 
					y + moveY * (GameMap.SQUARE_WIDTH/2)+((-moveY) * SMALL_VALUE))) {
				// go back
				x -= moveX * SPEED;
				y -= moveY * SPEED;
	
				// stop movement
				moveX = 0;
				moveY = 0;
				
				nextX = 0;
				nextY = 0;
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Hack to make sure object is traveling in the center of square
	 * 
	 * That should deal with small errors of floating point calculations 
	 */
	protected void correctPosition() {

		float x1 = Math.abs(x % 1);
		float y1 = Math.abs(y % 1);
		if (x1 < SMALL_VALUE || x1 > 1 - SMALL_VALUE)
			x = Math.round(x);
		if (y1 < SMALL_VALUE || y1 > 1 - SMALL_VALUE)
			y = Math.round(y);
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	/**
	 * Update rotation according to movement direction
	 */
	private void updateRotation() {
		if (moveY == 0) {
			// move horizontally
			if (moveX > 0) {
				// right
				rotate = 270.0f;
			} else {
				// left
				rotate = 90.0f;
			}
		} else if (moveY > 0) {
			// up
			rotate = 0.0f;
		} else {
			// down
			rotate = 180.0f;
		}
	}
	
	/**
	 * Is object in the middle of square
	 * @return
	 */
	public boolean inMiddleOfSquare() {
		float x1 = Math.abs(x % 1);
		float y1 = Math.abs(y % 1);
		return (x1 < SMALL_VALUE || x1 > 1 - SMALL_VALUE) && (y1 < SMALL_VALUE || y1 > 1 - SMALL_VALUE);
	}


	public void goUp() {
		nextX = 0;
		nextY = 1;
	}

	public void goDown() {
		nextX = 0;
		nextY = -1;
	}

	public void goLeft() {
		nextX = -1;
		nextY = 0;
	}

	public void goRight() {
		nextX = 1;
		nextY = 0;
	}

	
	public void render() {
    	GL11.glPushMatrix();
    	
        GL11.glTranslatef(x, y, 0.0f);
        
        GL11.glRotatef(rotate, 0.0f, 0.0f, 1.0f);
        
        GL11.glCallList(displayListID);
        
    	GL11.glPopMatrix();
	}

}
