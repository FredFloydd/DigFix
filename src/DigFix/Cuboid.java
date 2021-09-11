package DigFix;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

public class Cuboid extends Component{

    // Side lengths
    private float x;
    private float y;
    private float z;

    // Location of origin within cuboid
    private Vector3f origin;

    public Cuboid(Vector3f dimensions, Vector3f origin,  Vector3f position,
                  Matrix4f orientation, String textureFilename) {

        // Initialise basic parameters
        this.x = dimensions.x;
        this.y = dimensions.y;
        this.z = dimensions.z;
        this.origin = origin;

        // Initialise mesh
        mesh = new CubeMesh();

        // Initialise Shader
        shader = new ShaderProgram(new Shader(GL_VERTEX_SHADER, VSHADER_FN), new Shader(GL_FRAGMENT_SHADER, FSHADER_FN), "colour");
        shader.bindDataToShader("oc_position", mesh.vertexHandle, 3);
        shader.bindDataToShader("oc_normal", mesh.normalHandle, 3);
        shader.bindDataToShader("texcoord", mesh.texHandle, 2);

        // Initialise Texturing
        texture = new Texture();
        texture.load(textureFilename);

        // Build Transformation Matrices
        initialTransform = new Matrix4f();
        movementTransform = new Matrix4f();

        // Move to the initial position
        initialTransform.translate(position);

        // Rotate to the initial orientation
        initialTransform.mul(orientation);

        // Move to the new origin
        initialTransform.translate(origin.mul(-1));

        // Scale the transformation matrix
        initialTransform.scale(x / 2f, y / 2f, z / 2f);

        // Initialise position
        this.position = position;
        this.orientation = orientation;
    }

    // Change dimensions while preserving origin
    public void changeDimensions(float x, float y, float z){
        initialTransform.translate(origin);
        initialTransform.scale(x / this.x, y / this.y, z / this.z);
        initialTransform.translate(new Vector3f(origin).mul(-1));
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Changes the position of the cuboid's origin
    public void  changeOrigin(Vector3f origin){
        initialTransform.translate(origin);
        initialTransform.translate(this.origin.mul(-1));
        this.origin = origin;
    }
}
