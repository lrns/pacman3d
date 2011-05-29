import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.PartialDisk;

/**
 * Class to show various screens: count down (loading), start screen, game over,
 * pause
 * 
 * @author Laurynas Sukys
 * 
 */
public class Screens {

	private Text text;
	private PartialDisk disk;
	// loading progress
	private float progress = 0.0f;

	// current level
	private int level;

	// current score
	private int score = 0;

	/**
	 * 
	 * @param text
	 *            text object used to print text on screen
	 */
	public Screens(Text text) {
		this.text = text;
		disk = new PartialDisk();
	}

	public void renderPauseScreen() {

		// draw pause symbol - two rectangles
		GL11.glPushMatrix();
		GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);

		GL11.glTranslatef(-1.5f, 0.0f, 0.0f);
		Cuboid.plane(2.0f, 5.0f, 1.0f);

		GL11.glTranslatef(3.0f, 0.0f, 0.0f);
		Cuboid.plane(2.0f, 5.0f, 1.0f);
		GL11.glPopMatrix();

		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		text.printString(-2.0f, -4.0f, "PRESS P TO CONTINUE");

	}

	/**
	 * Increase loading progress
	 */
	public void increaseCountdown() {
		progress += 1.0f;
	}

	/**
	 * Reset loading progress
	 * 
	 * @param level
	 *            game level
	 */
	public void resetCountdown(int level) {
		progress = 0.0f;
		this.level = level;
	}

	public boolean isCountdownComplete() {
		return progress > 400.0f;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getLevel() {
		return level;
	}

	public int getScore() {
		return score;
	}

	public void renderCountdownScreen() {

		// calculate loading color depending on progress
		// red -> yellow -> green
		GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		text.printString(-0.5f, 4.5f, "LEVEL " + level);
		if (score > 0) {
			text.printString(-0.5f, 4.0f, "SCORE " + score);
		}

		// Show loading progress
		if (progress > 359.0f) {
			GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		} else {
			float r = progress < 180.0f ? 1.0f : 1 - progress % 180 / 180.0f;
			float g = progress > 179.0f ? 1.0f : progress / 180.0f;
			GL11.glColor4f(r, g, 0.0f, 1.0f);
		}

		if (progress > 359.0f) {
			disk.draw(0.0f, 5.0f, 24, 24, 90.0f, 360.0f);
		} else {
			disk.draw(5.0f * (1 - (progress / 360.0f)), 5.0f, 24, 24, 90.0f, progress);
		}

		if (progress > 359.0f) {
			GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			text.printString(0.0f, -4.0f, "GO");
		} else {
			GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
			text.printString(-0.5f, -4.0f, "GET READY");
		}

	}

	public void renderStartScreen() {
		GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		text.printString(-10.0f, -4.0f, "CONTROLS");
		text.printString(-10.0f, -4.5f, "ARROWS TO MOVE");
		text.printString(-10.0f, -4.9f, "P TO PAUSE");
		text.printString(-10.0f, -5.3f, "F5 TO START AGAIN");
		text.printString(-10.0f, -5.7f, "F1 TO TOGGLE FULLSCREEN");
		text.printString(-10.0f, -6.1f, "ESC TO EXIT");

		GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		text.printString(-3.5f, 4.0f, "YET ANOTHER BORING PACMAN GAME");
		text.printString(-2.0f, -4.0f, "PRESS SPACE TO START");

		GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		disk.draw(0.0f, 4.0f, 24, 24, 130.0f, 280.0f);

	}

	public void renderGameoverScreen() {

		GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		text.printString(-0.2f, 0.0f, "GAMEOVER");
		text.printString(-0.4f, -1.0f, "SCORE " + score);
		text.printString(-3.0f, -4.0f, "PRESS F5 TO START AGAIN OR ESC TO QUIT");

	}

}
