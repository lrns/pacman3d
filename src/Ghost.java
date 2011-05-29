import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

/**
 * Class represents Ghost of Pac-Man game
 * 
 * @author Laurynas Sukys
 * 
 */
public class Ghost extends MovingCharacter {

	private Sphere sphere;
	private Cylinder cylinder;

	// random numbers used for ghosts movement
	Random generator;

	private float[] color;

	// ghost is edible when Pacman is in super mode after eating a powerup
	private boolean edible = true;

	private int normalGhostDL;

	// two different appearances for edible ghosts (flashing color)
	private int edibleGhostDL;
	private int edibleGhostDL2;

	/**
	 * 
	 * @param map
	 *            game map
	 * @param color
	 *            color of ghost
	 */
	public Ghost(GameMap map, float[] color) {
		super(map);
		setSpeed(0.08f);

		this.color = color;

		sphere = new Sphere();
		cylinder = new Cylinder();

		initDL();
		generator = new Random();

		x = map.getGhostsPoint()[0];
		y = map.getGhostsPoint()[1];
		goUp();
	}

	public void setEdible(boolean edible) {
		this.edible = edible;
		displayListID = edible ? edibleGhostDL : normalGhostDL;
	}

	/**
	 * Flash ghost when super mode is near the end
	 */
	public void flash() {
		if (edible) {
			displayListID = displayListID == edibleGhostDL ? edibleGhostDL2 : edibleGhostDL;
		}
	}

	/**
	 * Move ghost to its starting point
	 */
	public void reset() {
		x = map.getGhostsPoint()[0];
		y = map.getGhostsPoint()[1];
		goUp();
		setEdible(false);
	}

	/**
	 * Make a random move
	 */
	private void randomMove() {
		int i = generator.nextInt(4);
		switch (i) {
		case 0:
			goLeft();
			break;
		case 1:
			goRight();
			break;
		case 2:
			goDown();
			break;
		case 3:
			goUp();
			break;
		default:
			break;
		}
	}

	/**
	 * Create display lists
	 * 
	 * Ghost consists of cylinder and a sphere and two spheres as eyes
	 */
	private void initDL() {
		normalGhostDL = GL11.glGenLists(1);

		GL11.glNewList(normalGhostDL, GL11.GL_COMPILE);
		GL11.glColor4f(color[0], color[1], color[2], 1.0f);
		sphere.setOrientation(GLU.GLU_OUTSIDE);
		cylinder.setOrientation(GLU.GLU_OUTSIDE);

		cylinder.draw(0.4f, 0.4f, 1.0f, 24, 24);

		GL11.glTranslatef(0.0f, 0.0f, 1.0f);
		sphere.draw(0.4f, 24, 24);

		// eyes
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glTranslatef(-0.20f, 0.25f, 0.20f);
		sphere.draw(0.15f, 16, 16);

		GL11.glTranslatef(0.4f, 0.0f, 0.0f);
		sphere.draw(0.15f, 16, 16);

		GL11.glEndList();

		// edible ghost - blue
		edibleGhostDL = GL11.glGenLists(1);

		GL11.glNewList(edibleGhostDL, GL11.GL_COMPILE);
		GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		sphere.setOrientation(GLU.GLU_OUTSIDE);
		cylinder.setOrientation(GLU.GLU_OUTSIDE);
		cylinder.draw(0.4f, 0.4f, 1.0f, 24, 24);

		GL11.glTranslatef(0.0f, 0.0f, 1.0f);
		sphere.draw(0.4f, 24, 24);

		// eyes
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glTranslatef(-0.20f, 0.25f, 0.20f);
		sphere.draw(0.15f, 16, 16);

		GL11.glTranslatef(0.4f, 0.0f, 0.0f);
		sphere.draw(0.15f, 16, 16);

		GL11.glEndList();

		// edible ghost - white
		edibleGhostDL2 = GL11.glGenLists(1);

		GL11.glNewList(edibleGhostDL2, GL11.GL_COMPILE);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		sphere.setOrientation(GLU.GLU_OUTSIDE);
		cylinder.setOrientation(GLU.GLU_OUTSIDE);
		cylinder.draw(0.4f, 0.4f, 1.0f, 24, 24);

		GL11.glTranslatef(0.0f, 0.0f, 1.0f);
		sphere.draw(0.4f, 24, 24);

		// eyes
		GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		GL11.glTranslatef(-0.20f, 0.25f, 0.20f);
		sphere.draw(0.15f, 16, 16);

		GL11.glTranslatef(0.4f, 0.0f, 0.0f);
		sphere.draw(0.15f, 16, 16);

		GL11.glEndList();

		displayListID = normalGhostDL;
	}

	/**
	 * Move a ghost
	 */
	public void move() {

		changeDirection();
		moveForward();

		// try moves while move is available
		while (notMoving()) {
			randomMove();
			changeDirection();

			// move if thats not the same or opposite direction
			if (nextX != moveX
					&& nextX != moveY
					&& !((moveX == nextX && moveY == -nextY) || (moveX == -nextX && moveY == nextY))) {
				moveForward();
			}

		}

		// Sometimes make a random move
		if (inMiddleOfSquare() && generator.nextFloat() < 0.2) {
			randomMove();
		}

		correctPosition();

	}

	/**
	 * @return the edable
	 */
	public boolean isEdable() {
		return edible;
	}

}
