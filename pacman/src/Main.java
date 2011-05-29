import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;


/**
 * @author Laurynas Sukys (081365012)
 * 
 * Graphics Coursework
 * Pac-Man game
 * 
 * Some code adapted from Graphics module exercises 
 * 
 * *****************************
 * 
 * @author Mark Bernard
 * date:    16-Nov-2003
 *
 * Port of NeHe's Lesson 1 to LWJGL
 * Title: Setting Up An OpenGL Window
 * Uses version 0.8alpha of LWJGL http://www.lwjgl.org/
 *
 * Be sure that the LWJGL libraries are in your classpath
 *
 * Ported directly from the C++ version
 *
 * 2004-05-08: Updated to version 0.9alpha of LWJGL.
 *             Changed from all static to all instance objects.
 * 2004-09-21: Updated to version 0.92alpha of LWJGL.
 * 2008-10-01: Updated to version 2.0rc2 of LWJGL by John Shearer (jgds)
 * 2009-10-02: Updated to version 2.1 of LWJGL by John Shearer (jgds)
 */

public class Main {
	private final static int FPS_INTERVAL = 1000;
    private boolean done = false;
    private boolean fullscreen = false;
    private final String windowTitle = "Yet Another Boring Pac-Man Game";
    private boolean f1 = false;
    private int width;
    private int height;
    private int colorDepth;
    
    private DisplayMode displayMode;


    private Game game = null;
    private FPSCounterTask fpsCounter;
    
    // objecto to print text on screen
    private Text text;
    
    // is pause on
    private boolean pause = false;
    
    // countdown (loading level) stage
    private boolean countdown = false;
    // game over stage
    private boolean gameover = false;
    
    // is pause key down
    private boolean pause_key = false;
    
    // Object to display start, loading and other screens 
    private Screens screens;
    
    // default map
    private String mapFile = "map.txt";
    
    // time of last rendered frame, used to control FPS
    private Date lastFrame = new Date();
    
    /**
     * Everything starts and ends here. .
     * Takes 1 optional command line argument - file name of a custom map file
     * @param args command line arguments
     */
    public static void main(String args[]) {
        boolean fullscreen = false;
        int width = 1024;
        int height = 768;
        int colorDepth = 24;
        
        String map = null;
        if(args.length > 0) {
        	 map = args[0];            
        }
        Main game = new Main(map);
        game.run(fullscreen, width, height, colorDepth);
    }
    
	/**
	 * Initialise Pac-Man game
	 * 
	 * @param map
	 *            custom map file
	 */
    public Main(String map) {
		if (map != null) {
			mapFile = map;
		}
	}

