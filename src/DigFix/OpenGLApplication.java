package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.*;

import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.NULL;


// Class for an OpenGL Window with rendering loop and meshes to draw

public class OpenGLApplication {

	private static final float FOV_Y = (float) Math.toRadians(50);
	protected static int WIDTH = 1280, HEIGHT = 720;
	private Camera camera;
	private long window;
	
	private long currentTime;

	// Callbacks for input handling
	private GLFWCursorPosCallback cursorCB;
	private GLFWKeyCallback keyCB;
	private GLFWMouseButtonCallback mouseCB;

	// Chunk made up of dirt blocks
	private Chunk chunk;
	private Cuboid skybox;

	// Player and Robot for view control
	private Player player;
	private Robot robot;
	private int centreX;
	private int centreY;

	// Initialize OpenGL and the world
	public void initialize() throws Exception {

		if (glfwInit() != true)
			throw new RuntimeException("Unable to initialize the graphics runtime.");

		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		
		glfwWindowHint(GLFW_SAMPLES, 4); // Multi sample buffer for MSAA

		// Ensure that the right version of OpenGL is used (at least 3.2)
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // Use CORE OpenGL profile without depreciated functions
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // Make it forward compatible

		window = glfwCreateWindow(WIDTH, HEIGHT, "DigFix", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the application window.");

		GLFWVidMode mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (mode.width() - WIDTH) / 2, (mode.height() - HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		createCapabilities();

		// Enable v-sync
		glfwSwapInterval(1);

		// Cull back-faces of polygons
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		
		// Enable MSAA
		glEnable(GL_MULTISAMPLE);  

		// Do depth comparisons when rendering
		glEnable(GL_DEPTH_TEST);

		// Set up robot and centre coordinates for mouse tracking
		robot = new Robot();
		centreX = mode.width() / 2;
		centreY = mode.height() / 2;
		robot.mouseMove(centreX, centreY);

		// Create player
		player = new Player(new Vector3f(0f, 0f, 5f), new Vector3f(0f, 0f, -1f),
				((float) WIDTH / (float) HEIGHT), FOV_Y, new Vector3f(0f, 1f, 0f));

		// Create camera
		camera = player.camera;
		//camera = new Camera((float) WIDTH / (float) HEIGHT, FOV_Y, new Vector3f(0f, 1.75f, 40f), new Vector3f(0f, 0f, -1f), camera.up);

		// Setup input handlers
		initializeInputs();

		// This is where we are creating the meshes
		chunk = new Chunk(new Vector2i());
		skybox = new Cuboid(new Vector3f(1000), new Vector3f(), new Vector3f(), new Matrix4f(), "resources/skybox.png");

		currentTime = System.currentTimeMillis();
	}

	private void initializeInputs() {

		// Callback for: when dragging the mouse, rotate the camera
		robot.mouseMove(centreX, centreY);
		cursorCB = new GLFWCursorPosCallback() {

			public void invoke(long window, double mouseX, double mouseY) {
				float sensitivity = 0.005f;
				float angleX = (float) (mouseX - 640) * sensitivity;
				float angleY = (float) (mouseY - 330) * sensitivity;
				player.updatePlayerOrientation(angleX, angleY);
				robot.mouseMove(centreX, centreY);
			}
		};

		// Callback for keyboard controls: WASD to move
		keyCB = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_W && action == GLFW_PRESS) {
					player.walkingDirections.x = 1;
				} else if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
					player.walkingDirections.x = 0;
				} else if (key == GLFW_KEY_S && action == GLFW_PRESS) {
					player.walkingDirections.z = 1;
				} else if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
					player.walkingDirections.z = 0;
				} else if (key == GLFW_KEY_A && action == GLFW_PRESS) {
					player.walkingDirections.w = 1;
				} else if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
					player.walkingDirections.w = 0;
				} else if (key == GLFW_KEY_D && action == GLFW_PRESS) {
					player.walkingDirections.y = 1;
				} else if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
					player.walkingDirections.y = 0;
				} else if (key == GLFW_KEY_P && action == GLFW_PRESS) {
					stop();
				}
			}
		};

		// Callback for mouse controls: Left click to mine
		mouseCB = new GLFWMouseButtonCallback() {
			public void invoke(long window, int key, int action, int mods) {
				if (key == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS)
					player.breakBlock(chunk);
			}
		};

		GLFWFramebufferSizeCallback fbs_cb = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				glViewport( 0, 0, width, height );
				camera.setAspectRatio( width * 1.f/height );
			}
		};

		// Set callbacks on the window
		glfwSetCursorPosCallback(window, cursorCB);
		glfwSetKeyCallback(window, keyCB);
		glfwSetMouseButtonCallback(window, mouseCB);
		glfwSetFramebufferSizeCallback(window, fbs_cb);
	}

	// Run game
	public void run() throws Exception {

		initialize();

		while (glfwWindowShouldClose(window) != true) {
			render();
		}
	}
	
	// Render the world
	public void render() {

		// Clear the buffer
		glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // Set the background colour to white
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);	

		long newTime = System.currentTimeMillis();

		// Update player position
		float deltaTime = (newTime - currentTime) / 1000.f; // Time taken to render this frame in seconds (= 0 when the application is paused)
		player.updatePosition(deltaTime, currentTime);

		// Draw player and other world objects
		//player.body.renderRobot(camera, deltaTime, currentTime);
		chunk.renderChunk(camera, player.camera);
		glCullFace(GL_FRONT);
		skybox.render(camera);
		glCullFace(GL_BACK);

		currentTime = newTime;

		checkError();
		
		// Swap the draw and back buffers to display the rendered image
		glfwSwapBuffers(window);
		glfwPollEvents();
		checkError();
	}

	public void stop() {
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private void checkError() {
		int error = glGetError();
		if (error != GL_NO_ERROR)
			throw new RuntimeException("OpenGL produced an error (code " + error + ")");
	}
}
