import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * Class to draw some text on screen using bitmap font
 * 
 * Some code adapted from lwjgl examples 8-2 
 * 
 * @author Laurynas Sukys
 *
 */
public class Text {

	// z coordinate for the text
	private float zOffset = 0.0f;
	
	
	private byte[] space = {
	        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
	    };
	private ByteBuffer spaceBuffer = (ByteBuffer) BufferUtils.createByteBuffer(space.length).put(space).flip();
	private ByteBuffer[] letters = {
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xff, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0x66, (byte) 0x3c, (byte) 0x18}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3, (byte) 0xc3, (byte) 0xc7, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3, (byte) 0xc3, (byte) 0xc7, (byte) 0xfe}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x7e, (byte) 0xe7, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xe7, (byte) 0x7e}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xfc, (byte) 0xce, (byte) 0xc7, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc7, (byte) 0xce, (byte) 0xfc}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xfc, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xff}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xfc, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xff}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3, (byte) 0xcf, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xe7, (byte) 0x7e}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xff, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x7e, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x7e}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x7c, (byte) 0xee, (byte) 0xc6, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc3, (byte) 0xc6, (byte) 0xcc, (byte) 0xd8, (byte) 0xf0, (byte) 0xe0, (byte) 0xf0, (byte) 0xd8, (byte) 0xcc, (byte) 0xc6, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xdb, (byte) 0xff, (byte) 0xff, (byte) 0xe7, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc7, (byte) 0xc7, (byte) 0xcf, (byte) 0xcf, (byte) 0xdf, (byte) 0xdb, (byte) 0xfb, (byte) 0xf3, (byte) 0xf3, (byte) 0xe3, (byte) 0xe3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xe7, (byte) 0x7e}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xc0, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3, (byte) 0xc3, (byte) 0xc7, (byte) 0xfe}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x3f, (byte) 0x6e, (byte) 0xdf, (byte) 0xdb, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0x66, (byte) 0x3c}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc3, (byte) 0xc6, (byte) 0xcc, (byte) 0xd8, (byte) 0xf0, (byte) 0xfe, (byte) 0xc7, (byte) 0xc3, (byte) 0xc3, (byte) 0xc7, (byte) 0xfe}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x7e, (byte) 0xe7, (byte) 0x03, (byte) 0x03, (byte) 0x07, (byte) 0x7e, (byte) 0xe0, (byte) 0xc0, (byte) 0xc0, (byte) 0xe7, (byte) 0x7e}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0xff}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x7e, (byte) 0xe7, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x3c, (byte) 0x3c, (byte) 0x66, (byte) 0x66, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc3, (byte) 0xe7, (byte) 0xff, (byte) 0xff, (byte) 0xdb, (byte) 0xdb, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xc3, (byte) 0x66, (byte) 0x66, (byte) 0x3c, (byte) 0x3c, (byte) 0x18, (byte) 0x3c, (byte) 0x3c, (byte) 0x66, (byte) 0x66, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x3c, (byte) 0x3c, (byte) 0x66, (byte) 0x66, (byte) 0xc3}).flip(),
	        (ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0xff, (byte) 0xc0, (byte) 0xc0, (byte) 0x60, (byte) 0x30, (byte) 0x7e, (byte) 0x0c, (byte) 0x06, (byte) 0x03, (byte) 0x03, (byte) 0xff}).flip()
	    };
	    
    private ByteBuffer[] numbers = {
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x3c, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x3c}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x3c, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x18, (byte) 0x78, (byte) 0x38, (byte) 0x18}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x7e, (byte) 0x60, (byte) 0x60, (byte) 0x60, (byte) 0x60, (byte) 0x3c, (byte) 0x06, (byte) 0x06, (byte) 0x66, (byte) 0x66, (byte) 0x3c}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x3c, (byte) 0x66, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x1c, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x66, (byte) 0x3c}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x7f, (byte) 0x66, (byte) 0x36, (byte) 0x1e, (byte) 0x0e, (byte) 0x06}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x3c, (byte) 0x66, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x7c, (byte) 0x60, (byte) 0x60, (byte) 0x60, (byte) 0x60, (byte) 0x7e}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x3c, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x7c, (byte) 0x60, (byte) 0x60, (byte) 0x66, (byte) 0x3c}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x0c, (byte) 0x0c, (byte) 0x0c, (byte) 0x0c, (byte) 0x0c, (byte) 0x1f, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x7e}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x3c, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x3c, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x3c}).flip(),
	    		(ByteBuffer) BufferUtils.createByteBuffer(13).put(new byte[]{0x00, (byte) 0x00, (byte) 0x3c, (byte) 0x66, (byte) 0x06, (byte) 0x06, (byte) 0x06, (byte) 0x3e, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x66, (byte) 0x3c}).flip()
	            };
	private int fontOffset;


	/**
	 * Build font
	 */
	private void makeRasterFont() {
		int i, j;
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		fontOffset = GL11.glGenLists(128);
		for (i = 0, j = 'A'; i < 26; i++, j++) {
			GL11.glNewList(fontOffset + j, GL11.GL_COMPILE);
			GL11.glBitmap(8, 13, 0.0f, 2.0f, 10.0f, 0.0f, letters[i]);
			GL11.glEndList();
		}
		for (i = 0, j = '0'; i < 10; i++, j++) {
			GL11.glNewList(fontOffset + j, GL11.GL_COMPILE);
			GL11.glBitmap(8, 13, 0.0f, 2.0f, 10.0f, 0.0f, numbers[i]);
			GL11.glEndList();
		}
		GL11.glNewList(fontOffset + ' ', GL11.GL_COMPILE);
		GL11.glBitmap(8, 13, 0.0f, 2.0f, 10.0f, 0.0f, spaceBuffer);
		GL11.glEndList();
	}

	/**
	 * Constructor
	 * 
	 * @param zOffset
	 *            Z coordinate for text objects
	 */
	public Text(float zOffset) {
		this.zOffset = zOffset;
		makeRasterFont();

	}

	/**
	 * Print string on screen
	 * 
	 * @param x
	 * @param y
	 * @param s
	 *            String to print
	 */
	public void printString(float x, float y, String s) {
		GL11.glRasterPos3f(x, y, zOffset);
		GL11.glPushAttrib(GL11.GL_LIST_BIT);
		GL11.glListBase(fontOffset);
		GL11.glCallLists((ByteBuffer) BufferUtils.createByteBuffer(s.length()).put(s.getBytes())
				.flip());
		GL11.glPopAttrib();
	}



}
