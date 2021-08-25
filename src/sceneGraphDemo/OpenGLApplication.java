package sceneGraphDemo;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;


/***
 * Class for an OpenGL Window with rendering loop and meshes to draw
 *
 */
public class OpenGLApplication {

	private static final float FOV_Y = (float) Math.toRadians(50);
	protected static int WIDTH = 800, HEIGHT = 600;
	private Camera camera;
	private long window;
	
	private long currentTime;
	private long startTime;
	private long elapsedTime;
	private boolean pause = false;

	// Callbacks for input handling
	private GLFWCursorPosCallback cursor_cb;
	private GLFWScrollCallback scroll_cb;
	private GLFWKeyCallback key_cb;

	// Robots as part of scene
	private CubeRobot cubeRobot;
	private CubeRobot cubeRobot2;
	private CubeRobot cubeRobot3;

	// Player
	private Player player;

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

		CubeRobot player_robot = new CubeRobot();
		player_robot.rotate((float) Math.PI, new Vector3f(0f, 1f, 0f));
		player = new Player(player_robot, new Vector3f(0f, 0f, 10f), new Vector3f(0f, 0f, -1f),
				WIDTH / HEIGHT, FOV_Y, new Vector3f(0f, 1f, 0f));

		// Create camera, and setup input handlers
		camera = player.camera;
		initializeInputs();

		// This is where we are creating the meshes
		cubeRobot = new CubeRobot();
		cubeRobot.move(new Vector3f(-5f, 0f, -10f));
		cubeRobot2 = new CubeRobot();
		cubeRobot2.move(new Vector3f(5f, 0f, -10f));
		cubeRobot3 = new CubeRobot();
		cubeRobot3.move(new Vector3f(0f, 0f, -10f));

		startTime = System.currentTimeMillis();
		currentTime = System.currentTimeMillis();
	}

	private void initializeInputs() {

		// Callback for: when dragging the mouse, rotate the camera
		cursor_cb = new GLFWCursorPosCallback() {
			private double prevMouseX, prevMouseY;

			public void invoke(long window, double mouseX, double mouseY) {
				boolean dragging = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
				if (dragging) {
					//camera.rotate(mouseX - prevMouseX, mouseY - prevMouseY);
				}
				prevMouseX = mouseX;
				prevMouseY = mouseY;
			}
		};

		// Callback for keyboard controls: WASD to move
		key_cb = new GLFWKeyCallback() {
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_W && action == GLFW_PRESS) {
					player.walking_directions.x = 1;
				} else if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
					player.walking_directions.x = 0;
				} else if (key == GLFW_KEY_S && action == GLFW_PRESS) {
					player.walking_directions.z = 1;
				} else if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
					player.walking_directions.z = 0;
				} else if (key == GLFW_KEY_A && action == GLFW_PRESS) {
					player.walking_directions.w = 1;
				} else if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
					player.walking_directions.w = 0;
				} else if (key == GLFW_KEY_D && action == GLFW_PRESS) {
					player.walking_directions.y = 1;
				} else if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
					player.walking_directions.y = 0;
				}
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
		glfwSetCursorPosCallback(window, cursor_cb);
		glfwSetScrollCallback(window, scroll_cb);
		glfwSetKeyCallback(window, key_cb);
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

		elapsedTime += newTime - currentTime; // Time elapsed since the beginning of this program in milliseconds

		// Update player position
		float deltaTime = (newTime - currentTime) / 1000.f; // Time taken to render this frame in seconds (= 0 when the application is paused)
		player.updatePosition(deltaTime);

		// Draw player and other world entities
		cubeRobot.renderRobot(camera, deltaTime, elapsedTime);
		cubeRobot2.renderRobot(camera, deltaTime, elapsedTime);
		cubeRobot3.renderRobot(camera, deltaTime, elapsedTime);
		player.body.renderRobot(camera, deltaTime, elapsedTime);
		
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
