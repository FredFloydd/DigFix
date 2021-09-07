package DigFix;

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
import java.nio.FloatBuffer;

public class CubeRobot extends WorldObject{
	
    // Filenames for vertex and fragment shader source code
    private final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    private final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";
    
    // Components of the CubeRobot
	private final Cuboid body;
	private final Cuboid left_arm;
	private final Cuboid right_arm;
	private final Cuboid head;
	private final Cuboid left_leg;
	private final Cuboid right_leg;

	// Variables for animating walking
	public boolean walking;
	private final double max_arm_angle = 3 * Math.PI / 2;
	private final double max_leg_angle = 3 * Math.PI / 4;
	private final double angular_velocity = 2 * Math.PI / 1000;
	private final double arms_angle;
	private final double legs_angle;
	public long start_walking;
	private long time_walking;


	public CubeRobot(Vector3f position_, Vector3f orientation_) {

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
		position = position_;
		orientation = orientation_;
		up = new Vector3f(0f, 1f, 0f);

		// Initialise angles for animation
		arms_angle = 0;
		legs_angle = 0;
	}

	public void renderRobot(Camera camera, float deltaTime, long currentTime) {
		// If walking, animate body
		if (walking) {
			time_walking = currentTime - start_walking;
			float angle = (float) (deltaTime * max_arm_angle * Math.cos(angular_velocity * time_walking));
			right_arm.rotate(angle, new Vector3f(1f, 0f, 0f));
			left_arm.rotate(-angle, new Vector3f(1f, 0f, 0f));
			angle = (float) (deltaTime * max_leg_angle * Math.cos(angular_velocity * time_walking));
			right_leg.rotate(-angle, new Vector3f(1f, 0f, 0f));
			left_leg.rotate(angle, new Vector3f(1f, 0f, 0f));
		}
		else {
			right_arm.initialiseMovement();
			left_arm.initialiseMovement();
			right_leg.initialiseMovement();
			left_leg.initialiseMovement();
		}

		// Render body
		body.render(camera, getTransform());

		Matrix4f child_transform = getTransform().mul(body.movement_transform);

		// Render child body parts.
		right_arm.render(camera, child_transform);
		left_arm.render(camera, child_transform);
		head.render(camera, child_transform);
		right_leg.render(camera, child_transform);
		left_leg.render(camera, child_transform);
	}

	@Override
	public Matrix4f getTransform() {
		Vector3f world_position = new Vector3f(position.x, position.y, position.z);
		Vector3f world_orientation = new Vector3f(-orientation.x, orientation.y, -orientation.z);
		return new Matrix4f().translate(world_position).lookAlong(world_orientation, up);
	}
}
