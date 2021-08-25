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

public class Cuboid extends Component{

    // Side lengths
    private float x;
    private float y;
    private float z;

    // Location of origin within cuboid
    private Vector3f origin;

    public Cuboid(float x_, float y_, float z_, Vector3f origin_,  Vector3f initial_position,
                  Matrix4f initial_orientation, String texture_filename) {

        // Initialise basic parameters
        x = x_;
        y = y_;
        z = z_;
        origin = origin_;

        // Initialise mesh
        mesh = new CubeMesh();

        // Initialise Shader
        shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
        // Tell vertex shader where it can find vertex positions. 3 is the dimensionality of vertex position
        // The prefix "oc_" means object coordinates
        shader.bindDataToShader("oc_position", mesh.vertex_handle, 3);
        // Tell vertex shader where it can find vertex normals. 3 is the dimensionality of vertex normals
        shader.bindDataToShader("oc_normal", mesh.normal_handle, 3);
        // Tell vertex shader where it can find texture coordinates. 2 is the dimensionality of texture coordinates
        shader.bindDataToShader("texcoord", mesh.tex_handle, 2);

        // Initialise Texturing
        texture = new Texture();
        texture.load(texture_filename);

        // Build Transformation Matrices
        initial_transform = new Matrix4f();
        movement_transform = new Matrix4f();

        // Move to the initial position
        initial_transform.translate(initial_position);

        // Rotate to the initial orientation
        initial_transform.mul(initial_orientation);

        // Move to the new origin
        initial_transform.translate(origin.mul(-1));

        // Scale the transformation matrix
        initial_transform.scale(x / 2f, y / 2f, z / 2f);

        // Initialise position
        position = initial_position;
        orientation = initial_orientation;
    }

    // Change dimensions while preserving origin
    public void change_Dimensions(float newX, float newY, float newZ){
        initial_transform = initial_transform.translate(origin);
        initial_transform = initial_transform.scale(newX / x, newY / y, newZ / z);
        initial_transform = initial_transform.translate(origin.mul(-1));

        origin = origin.mul(-1);
        x = newX;
        y = newY;
        z = newZ;
    }

    // Changes the position of the cuboid's origin
    public void  change_Origin(Vector3f newOrigin){
        initial_transform = initial_transform.translate(newOrigin);
        initial_transform = initial_transform.translate(origin.mul(-1));

        origin = newOrigin;
    }
}
