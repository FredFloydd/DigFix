package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;


import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class Rectangle extends Component {

    // Side lengths
    private float x;
    private float z;

    // Location of origin within cuboid
    private Vector3f origin;

    public Rectangle(Vector3f dimensions, Vector3f origin,  Vector3f position,
                  Matrix4f orientation, String texture_filename) {

        // Initialise basic parameters
        this.x = dimensions.x;
        this.z = dimensions.z;
        this.origin = origin;

        // Initialise mesh
        mesh = new RectangleMesh();

        // Initialise Shader
        shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
        shader.bindDataToShader("oc_position", mesh.vertex_handle, 3);
        shader.bindDataToShader("oc_normal", mesh.normal_handle, 3);
        shader.bindDataToShader("texcoord", mesh.tex_handle, 2);

        // Initialise Texturing
        texture = new Texture();
        texture.load(texture_filename);

        // Build Transformation Matrices
        initial_transform = new Matrix4f();
        movement_transform = new Matrix4f();

        // Move to the initial position
        initial_transform.translate(position);

        // Rotate to the initial orientation
        initial_transform.mul(orientation);

        // Move to the new origin
        initial_transform.translate(origin.mul(-1));

        // Scale the transformation matrix
        initial_transform.scale(x / 2f, 1f, z / 2f);

        // Initialise position
        this.position = position;
        this.orientation = orientation;
    }

    // Change dimensions while preserving origin
    public void changeDimensions(float x, float z){
        initial_transform.translate(origin);
        initial_transform.scale(x / this.x, 1f, z / this.z);
        initial_transform.translate(new Vector3f(origin).mul(-1));
        this.x = x;
        this.z = z;
    }

    // Changes the position of the cuboid's origin
    public void  changeOrigin(Vector3f origin){
        initial_transform.translate(origin);
        initial_transform.translate(this.origin.mul(-1));
        this.origin = origin;
    }
}
