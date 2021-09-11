package DigFix;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

// Combines vertex and fragment Shaders into a single program that can be used for rendering.
public class ShaderProgram {
    Shader vertex_shader;
    Shader fragment_shader;
    String output_variable;
    private int program = 0;

    public ShaderProgram(Shader vertex_shader, Shader fragment_shader, String output_variable) {
        this.vertex_shader = vertex_shader;
        this.fragment_shader = fragment_shader;
        this.output_variable = output_variable;

        createProgram( output_variable );
    }

    public void createProgram( String output_variable )
    {
        if( program != 0 )
            glDeleteProgram( program );

        program = glCreateProgram();
        glAttachShader(program, vertex_shader.getHandle());
        glAttachShader(program, fragment_shader.getHandle());
        glBindFragDataLocation(program, 0, output_variable);
        glLinkProgram(program);
        glUseProgram(program);
    }

    public int getHandle() {
        return program;
    }
    
    public void useProgram() {
    	glUseProgram(program);
    }
    
    // Tell vertex shader where it can find vertex attributes
    public void bindDataToShader(String variableName, int ArrayBufferHandle, int AttribSize) {

        glBindBuffer(GL_ARRAY_BUFFER, ArrayBufferHandle); // Bring that buffer object into existence on GPU

        // Get the locations of the "position" vertex attribute variable in our ShaderProgram
		int variable_loc = glGetAttribLocation(getHandle(), variableName);

		// If the vertex attribute does not exist, position_loc will be -1, so we should not use it
		if (variable_loc != -1) {

			// Specifies where the data for given variable can be accessed
			glVertexAttribPointer(variable_loc, AttribSize, GL_FLOAT, false, 0, 0);

			// Enable that vertex attribute variable
			glEnableVertexAttribArray(variable_loc);
		} else {
			System.out.println("NO " + variableName);
		}
    }
    
	// Upload a 4x4 matrix 'm' to 'target' shader variable
    public void uploadMatrix4f(Matrix4f m, String target) {
		int location = glGetUniformLocation(getHandle(), target);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		m.get(buffer);
		glUniformMatrix4fv(location, false, buffer);
    }
    
	// Upload a 3x3 matrix 'm' to 'target' shader variable
    public void uploadMatrix3f(Matrix3f m, String target) {
		int location = glGetUniformLocation(getHandle(), target);
		FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
		m.get(buffer);
		glUniformMatrix3fv(location, false, buffer);
    }
    
	//Upload a 3D vector 'v' to 'target' shader variable
    public void uploadVector3f(Vector3f v, String target) {
		int location = glGetUniformLocation(getHandle(), target);
		glUniform3f(location, v.x, v.y, v.z);
    }
}
