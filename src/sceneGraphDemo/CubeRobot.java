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

public class CubeRobot extends Component{
	
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

	public CubeRobot() {

		// Create body node
		body = new Cuboid(1.6f, 2.4f, 0.8f, new Vector3f(0f, 0f, 0f),
				new Vector3f(0f, 2.4f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create right arm node
		right_arm = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1f, 0f),
				new Vector3f(-1.2f, 3.4f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create left arm node
		left_arm = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1f, 0f),
				new Vector3f(1.2f, 3.4f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create head node
		head = new Cuboid(1.6f, 1.6f, 1.6f, new Vector3f(0f, -0.8f, 0f),
				new Vector3f(0f, 3.6f, 0f), new Matrix4f(), "resources/cubemap_head.png");

		// Create right leg node
		right_leg = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1.2f, 0f),
				new Vector3f(-0.4f, 1.2f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create left leg node
		left_leg = new Cuboid(0.8f, 2.4f, 0.8f, new Vector3f(0f, 1.2f, 0f),
				new Vector3f(0.4f, 1.2f, 0f), new Matrix4f(), "resources/cubemap.png");

		// Create variables for moving robot in 3D space
		movement_transform = new Matrix4f();
		position = new Vector3f();
		orientation = new Matrix4f();
	}

	public void renderRobot(Camera camera, float deltaTime, long elapsedTime) {
		// Render body
		body.render(camera, movement_transform);

		Matrix4f child_transform = movement_transform.mul(body.movement_transform);

		// Render child body parts.
		right_arm.render(camera, child_transform);
		left_arm.render(camera, child_transform);
		head.render(camera, child_transform);
		right_leg.render(camera, child_transform);
		left_leg.render(camera, child_transform);
	}
}
