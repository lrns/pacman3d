import org.lwjgl.opengl.GL11;

/**
 * Class to render some OpenGL object: cube, cuboid and plane
 * 
 * Adapted from lwjgl examples (de.ciardhubh.lwjgl.opengl.utility.Cube), added texture coordinates
 * 
 * @author Laurynas Sukys
 *
 */
public class Cuboid {
    private static final float[][] vertices = {
        {-0.5f, -0.5f, -0.5f}, // 0
        {0.5f, -0.5f, -0.5f},
        {0.5f, 0.5f, -0.5f},
        {-0.5f, 0.5f, -0.5f}, // 3
        {-0.5f, -0.5f, 0.5f}, // 4
        {0.5f, -0.5f, 0.5f},
        {0.5f, 0.5f, 0.5f},
        {-0.5f, 0.5f, 0.5f} // 7
    };
    private static final float[][] normals = {
        {0, 0, -1},
        {0, 0, 1},
        {0, -1, 0},
        {0, 1, 0},
        {-1, 0, 0},
        {1, 0, 0}
    };
    private static final byte[][] indices = {
        {0, 3, 2, 1},
        {4, 5, 6, 7},
        {0, 1, 5, 4},
        {3, 7, 6, 2},
        {0, 4, 7, 3},
        {1, 2, 6, 5}
    };
    
    
    /**
     * Render cube
     * @param size
     */
    public static void cube(float size) {
	    // Draw all six sides of the cube.
	    for (int i = 0; i < 6; i++) {
	        GL11.glBegin(GL11.GL_QUADS);
	        // Draw all four vertices of the current side.
	        for (int m = 0; m < 4; m++) {
	        	if (m == 0) {
	        		GL11.glTexCoord2f(0.0f, 0.0f); 
	        	} else if (m == 1) {
	        		GL11.glTexCoord2f(0.0f, 1.0f); 
	        	} else if (m == 2) {
	        		GL11.glTexCoord2f(1.0f, 1.0f); 
	        	} else {
	        		GL11.glTexCoord2f(1.0f, 0.0f);
	        	}
	            float[] temp = vertices[indices[i][m]];
	            GL11.glNormal3f(normals[i][0], normals[i][1], normals[i][2]);
	            GL11.glVertex3f(temp[0] * size, temp[1] * size, temp[2] * size);
	        }
	        GL11.glEnd();
	    }
    }
    
    /**
     * Render 2D plane  
     * @param width
     * @param height
     * @param textureCoord texture coordinate to use
     */
    
    public static void plane(float width, float height, float textureCoord) {
		 GL11.glBegin(GL11.GL_QUADS);
		 	GL11.glNormal3f( 1.0f, 1.0f, 0.0f);  
	        GL11.glTexCoord2f(0.0f, 0.0f); 
	        GL11.glVertex3f(-width/2, -height/2, 0.0f);
	        
	        GL11.glTexCoord2f(0.0f, textureCoord); 
	        GL11.glVertex3f(-width/2, height/2, 0.0f);
	        
	        GL11.glTexCoord2f(textureCoord, textureCoord); 
	        GL11.glVertex3f(width/2, height/2, 0.0f);
	        
	        GL11.glTexCoord2f(textureCoord, 0.0f);

	        GL11.glVertex3f(width/2, -height/2, 0.0f);
	        GL11.glEnd();
    }
    
    /**
     * Render cuboid with 3 given dimensions
     * @param width
     * @param height
     * @param depth
     */
	public static void cuboid(float width, float height, float depth) {

	    // Draw all six sides of the cuboid
		
	    for (int i = 0; i < 6; i++) {
	        GL11.glBegin(GL11.GL_QUADS);
	        // Draw all four vertices of the current side.
	        for (int m = 0; m < 4; m++) {
	        	if (m == 0) {
	        		GL11.glTexCoord2f(0.0f, 0.0f); 
	        	} else if (m == 1) {
	        		GL11.glTexCoord2f(0.0f, 1.0f); 
	        	} else if (m == 2) {
	        		GL11.glTexCoord2f(1.0f, 1.0f); 
	        	} else {
	        		GL11.glTexCoord2f(1.0f, 0.0f);
	        	}
	            float[] temp = vertices[indices[i][m]];
	            GL11.glNormal3f(normals[i][0], normals[i][1], normals[i][2]);
	            GL11.glVertex3f(temp[0] * width, temp[1] * height, temp[2] * depth);
	        }
	        GL11.glEnd();
	    }
	}
}