    /**
     * Launch point
     * @param fullscreen boolean value, set to true to run in fullscreen mode
     */
    public void run(boolean fullscreen, int width, int height, int colorDepth) {
    	
        this.fullscreen = fullscreen;
        this.width = width;
        this.height = height;
        this.colorDepth = colorDepth;
        
  
        fpsCounter = new FPSCounterTask(FPS_INTERVAL);
        Timer timer = new Timer();
        // calculate FPS rate every FPS_INTERVAL ms
        timer.schedule(fpsCounter, 3000, FPS_INTERVAL);
        
        try {
            init();
            
            while (!done) {
                userInput();
                Date current = new Date();                
                // Control FPS, do not go over 60
                if (current.getTime() - lastFrame.getTime() > 15) {
                	lastFrame = current;
                	
                	if (countdown) {
                		// loading screen
                		if (screens.isCountdownComplete()) {
                			// loading completed, start level
                			startLevel(screens.getLevel(), screens.getScore());
                			countdown = false;
                		} else {
                			// continue loading
                			screens.increaseCountdown();
                		}
                	}
                	
                	if (!pause && game != null) {
                		// game is running
                		if (game.isFinished()) {
                			// game ended
                			if (game.getLives() == 0) {
                				// Game over
                				gameover = true;
                				screens.setScore(game.getScore());
                				game = null;
                			} else {
                				// Next level
                				screens.setScore(game.getScore());
                				startCountdown(game.getLevel()+1);
                				game = null;
                			}
                		} else {
                			// game in progress
                			game.update();
                		}
                	}
                	render();
                	Display.update();
                }
            }
            timer.cancel();
            cleanup();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    /**
     * Start loading particular level
     * @param level
     */
    private void startCountdown(int level) {
    	screens.resetCountdown(level);
    	countdown = true;
	}
    
	/**
	 * Start playing particular level
	 * 
	 * @param level
	 *            initial level
	 * @param score
	 *            initial score
	 */
	private void startLevel(int level, int score) {
		game = new Game(mapFile, level, score);
	}

    /**
     * All updating is done here.  Key and mouse polling as well as window closing and
     * custom updates, such as AI.
     */
    private void userInput() {
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {       // Exit if Escape is pressed
            done = true;
        }
        if(Display.isCloseRequested()) {                     // Exit if window is closed
            done = true;
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_F1) && !f1) {    // Is F1 Being Pressed?
            f1 = true;                                      // Tell Program F1 Is Being Held
            switchMode();                                   // Toggle Fullscreen / Windowed Mode
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_F1)) {          // Is F1 Being Pressed?
            f1 = false;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_F5) && (game != null || gameover)) {
        	// reset game, go to start screen
            pause = false;
            game = null;
            gameover = false;
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_P) && !pause_key && game != null) {
        	// pause on
            pause_key = true;
            pause = !pause;
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_P)) { 
        	// pause off
            pause_key = false;
        }
        
        if (!pause && game != null && !gameover) {
        	// game is running, check game controls
        	game.handleInput();
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && game == null && !countdown && !gameover) { 
        	// start game with space when start screen is active
        	startCountdown(1);
        }
    
    }

    private void switchMode() {
        fullscreen = !fullscreen;
        try {
            Display.setFullscreen(fullscreen);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
   

    /**
     * For rendering all objects to the screen
     * @return boolean for success or not
     */
	private boolean render() {
		fpsCounter.increase();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
																			
		GL11.glLoadIdentity(); 

		GL11.glTranslatef(0.0f, 0.0f, -30.0f);
		
		
		if (countdown) {
			// countdown screen, game level is loading
			screens.renderCountdownScreen();
			screens.increaseCountdown();
		} else if (gameover) {
			// game ended
			screens.renderGameoverScreen();
		} else if (!pause && game != null) {
			// playing actual game
			text.printString(8.5f, 7.9f, "FPS " + fpsCounter.getFPS());
			text.printString(8.5f, 7.3f, "LEVEL " + game.getLevel());
			text.printString(8.5f, 6.9f, "LIVES " + game.getLives());
			text.printString(8.5f, 6.5f, "SCORE " + game.getScore());
			game.render();
		} else if(pause) {
			// pause
			screens.renderPauseScreen();
		} else {
			// start screen
			screens.renderStartScreen();
		}

		return true;

	}


	/**
     * Create a window depending on whether fullscreen is selected
     * @throws Exception Throws the Window.create() exception up the stack.
     */
    private void createWindow() throws Exception {
        Display.setFullscreen(fullscreen);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        
        displayMode = null;
        for (int i = 0; i < d.length; i++) {
	       //System.out.println("Available mode: " + d[i].getWidth() + " " + d[i].getHeight() + " " + d[i].getBitsPerPixel());
           if (d[i].getWidth() == width
                && d[i].getHeight() == height
                && d[i].getBitsPerPixel() == colorDepth)
               // && (d[i].getBitsPerPixel() == 24 || d[i].getBitsPerPixel() == 32 )) // jgds
            {
                displayMode = d[i];
                break;
            }
        }
        if (displayMode == null) {
        	displayMode = d[0];
        }

        Display.setDisplayMode(displayMode);
        Display.setTitle(windowTitle);
        Display.create();
    }

   
    /**
     * Do all initilization code here.  Including Keyboard and OpenGL
     * @throws Exception Passes any exceptions up to the main loop to be handled
     */
    private void init() throws Exception {
        createWindow();
        
        // Text object to print text on screen
        text = new Text(10.0f);
        
        // Object to display various screens: start screen, game over, loading, etc.
        screens = new Screens(text);
        
        initGL();
        initTextures();
        
    }
    
 

    /**
     * Initialize OpenGL
     *
     */
    private void initGL() {
        
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        initLights();

        // grey background
        GL11.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
        
        
        GL11.glClearDepth(1.0); // Depth Buffer Setup
        
        GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

        GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
        GL11.glLoadIdentity(); // Reset The Projection Matrix

        // Calculate The Aspect Ratio Of The Window
        GLU.gluPerspective(
          45.0f,
          (float) displayMode.getWidth() / (float) displayMode.getHeight(),
          0.1f,
          100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

        // Really Nice Perspective Calculations
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

    }
    
    /**
     * Load textures
     */
    private void initTextures() {
    	IntBuffer textures = BufferUtils.createIntBuffer(2);   
        GL11.glGenTextures(textures);
        
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // bricks texture for walls
        Texture bricksTexture = new Texture("images/bricks1.png");
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(0));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, bricksTexture.getWidth(), bricksTexture.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bricksTexture.getImageBuffer());

        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_DECAL);
        
        // Texture for floor
        Texture grassTexture = new Texture("images/floor2.jpg");
        
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(1));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, grassTexture.getWidth(), grassTexture.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, grassTexture.getImageBuffer());

        GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_DECAL);
        
    }
    
    /**
     * Initialise lights
     */
    private void initLights() {
    	float[] ambient = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] position = {0.0f, 3.0f, 2.0f, 0.0f};
        float[] lmodel_ambient = {0.4f, 0.4f, 0.4f, 1.0f};
        float local_view = 0.0f;

        FloatBuffer ambientBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(ambient.length).put(
                ambient).flip();
        FloatBuffer diffuseBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(diffuse.length).put(
                diffuse).flip();
        FloatBuffer positionBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(position.length).put(
                position).flip();
        FloatBuffer lmodel_ambientBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(
                lmodel_ambient.length).put(lmodel_ambient).flip();
        
        float[] mat_specular = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] diffuseMaterial = {1.0f, 1.0f, 0.0f, 1.0f};        
        
        FloatBuffer mat_specularBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(
                mat_specular.length).put(mat_specular).flip();
        FloatBuffer diffuseMaterialBuffer = (FloatBuffer) BufferUtils.createFloatBuffer(
                diffuseMaterial.length).put(diffuseMaterial).flip();      
        
        
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, diffuseMaterialBuffer);
        GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, mat_specularBuffer);
        GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 100.0f);
        
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, ambientBuffer);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, diffuseBuffer);
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, positionBuffer);
        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, lmodel_ambientBuffer);
        GL11.glLightModelf(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view);  
        
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        
        GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        

    }

    /**
     * Cleanup all the resources.
     *
     */
    private void cleanup() {
        Display.destroy();
    }

}

/**
 * FPS counter
 *
 */
class FPSCounterTask extends TimerTask {
	
	// number of frames during period
	private int count = 0;
	
	// length of period in seconds
	private int period;
	
	// latest FPS number
	private int fps;

	public FPSCounterTask(int p) {
		period = p/1000;
		
	}
	public void increase() {
		count++;
	}
	public void setCount(int c) {
		count = c;
	}
	
	public int getFPS() {
		return fps;
	}

	public void run() {
		// executed by timer, calculates FPS rate
		fps = count / period;		
		count = 0;
	}
}
