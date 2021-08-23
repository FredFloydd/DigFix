package sceneGraphDemo;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

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
    
    // Component 1 : Body
	private Mesh body_mesh;				// Mesh of the body
	private ShaderProgram body_shader;	// Shader to colour the body mesh
	private Texture body_texture;		// Texture image to be used by the body shader
	private Matrix4f body_transform;    // Transformation matrix of the body object
	private Vector3f body_position;     // Position of the body

	// Component 2: Right Arm
	private Mesh right_arm_mesh;             // Mesh of the right arm
	private ShaderProgram right_arm_shader;  // Shader to colour the right arm mesh
	private Texture right_arm_texture;       // Texture image to be used by the right arm shader
	private Matrix4f right_arm_transform;    // Transformation matrix of the right arm object

	// Component 3: Left Arm
	private Mesh left_arm_mesh;             // Mesh of the left arm
	private ShaderProgram left_arm_shader;  // Shader to colour the left arm mesh
	private Texture left_arm_texture;       // Texture image to be used by the left arm shader
	private Matrix4f left_arm_transform;    // Transformation matrix of the left arm object

	// Component 3: Head
	private Mesh head_mesh;             // Mesh of the head
	private ShaderProgram head_shader;  // Shader to colour the head mesh
	private Texture head_texture;       // Texture image to be used by the head shader
	private Matrix4f head_transform;    // Transformation matrix of the head object

	// Component 4: Right Leg
	private Mesh right_leg_mesh;             // Mesh of the right leg
	private ShaderProgram right_leg_shader;  // Shader to colour the right leg mesh
	private Texture right_leg_texture;       // Texture image to be used by the right leg shader
	private Matrix4f right_leg_transform;    // Transformation matrix of the right leg object

	// Component 5: Left Leg
	private Mesh left_leg_mesh;             // Mesh of the left leg
	private ShaderProgram left_leg_shader;  // Shader to colour the left leg mesh
	private Texture left_leg_texture;       // Texture image to be used by the left leg shader
	private Matrix4f left_leg_transform;    // Transformation matrix of the left leg object


	/**
 *  Constructor
 *  Initialize all the CubeRobot components
 */
	public CubeRobot() {
		// Create body node
		
		// Initialise Geometry
		body_mesh = new CubeMesh(); 
		
		// Initialise Shader
		body_shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		body_shader.bindDataToShader("oc_position", body_mesh.vertex_handle, 3);
		// Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
		body_shader.bindDataToShader("oc_normal", body_mesh.normal_handle, 3);
		// Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
		body_shader.bindDataToShader("texcoord", body_mesh.tex_handle, 2);
		
		// Initialise Texturing
		body_texture = new Texture(); 
		body_texture.load("resources/cubemap.png");
		
		// Build Transformation Matrix
		body_transform = new Matrix4f(); 
		
		// Scale the body transformation matrix
		body_transform.scale(1.0f, 2.0f, 1.0f);

		// Initialise body position
		body_position = new Vector3f();

		// Create right arm node

		// Initialise Geometry
		right_arm_mesh = new CubeMesh();

		// Initialise Shader
		right_arm_shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		right_arm_shader.bindDataToShader("oc_position", right_arm_mesh.vertex_handle, 3);
		// Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
		right_arm_shader.bindDataToShader("oc_normal", right_arm_mesh.normal_handle, 3);
		// Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
		right_arm_shader.bindDataToShader("texcoord", right_arm_mesh.tex_handle, 2);

		// Initialise Texturing
		right_arm_texture = new Texture();
		right_arm_texture.load("resources/cubemap.png");

		// Build Transformation Matrix
		right_arm_transform = new Matrix4f();

		// Scale and rotate the right arm transformation matrix
		right_arm_transform.translate(-1f, 2f, 0f);
		right_arm_transform.rotate(-0.3f, new Vector3f(0f,0f,1f));
		right_arm_transform.scale(0.25f, 1.5f, 0.25f);
		right_arm_transform.translate(0f, -1f, 0f);

		// Create left arm node

		// Initialise Geometry
		left_arm_mesh = new CubeMesh();

		// Initialise Shader
		left_arm_shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		left_arm_shader.bindDataToShader("oc_position", left_arm_mesh.vertex_handle, 3);
		// Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
		left_arm_shader.bindDataToShader("oc_normal", left_arm_mesh.normal_handle, 3);
		// Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
		left_arm_shader.bindDataToShader("texcoord", left_arm_mesh.tex_handle, 2);

		// Initialise Texturing
		left_arm_texture = new Texture();
		left_arm_texture.load("resources/cubemap.png");

		// Build Transformation Matrix
		left_arm_transform = new Matrix4f();

		// Scale and rotate the right arm transformation matrix
		left_arm_transform.translate(1f, 2f, 0f);
		left_arm_transform.rotate(0.3f, new Vector3f(0f,0f,1f));
		left_arm_transform.scale(0.25f, 1.5f, 0.25f);
		left_arm_transform.translate(0f, -1f, 0f);

		// Create head node

		// Initialise Geometry
		head_mesh = new CubeMesh();

		// Initialise Shader
		head_shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		head_shader.bindDataToShader("oc_position", head_mesh.vertex_handle, 3);
		// Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
		head_shader.bindDataToShader("oc_normal", head_mesh.normal_handle, 3);
		// Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
		head_shader.bindDataToShader("texcoord", head_mesh.tex_handle, 2);

		// Initialise Texturing
		head_texture = new Texture();
		head_texture.load("resources/cubemap_head.png");

		// Build Transformation Matrix
		head_transform = new Matrix4f();

		// Scale and rotate the right arm transformation matrix
		head_transform.translate(0f, 2f, 0f);
		head_transform.scale(0.5f, 0.5f, 0.5f);
		head_transform.translate(0f, 1f, 0f);

		// Create right leg node

		// Initialise Geometry
		right_leg_mesh = new CubeMesh();

		// Initialise Shader
		right_leg_shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		right_leg_shader.bindDataToShader("oc_position", right_leg_mesh.vertex_handle, 3);
		// Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
		right_leg_shader.bindDataToShader("oc_normal", right_leg_mesh.normal_handle, 3);
		// Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
		right_leg_shader.bindDataToShader("texcoord", right_leg_mesh.tex_handle, 2);

		// Initialise Texturing
		right_leg_texture = new Texture();
		right_leg_texture.load("resources/cubemap.png");

		// Build Transformation Matrix
		right_leg_transform = new Matrix4f();

		// Scale and rotate the right arm transformation matrix
		right_leg_transform.translate(-0.7f, -2f, 0f);
		right_leg_transform.scale(0.25f, 1f, 0.25f);
		right_leg_transform.translate(0f, -1f, 0f);

		// Create left leg node

		// Initialise Geometry
		left_leg_mesh = new CubeMesh();

		// Initialise Shader
		left_leg_shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
		// Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
		// The prefix "oc_" means object coordinates
		left_leg_shader.bindDataToShader("oc_position", left_leg_mesh.vertex_handle, 3);
		// Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
		left_leg_shader.bindDataToShader("oc_normal", left_leg_mesh.normal_handle, 3);
		// Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
		left_leg_shader.bindDataToShader("texcoord", left_leg_mesh.tex_handle, 2);

		// Initialise Texturing
		left_leg_texture = new Texture();
		left_leg_texture.load("resources/cubemap.png");

		// Build Transformation Matrix
		left_leg_transform = new Matrix4f();

		// Scale and rotate the right arm transformation matrix
		left_leg_transform.translate(0.7f, -2f, 0f);
		left_leg_transform.scale(0.25f, 1f, 0.25f);
		left_leg_transform.translate(0f, -1f, 0f);
	}
	

	/**
	 * Updates the scene and then renders the CubeRobot
	 * @param camera - Camera to be used for rendering
	 * @param deltaTime		- Time taken to render this frame in seconds (= 0 when the application is paused)
	 * @param elapsedTime	- Time elapsed since the beginning of this program in millisecs
	 */
	public void render(Camera camera, float deltaTime, long elapsedTime) {
		
		// Animate Body. Translate the body as a function of time
		float walk_speed = 0f;
		float body_rotate_speed = (float) (-1.5f * Math.cos(0.005f * elapsedTime));
		Vector3f moved = new Vector3f(0f, 0f, -1f).mul(walk_speed * deltaTime);
		Matrix4f body_movement = new Matrix4f().translate(moved);
		Matrix4f body_rotation = new Matrix4f().translate(body_position).rotate(body_rotate_speed * deltaTime, new Vector3f(0f, 1f, 0f)).translate(body_position.mul(-1));
		body_transform = body_transform.mulLocal(body_movement.mul(body_rotation));
		body_position = body_position.mul(-1).add(moved);
		
		// Animate Arm. Rotate the right arm around its end as a function of time
		float right_arm_rotate_speed = (float) (-1.5f * Math.cos(0.005f * elapsedTime));
		Matrix4f right_arm_rotation = new Matrix4f().translate(-1f, 2f, 0f).rotate(right_arm_rotate_speed * deltaTime, new Vector3f(0f, 0f, 1f)).translate(1f, -2f, 0f);
		right_arm_transform = right_arm_transform.mulLocal(right_arm_rotation);

		// Animate Arm. Rotate the left arm around its end as a function of time
		float left_arm_rotate_speed = (float) (1.5f * Math.cos(0.005f * elapsedTime));
		Matrix4f left_arm_rotation = new Matrix4f().translate(1f, 2f, 0f).rotate(left_arm_rotate_speed * deltaTime, new Vector3f(0f, 0f, 1f)).translate(-1f, -2f, 0f);
		left_arm_transform = left_arm_transform.mulLocal(left_arm_rotation);

		renderMesh(camera, body_mesh, body_transform, body_shader, body_texture);
		
		// Chain transformation matrices of the arm and body (Scene Graph)
		right_arm_transform = right_arm_transform.mulLocal(body_movement);
		left_arm_transform = left_arm_transform.mulLocal(body_movement);
		head_transform = head_transform.mulLocal(body_movement);
		right_leg_transform = right_leg_transform.mulLocal(body_movement);
		left_leg_transform = left_leg_transform.mulLocal(body_movement);

		// Render child body parts.
		renderMesh(camera, right_arm_mesh, right_arm_transform, right_arm_shader, right_arm_texture);
		renderMesh(camera, left_arm_mesh, left_arm_transform, left_arm_shader, left_arm_texture);
		renderMesh(camera, head_mesh, head_transform, head_shader, head_texture);
		renderMesh(camera, right_leg_mesh, right_leg_transform, right_leg_shader, right_leg_texture);
		renderMesh(camera, left_leg_mesh, left_leg_transform, left_leg_shader, left_leg_texture);
	}
	
	/**
	 * Draw mesh from a camera perspective
	 * @param camera		- Camera to be used for rendering
	 * @param mesh			- mesh to render
	 * @param modelMatrix	- model transformation matrix of this mesh
	 * @param shader		- shader to colour this mesh
	 * @param texture		- texture image to be used by the shader
	 */
	public void renderMesh(Camera camera, Mesh mesh , Matrix4f modelMatrix, ShaderProgram shader, Texture texture) {
		// If shaders modified on disk, reload them
		shader.reloadIfNeeded(); 
		shader.useProgram();

		// Step 2: Pass relevant data to the vertex shader
		
		// compute and upload MVP
		Matrix4f mvp_matrix = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(modelMatrix);
		shader.uploadMatrix4f(mvp_matrix, "mvp_matrix");
		
		// Upload Model Matrix and Camera Location to the shader for Phong Illumination
		shader.uploadMatrix4f(modelMatrix, "m_matrix");
		shader.uploadVector3f(camera.getCameraPosition(), "wc_camera_position");
		
		// Transformation by a nonorthogonal matrix does not preserve angles
		// Thus we need a separate transformation matrix for normals
		Matrix3f normal_matrix = new Matrix3f();
		// Calculate normal transformation matrix
		normal_matrix = new Matrix3f(modelMatrix).invert().transpose().mul(normal_matrix);

		shader.uploadMatrix3f(normal_matrix, "normal_matrix");
		
		// Step 3: Draw our VertexArray as triangles
		// Bind Texture
		texture.bindTexture();
		// draw
		glBindVertexArray(mesh.vertexArrayObj); // Bind the existing VertexArray object
		glDrawElements(GL_TRIANGLES, mesh.no_of_triangles, GL_UNSIGNED_INT, 0); // Draw it as triangles
		glBindVertexArray(0);             // Remove the binding
		
        // Unbind texture
		texture.unBindTexture();
	}
}
