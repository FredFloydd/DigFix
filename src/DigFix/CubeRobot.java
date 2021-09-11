package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.lang.Math;

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

	// Dimensions of each body part
	private final Vector3f body_dimensions = new Vector3f(0.5f, 0.75f, 0.25f);
	private final Vector3f arm_dimensions = new Vector3f(0.25f, 0.75f, 0.25f);
	private final Vector3f head_dimensions = new Vector3f(0.5f, 0.5f, 0.5f);
	private final Vector3f leg_dimensions = new Vector3f(0.25f, 0.75f, 0.25f);
	public final float viewHeight = 1.75f;

	// Variables for animating walking
	public boolean walking;
	private final double max_arm_angle = 3 * Math.PI / 2;
	private final double max_leg_angle = 3 * Math.PI / 4;
	private final double angular_velocity = 2 * Math.PI / 500f;
	public long start_walking;
	private long time_walking;


	public CubeRobot(Vector3f position, Vector3f orientation) {

		// Create body parts
		body = new Cuboid(body_dimensions, new Vector3f(), new Vector3f(0f, leg_dimensions.y + 0.5f * body_dimensions.y, 0f),
				new Matrix4f(), "resources/head.png");

		right_arm = new Cuboid(arm_dimensions, new Vector3f(0f, 0.5f * arm_dimensions.y, 0f), new Vector3f(-0.5f * (body_dimensions.x + arm_dimensions.x),
				leg_dimensions.y + body_dimensions.y, 0f), new Matrix4f(), "resources/arm.png");

		left_arm = new Cuboid(arm_dimensions, new Vector3f(0f, 0.5f * arm_dimensions.y , 0f), new Vector3f(0.5f * (body_dimensions.x + arm_dimensions.x),
				leg_dimensions.y + body_dimensions.y, 0f), new Matrix4f(), "resources/arm.png");

		head = new Cuboid(head_dimensions, new Vector3f(0f, -0.5f * head_dimensions.y, 0f),
				new Vector3f(0f, leg_dimensions.y + body_dimensions.y, 0f), new Matrix4f(), "resources/head.png");

		right_leg = new Cuboid(leg_dimensions, new Vector3f(0f, 0.5f * leg_dimensions.y, 0f),
				new Vector3f(-0.25f * body_dimensions.x, leg_dimensions.y, 0f), new Matrix4f(), "resources/leg.png");

		left_leg = new Cuboid(leg_dimensions, new Vector3f(0f, 0.5f * leg_dimensions.y, 0f),
				new Vector3f(0.25f * body_dimensions.x, leg_dimensions.y, 0f), new Matrix4f(), "resources/leg.png");

		// Create variables for moving robot in 3D space
		this.position = position;
		this.orientation = orientation;
		this.up = new Vector3f(0f, 1f, 0f);
	}

	public void renderRobot(Camera camera, float delta_time, long current_time) {
		// If walking, animate body
		if (walking) {
			time_walking = current_time - start_walking;
			float angle = (float) (delta_time * max_arm_angle * Math.cos(angular_velocity * time_walking));
			right_arm.rotate(angle, new Vector3f(1f, 0f, 0f));
			left_arm.rotate(-angle, new Vector3f(1f, 0f, 0f));
			angle = (float) (delta_time * max_leg_angle * Math.cos(angular_velocity * time_walking));
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
