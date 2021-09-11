package DigFix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

//Shader class loads and compiles shaders, so they can be attached to a ShaderProgram.
public class Shader {

    private int shaderID = 0;
    private final int type;
    private final String filename;
    private long shaderTimestamp = 0;

    public Shader(int type, String filename) {
        this.type = type;
        this.filename = filename;
        load( type, filename );
    }

     // Load shaderID from file and compile it
    public void load(int type, String filename) {

        // Read the shaderID source code from file
        String shaderSource = null;
        try {
            List<String> shaderSourceLines = Files.readAllLines(Paths.get(filename));
            shaderSource = String.join("\n", shaderSourceLines);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shaderID: " + filename);
        }

        // Create and compile the shaderID
        shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);

        // Check in case there was an error during compilation
        int status = glGetShaderi(shaderID, GL_COMPILE_STATUS );
        if (status == 0) {
            String error = glGetShaderInfoLog(shaderID);
            System.out.println(error);
            glDeleteShader(shaderID);
            throw new RuntimeException("shader compilation failed: consult the log above");
        }
    }

    public int getHandle() {
        return shaderID;
    }
}
