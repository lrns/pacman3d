import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

/**
 * Class to represent OpenGL texture and load it from file
 * 
 * Some code adapted from lwjgl examples
 * 
 * @author Laurynas Sukys
 */
public class Texture {

	private int height;
	private int width;
	private ByteBuffer imageBuffer;

	/**
	 * Extract color values from image
	 * 
	 * @param image
	 */
	private void processImage(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();

		byte[][][] textureImage = new byte[height][width][4];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int pixel = image.getRGB(j, i);
				int alpha = (pixel >> 24) & 0xff;
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;

				textureImage[i][j][0] = (byte) red;
				textureImage[i][j][1] = (byte) green;
				textureImage[i][j][2] = (byte) blue;
				textureImage[i][j][3] = (byte) alpha;
			}
		}

		// Convert to one-dimensional buffer for LWJGL
		byte[] temp = new byte[height * width * 4];
		int tempIndex = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				temp[tempIndex] = textureImage[i][j][0];
				temp[tempIndex + 1] = textureImage[i][j][1];
				temp[tempIndex + 2] = textureImage[i][j][2];
				temp[tempIndex + 3] = textureImage[i][j][3];
				tempIndex += 4;
			}
		}

		imageBuffer = (ByteBuffer) BufferUtils.createByteBuffer(temp.length).put(temp).flip();
	}

	/**
	 * 
	 * @param filename
	 *            file to load texture from
	 */
	public Texture(String filename) {
		// read BufferedImage
		File f = new File(filename);
		BufferedImage image;
		try {
			image = ImageIO.read(f);
			processImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the imageBuffer
	 */
	public ByteBuffer getImageBuffer() {
		return imageBuffer;
	}

}
