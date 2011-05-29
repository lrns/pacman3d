import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

/**
 * Map (maze) of a game
 * 
 * Center of a maze is drawn at 0,0
 * 
 * @author Laurynas Sukys
 * 
 */
public class GameMap {

	// constants of possible map squares
	public static final int EMPTY = 0;
	public static final int WALL = 1;
	public static final int PACMAN = 2;
	public static final int GHOSTS = 3;
	public static final int FOOD = 4;
	public static final int POWERUP = 5;
	public static final int PORTAL = 6;

	public static final float SQUARE_WIDTH = 1.0f;

	// actual map
	private int[][] map = null;

	private int height;
	private int width;

	// pacman start point
	private int[] startSquare;

	// start point of ghosts
	private int[] ghostsPoint;

	private Sphere sphere;

	// offset to map edges from map center
	private float offsetX;
	private float offsetY;

	// display lists
	private int mapDL;
	private int foodDL;

	// count remaining items
	private int foodCount;
	private int powerupsCount;

	/**
	 * 
	 * @param filename
	 *            file to read map from
	 */
	public GameMap(String filename) {
		parseMap(filename);
	}

	/**
	 * Read map from file
	 * 
	 * @param filename
	 */
	public void parseMap(String filename) {
		try {

			FileInputStream fstream = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					lines.add(line);
				}
			}
			in.close();

			height = lines.size();
			width = lines.get(0).length();
			map = new int[height][width];

			// offset is a half of a dimension
			offsetX = -width / 2;
			offsetY = -height / 2;

			foodCount = 0;
			powerupsCount = 0;

			// detect map square type
			for (int i = 0; i < lines.size(); i++) {
				line = lines.get(i);
				int type = EMPTY;
				for (int j = 0; j < line.length(); j++) {
					switch (line.charAt(j)) {
					case '#':
						type = WALL;
						break;
					case '.':
						type = FOOD;
						foodCount++;
						break;
					case 'x':
						type = POWERUP;
						powerupsCount++;
						break;
					case 'P':
						type = PACMAN;
						startSquare = new int[] { j, height - i - 1 };
						break;
					case 'G':
						type = GHOSTS;
						ghostsPoint = new int[] { j, height - i - 1 };
						break;
					case ' ':
						type = EMPTY;
						break;
					default:
						type = EMPTY;
						break;
					}
					// reverse
					map[height - 1 - i][j] = type;
				}
			}

			startSquare[0] += offsetX;
			startSquare[1] += offsetY;
			ghostsPoint[0] += offsetX;
			ghostsPoint[1] += offsetY;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * init GL data
	 */
	public void initMap() {
		mapDL = GL11.glGenLists(1);
		GL11.glNewList(mapDL, GL11.GL_COMPILE);

		// floor
		GL11.glPushMatrix();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 2);

		GL11.glTranslatef(0.0f, 0.0f, -1.0f);

		Cuboid.plane(width * SQUARE_WIDTH, height * SQUARE_WIDTH, 6.0f);
		GL11.glPopMatrix();

		// walls

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 1);
		GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_DECAL);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (getSquare(x, y) == WALL) {
					// draw wall cube
					GL11.glPushMatrix();
					GL11.glTranslatef(x + offsetX, y + offsetY, 0.0f);
					Cuboid.cube(SQUARE_WIDTH);
					GL11.glPopMatrix();
				}
			}
		}
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glEndList();

		// food
		sphere = new Sphere();
		foodDL = GL11.glGenLists(1);

		createFoodDL();

	}

	/**
	 * Convert GL X to map square X coordinate
	 * 
	 * @param x
	 * @return
	 */
	public int coordToSquareX(float x) {
		return (int) Math.round(x - offsetX);
	}

	/**
	 * Convert GL Y to map square Y coordinate
	 * 
	 * @param y
	 * @return
	 */
	public int coordToSquareY(float y) {
		return (int) Math.round(y - offsetY);
	}

	/**
	 * Eat food at a given square
	 * 
	 * @param x
	 * @param y
	 * @return remaining count of food items
	 */
	public int eatFood(int x, int y) {
		if (map[y][x] == FOOD) {
			foodCount--;
			map[y][x] = EMPTY;
			createFoodDL();
		} else if (map[y][x] == POWERUP) {
			map[y][x] = EMPTY;
			powerupsCount--;

			// Recreate food display list
			createFoodDL();
		}

		return foodCount + powerupsCount;
	}

	/**
	 * Check if this coordinate is a portal to other side.
	 * 
	 * Square must be on the edge and the square on opposite side of map must be
	 * empty
	 * 
	 * @param x
	 *            GL X
	 * @param y
	 *            GL Y
	 * 
	 * @return new coordinates (after teleportation)
	 */
	public float[] checkForPortal(float x, float y) {
		int x2 = (int) Math.round(x - offsetX);
		int y2 = (int) Math.round(y - offsetY);

		if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height && map[y2][x2] != WALL) {
			// in bounds
			if (x2 == 0 || x2 == width - 1) {
				// on X edge
				int opposite = Math.abs(x2 - width + 1);
				if (map[y2][opposite] != WALL) {
					float[] square = { opposite + offsetX, y };
					return square;
				}
			} else if (y2 == 0 || y2 == height - 1) {
				// on Y edge
				int opposite = Math.abs(y2 - height + 1);
				if (map[opposite][x2] != WALL) {
					float[] square = { x, opposite + offsetY };
					return square;
				}
			}
		}

		return null;
	}

	/**
	 * Is a square under given GL coordinates empty
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isSquareEmpty(float x, float y) {

		int x2 = (int) Math.round(x - offsetX);
		int y2 = (int) Math.round(y - offsetY);

		// check map bounds

		if (x2 >= 0 && x2 < width && y2 >= 0 && y2 < height) {
			return map[y2][x2] != WALL;
		} else {
			return false;
		}
		// return true;
	}

	public int getSquare(int x, int y) {
		return map[y][x];
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	/**
	 * Start square of pacman
	 * 
	 * @return GL coordinates of pacman
	 */
	public int[] getStartSquare() {
		return startSquare;
	}

	/**
	 * Start square of ghosts
	 * 
	 * @return GL coordinates of a point
	 */
	public int[] getGhostsPoint() {
		return ghostsPoint;
	}

	/**
	 * Create display list with food and powerupds
	 */
	private void createFoodDL() {
		GL11.glNewList(foodDL, GL11.GL_COMPILE);

		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (getSquare(x, y) == FOOD) {
					GL11.glColor4f(1.0f, 1.0f, 0.5f, 1.0f);
					GL11.glPushMatrix();
					GL11.glTranslatef(x + offsetX, y + offsetY, 0.0f);
					sphere.draw(0.1f, 12, 12);
					GL11.glPopMatrix();

				} else if (getSquare(x, y) == POWERUP) {
					GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
					GL11.glPushMatrix();
					GL11.glTranslatef(x + offsetX, y + offsetY, 0.0f);
					sphere.draw(0.2f, 12, 12);
					GL11.glPopMatrix();
				}
			}
		}
		GL11.glEndList();
	}

	public void render() {
		GL11.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);

		GL11.glCallList(mapDL);
		GL11.glCallList(foodDL);
	}

	/**
	 * @return the foodCount
	 */
	public int getFoodCount() {
		return foodCount;
	}

	public int getPowerupsCount() {
		return powerupsCount;
	}

	/**
	 * @param foodCount
	 *            the foodCount to set
	 */
	public void setFoodCount(int foodCount) {
		this.foodCount = foodCount;
	}
}
