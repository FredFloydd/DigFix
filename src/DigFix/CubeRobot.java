package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.lang.Math;

public class CubeRobot extends WorldObject{
	
    // Filenames for vertex and fragment shader source code
    private final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    private final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";
    
    // Components of the CubeRobot
	private final Cuboid torso;
	private final Cuboid leftArm;
	private final Cuboid rightArm;
	private final Cuboid head;
	private final Cuboid leftLeg;
	private final Cuboid rightLeg;

	// Dimensions of each body part
	private final Vector3f torsoDimensions = new Vector3f(0.5f, 0.75f, 0.25f);
	private final Vector3f armDimensions = new Vector3f(0.25f, 0.75f, 0.25f);
	private final Vector3f headDimensions = new Vector3f(0.5f, 0.5f, 0.5f);
	private final Vector3f legDimensions = new Vector3f(0.25f, 0.75f, 0.25f);
	public final float viewHeight = 1.75f;

	// Variables for animating walking
	public boolean walking;
	private final double MAX_ARM_ANGLE = 3 * Math.PI / 2;
	private final double MAX_LEG_ANGLE = 3 * Math.PI / 4;
	private final double ANGULAR_VELOCITY = 2 * Math.PI / 500f;
	public long startWalking;
	private long timeWalking;


	public CubeRobot(Vector3f position, Vector3f orientation) {

		// Create body parts
		torso = new Cuboid(torsoDimensions, new Vector3f(), new Vector3f(0f,
				legDimensions.y + 0.5f * torsoDimensions.y, 0f), new Matrix4f(),
				"resources/head.png");

		rightArm = new Cuboid(armDimensions, new Vector3f(0f, 0.5f * armDimensions.y, 0f),
				new Vector3f(-0.5f * (torsoDimensions.x + armDimensions.x),
				legDimensions.y + torsoDimensions.y, 0f), new Matrix4f(), "resources/arm.png");

		leftArm = new Cuboid(armDimensions, new Vector3f(0f, 0.5f * armDimensions.y , 0f),
				new Vector3f(0.5f * (torsoDimensions.x + armDimensions.x),
				legDimensions.y + torsoDimensions.y, 0f), new Matrix4f(), "resources/arm.png");

		head = new Cuboid(headDimensions, new Vector3f(0f, -0.5f * headDimensions.y, 0f),
				new Vector3f(0f, legDimensions.y + torsoDimensions.y, 0f),
				new Matrix4f(), "resources/head.png");

		rightLeg = new Cuboid(legDimensions, new Vector3f(0f, 0.5f * legDimensions.y, 0f),
				new Vector3f(-0.25f * torsoDimensions.x, legDimensions.y, 0f),
				new Matrix4f(), "resources/leg.png");

		leftLeg = new Cuboid(legDimensions, new Vector3f(0f, 0.5f * legDimensions.y, 0f),
				new Vector3f(0.25f * torsoDimensions.x, legDimensions.y, 0f),
				new Matrix4f(), "resources/leg.png");

		// Create variables for moving robot in 3D space
		this.position = position;
		this.orientation = orientation;
		this.up = new Vector3f(0f, 1f, 0f);
	}

	public void renderRobot(Camera camera, float deltaTime, long currentTime) {
		// If walking, animate body
		if (walking) {
			timeWalking = currentTime - startWalking;
			float angle = (float) (deltaTime * MAX_ARM_ANGLE * Math.cos(ANGULAR_VELOCITY * timeWalking));
			rightArm.rotate(angle, new Vector3f(1f, 0f, 0f));
			leftArm.rotate(-angle, new Vector3f(1f, 0f, 0f));
			angle = (float) (deltaTime * MAX_LEG_ANGLE * Math.cos(ANGULAR_VELOCITY * timeWalking));
			rightLeg.rotate(-angle, new Vector3f(1f, 0f, 0f));
			leftLeg.rotate(angle, new Vector3f(1f, 0f, 0f));
		}
		else {
			rightArm.initialiseMovement();
			leftArm.initialiseMovement();
			rightLeg.initialiseMovement();
			leftLeg.initialiseMovement();
		}

		// Render body
		torso.render(camera, getTransform());

		Matrix4f child_transform = getTransform().mul(torso.movementTransform);

		// Render child body parts.
		rightArm.render(camera, child_transform);
		leftArm.render(camera, child_transform);
		head.render(camera, child_transform);
		rightLeg.render(camera, child_transform);
		leftLeg.render(camera, child_transform);
	}

	@Override
	public Matrix4f getTransform() {
		Vector3f worldPosition = new Vector3f(position.x, position.y, position.z);
		Vector3f worldOrientation = new Vector3f(-orientation.x, orientation.y, -orientation.z);
		return new Matrix4f().translate(worldPosition).lookAlong(worldOrientation, up);
	}
}
