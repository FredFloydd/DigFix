package DigFix;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public abstract class Component {

    // Filenames for vertex and fragment shader source code
    protected final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    protected final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";

    // Variables used by shader for drawing component
    protected Mesh mesh;
    protected ShaderProgram shader;
    protected Texture texture;

    // Variables used to transform the component in 3D space
    protected Matrix4f initialTransform;
    protected Matrix4f movementTransform;
    protected Vector3f position;
    protected Matrix4f orientation;

    protected boolean invisible;

    // Moves the component by a given amount in its frame
    public void move(Vector3f movementVector) {
        position.add(movementVector);
        Matrix4f translation = new Matrix4f().translate(movementVector);
        movementTransform.mulLocal(translation);
    }

    // Rotates the component by a given amount in its frame
    public void rotate(float angle, Vector3f axis) {
        Matrix4f rotation = new Matrix4f().translate(position);
        rotation.rotate(angle, axis);
        rotation.translate(new Vector3f(position).mul(-1));
        movementTransform.mulLocal(rotation);
        orientation.rotate(angle, axis);
    }

    // Returns component to the origin, with no rotation
    public void initialiseMovement() {
        movementTransform = new Matrix4f();
        orientation = new Matrix4f();
    }

    // Render component, applying parent transforms if it is a child in a scene graph
    public void render(Camera camera) {
        Matrix4f renderTransform = new Matrix4f().mul(movementTransform).mul(initialTransform);
        if (!invisible) {
            renderMesh(camera, mesh, renderTransform, shader, texture);
        }
    }

    public void render(Camera camera, Matrix4f parentTransform) {
        Matrix4f renderTransform = new Matrix4f().mul(parentTransform).mul(movementTransform).mul(initialTransform);
        if (!invisible) {
            renderMesh(camera, mesh, renderTransform, shader, texture);
        }
    }

    // Draws the component from its mesh
    private void renderMesh(Camera camera, Mesh mesh, Matrix4f modelMatrix, ShaderProgram shader, Texture texture) {
        // Tell GPU to use the shader program
        shader.useProgram();

        // Compute and upload MVP
        Matrix4f mvpMatrix = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getTransform()).mul(modelMatrix);
        shader.uploadMatrix4f(mvpMatrix, "mvp_matrix");

        // Upload Model Matrix and Camera Location to the shader for Phong Illumination
        shader.uploadMatrix4f(modelMatrix, "m_matrix");
        shader.uploadVector3f(camera.getPosition(), "wc_camera_position");

        // Transformation by a non-orthogonal matrix does not preserve angles
        // Thus we need a separate transformation matrix for normals
        Matrix3f normalMatrix = new Matrix3f();
        // Calculate normal transformation matrix
        normalMatrix = new Matrix3f(modelMatrix).invert().transpose().mul(normalMatrix);

        shader.uploadMatrix3f(normalMatrix, "normal_matrix");

        // Bind Texture
        texture.bindTexture();

        // Draw
        glBindVertexArray(mesh.vertexArrayObj);                                          // Bind the existing VertexArray object
        glDrawElements(GL_TRIANGLES, mesh.noOfTriangles, GL_UNSIGNED_INT, 0);   // Draw it as triangles
        glBindVertexArray(0);                                                            // Remove the binding

        // Unbind texture
        texture.unBindTexture();
    }
}