package sceneGraphDemo;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.CallbackI;

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.lang.Math;
import java.nio.FloatBuffer;;

public class CubeRobot {
	
    // Filenames for vertex and fragment shader source code
    private final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    private final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";
    
    // Components of this CubeRobot

	private Cuboid body;
	private Cuboid left_arm;
	private Cuboid right_arm;
	private Cuboid head;
	private Cuboid left_leg;
	private Cuboid right_leg;

	/**
 *  Constructor
 *  Initialize all the CubeRobot components
 */
	public CubeRobot() {

		// Create body node
		body = new Cuboid(1.6f, 2.4f, 0.8f, new Vector3f(0f, 0f, 0f),
				new Vector3f(0f, 0f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create right arm node
		right_arm = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1f, 0f),
				new Vector3f(-1.2f, 1f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create left arm node
		left_arm = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1f, 0f),
				new Vector3f(1.2f, 1f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create head node
		head = new Cuboid(1.6f, 1.6f, 1.6f, new Vector3f(0f, -0.8f, 0f),
				new Vector3f(0f, 1.2f, 0f), new Matrix4f(), "resources/cubemap_head.png");

		// Create right leg node
		right_leg = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1.2f, 0f),
				new Vector3f(-0.4f, -1.2f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create left leg node
		left_leg = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1.2f, 0f),
				new Vector3f(0.4f, -1.2f, 0f), new Matrix4f(), "resources/cubemap.png");
	}

	/**
	 * Updates the scene and then renders the CubeRobot
	 * @param camera - Camera to be used for rendering
	 * @param deltaTime		- Time taken to render this frame in seconds (= 0 when the application is paused)
	 * @param elapsedTime	- Time elapsed since the beginning of this program in millisecs
	 */
	public void render(Camera camera, float deltaTime, long elapsedTime) {

		// Animate Body. Translate the body as a function of time
		float walk_Speed = -0.5f;
		float rotate_Speed = -0.5f;
		Vector3f move_Vector = new Vector3f(0f, 0f, 1f).mul(walk_Speed * deltaTime);
		body.move(move_Vector);
		body.rotate(rotate_Speed * deltaTime, new Vector3f(0f, 1f, 0f));

		// Animate Arm. Rotate the right arm around its end as a function of time
		right_arm.rotate((float) 0.5 * deltaTime, new Vector3f(1f, 0f, 0f));

		// Animate Arm. Rotate the left arm around its end as a function of time


		// Animate Leg. Rotate the right leg around its end as a function of time


		// Animate Leg. Rotate the left leg around its end as a function of time

		// Render body
		body.render(camera);

		// Render child body parts.
		right_arm.render(camera, body.movement_transform);
		left_arm.render(camera, body.movement_transform);
		head.render(camera, body.movement_transform);
		right_leg.render(camera, body.movement_transform);
		left_leg.render(camera, body.movement_transform);
	}
}
