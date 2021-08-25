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

public abstract class Component {

    // Filenames for vertex and fragment shader source code
    protected final static String VSHADER_FN = "resources/cube_vertex_shader.glsl";
    protected final static String FSHADER_FN = "resources/cube_fragment_shader.glsl";

    // Define variables universal to drawable objects
    protected Mesh mesh;                   // Mesh of the component
    protected ShaderProgram shader;        // Shader to colour the component mesh
    protected Texture texture;             // Texture image to be used by the component shader
    protected Matrix4f initial_transform;  // Transformation matrix of the initialised component
    protected Matrix4f movement_transform;  // Transformation matrix of the component
    protected Vector3f position;           // Position of the component
    protected Matrix4f orientation;        // Orientation of the component

    public void move(Vector3f movement_vector){
        Matrix4f translation = new Matrix4f().translate(movement_vector);
        movement_transform = movement_transform.mulLocal(translation);
        position = position.add(movement_vector);
    }

    public void rotate(Matrix4f rotation_matrix){
        Matrix4f rotation = new Matrix4f().translate(position);
        rotation = rotation.mul(rotation_matrix);
        rotation = rotation.translate(position.mul(-1));
        movement_transform = movement_transform.mulLocal(rotation);
        position = position.mul(-1);
        orientation = orientation.mulLocal(rotation_matrix);
    }

    public void rotate(float angle, Vector3f axis){
        Matrix4f rotation = new Matrix4f().translate(position);
        rotation = rotation.rotate(angle, axis);
        rotation = rotation.translate(position.mul(-1));
        movement_transform = movement_transform.mulLocal(rotation);
        position = position.mul(-1);
        orientation = orientation.rotate(angle, axis);
    }

    // Render component, applying parent transforms if it is a child in a scene graph
    public void render(Camera camera) {
        Matrix4f render_transform = new Matrix4f().mul(movement_transform).mul(initial_transform);
        renderMesh(camera, mesh, render_transform, shader, texture);
    }
    public void render(Camera camera, Matrix4f parent_transform){
        Matrix4f render_transform = new Matrix4f().mul(parent_transform).mul(movement_transform).mul(initial_transform);
        renderMesh(camera, mesh, render_transform, shader, texture);

    }

    private void renderMesh(Camera camera, Mesh mesh , Matrix4f modelMatrix, ShaderProgram shader, Texture texture) {
        // If shaders modified on disk, reload them
        shader.reloadIfNeeded();
        shader.useProgram();

        // compute and upload MVP
        Matrix4f mvp_matrix = new Matrix4f(camera.getProjectionMatrix()).mul(camera.getViewMatrix()).mul(modelMatrix);
        shader.uploadMatrix4f(mvp_matrix, "mvp_matrix");

        // Upload Model Matrix and Camera Location to the shader for Phong Illumination
        shader.uploadMatrix4f(modelMatrix, "m_matrix");
        shader.uploadVector3f(camera.getCameraPosition(), "wc_camera_position");

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