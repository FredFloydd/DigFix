package DigFix;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public abstract class Component {

    // Filenames for vertex and fragment shader source code
    protected final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    protected final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";

    // Variables used by shader for drawing component
    protected Mesh mesh;                   // Mesh of the component
    protected ShaderProgram shader;        // Shader to colour the component mesh
    protected Texture texture;             // Texture image to be used by the component shader

    // Variables used to transform the component in 3D space
    protected Matrix4f initial_transform;  // Initial transform of the component
    protected Matrix4f movement_transform; // Subsequent transform of the component
    protected Vector3f position;           // Position of the component in its frame
    protected Matrix4f orientation;        // Matrix describing the orientation of the component

    protected boolean invisibile;          // Boolean to determine whether to draw component

    // Moves the component by a given amount in its frame
    public void move(Vector3f movement_vector) {
        position = position.add(movement_vector);
        Matrix4f translation = new Matrix4f().translate(movement_vector);
        movement_transform = movement_transform.mulLocal(translation);
    }

    public void moveWorldCoords(Vector3f movement_vector) {
        movement_vector.x *= -1;
        movement_vector.z *= -1;
        move(movement_vector);
    }

    public void rotateWorldCoords(double phi, double theta, Vector3f up) {
        Vector3f new_orientation = new Vector3f();
        new_orientation.x = (float) (Math.sin(phi) * Math.sin(theta));
        new_orientation.y = (float) Math.cos(theta);
        new_orientation.z = (float) -(Math.cos(phi) * Math.sin(theta));
        initial_transform = new Matrix4f().lookAlong(new_orientation, up);
        orientation = new Matrix4f().lookAlong(new_orientation, up);
    }

    // Rotates the component by a given amount in its frame
    public void rotate(float angle, Vector3f axis) {
        Matrix4f rotation = new Matrix4f().translate(position);
        rotation = rotation.rotate(angle, axis);
        rotation = rotation.translate(new Vector3f(position).mul(-1));
        movement_transform = movement_transform.mulLocal(rotation);
        orientation = orientation.rotate(angle, axis);
    }

    public void initialiseMovement() {
        movement_transform = new Matrix4f();
        orientation = new Matrix4f();
    }

    // Render component, applying parent transforms if it is a child in a scene graph
    public void render(Camera camera) {
        Matrix4f render_transform = new Matrix4f().mul(movement_transform).mul(initial_transform);
        if (!invisibile) {
            renderMesh(camera, mesh, render_transform, shader, texture);
        }
    }

    public void render(Camera camera, Matrix4f parent_transform) {
        Matrix4f render_transform = new Matrix4f().mul(parent_transform).mul(movement_transform).mul(initial_transform);
        if (!invisibile) {
            renderMesh(camera, mesh, render_transform, shader, texture);
        }
    }

    // Draws the component from its mesh
    private void renderMesh(Camera camera, Mesh mesh, Matrix4f modelMatrix, ShaderProgram shader, Texture texture) {
        // If shaders modified on disk, reload them
        shader.reloadIfNeeded();
        shader.useProgram();

        // compute and upload MVP
        Matrix4f mvp_matrix = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getTransform()).mul(modelMatrix);
        shader.uploadMatrix4f(mvp_matrix, "mvp_matrix");

        // Upload Model Matrix and Camera Location to the shader for Phong Illumination
        shader.uploadMatrix4f(modelMatrix, "m_matrix");
        shader.uploadVector3f(camera.getPosition(), "wc_camera_position");

        // Transformation by a non-orthogonal matrix does not preserve angles
        // Thus we need a separate transformation matrix for normals
        Matrix3f normal_matrix = new Matrix3f();
        // Calculate normal transformation matrix
        normal_matrix = new Matrix3f(modelMatrix).invert().transpose().mul(normal_matrix);

        shader.uploadMatrix3f(normal_matrix, "normal_matrix");

        // Bind Texture
        texture.bindTexture();

        // Draw
        glBindVertexArray(mesh.vertexArrayObj);                                          // Bind the existing VertexArray object
        glDrawElements(GL_TRIANGLES, mesh.no_of_triangles, GL_UNSIGNED_INT, 0);   // Draw it as triangles
        glBindVertexArray(0);                                                            // Remove the binding

        // Unbind texture
        texture.unBindTexture();
    }
}