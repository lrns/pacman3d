import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

/**
 * User controlled Pac-Man character
 * 
 * @author Laurynas Sukys
 * 
 */
public class Pacman extends MovingCharacter {

	private Sphere sphere;
	private int superPacmanDL;
	private int normalPacmanDL;

	// is super mode (eating ghosts after eating a powerup) on
	private boolean superMode = false;

	public Pacman(GameMap map) {
		super(map);

		sphere = new Sphere();

		initDL();

		x = map.getStartSquare()[0];
		y = map.getStartSquare()[1];
		goLeft();
	}

	public void reset() {
		x = map.getStartSquare()[0];
		y = map.getStartSquare()[1];
		setSuperMode(false);
		goLeft();
	}

	/**
	 * Turn super mode on/off
	 * 
	 * @param on
	 */
	public void setSuperMode(boolean on) {
		superMode = on;
		displayListID = on ? superPacmanDL : normalPacmanDL;
	}

	/**
	 * move pacman
	 */
	public void move() {

		changeDirection();

		moveForward();

		correctPosition();
	}

	/**
	 * Create display list
	 */
	private void initDL() {
		normalPacmanDL = GL11.glGenLists(1);

		GL11.glNewList(normalPacmanDL, GL11.GL_COMPILE);
		GL11.glPushMatrix();

		GL11.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		sphere.draw(0.45f, 24, 24);

		// eyes
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glTranslatef(-0.25f, 0.3f, 0.20f);
		sphere.draw(0.15f, 16, 16);

		GL11.glTranslatef(0.5f, 0.0f, 0.0f);
		sphere.draw(0.15f, 16, 16);

		GL11.glPopMatrix();
		GL11.glEndList();

		// Super pacman - different colors
		superPacmanDL = GL11.glGenLists(1);

		GL11.glPushMatrix();
		GL11.glNewList(superPacmanDL, GL11.GL_COMPILE);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		sphere.draw(0.45f, 24, 24);

		// eyes
		GL11.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
		GL11.glTranslatef(-0.25f, 0.3f, 0.20f);
		sphere.draw(0.15f, 16, 16);

		GL11.glTranslatef(0.5f, 0.0f, 0.0f);
		sphere.draw(0.15f, 16, 16);

		GL11.glPopMatrix();
		GL11.glEndList();

		displayListID = normalPacmanDL;

	}

	public boolean inSuperMode() {
		return superMode;
	}

}
