package DigFix;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;


// Abstract class encapsulating a 3D mesh object
// Mesh object must have 3D position, UV texture coordinates and normals

public abstract class Mesh {

	// Shape and rendering properties
	public int vertexArrayObj;
	public int noOfTriangles;
	public int vertexHandle;
	public int normalHandle;
	public int texHandle;

	// Abstract methods that all subclasses should implement
	abstract float[]  initializeVertexPositions(); 
	abstract int[]  initializeVertexIndices();
	abstract float[]  initializeVertexNormals();
	abstract float[]  initializeTextureCoordinates();

	public Mesh() {
	}

	// Initialize mesh
	public void initialize() {

		float[] vertPositions = initializeVertexPositions();
		int[] indices = initializeVertexIndices();
		float[] vertNormals = initializeVertexNormals();
		float[] textureCoordinates = initializeTextureCoordinates();
		noOfTriangles = indices.length;

		loadDataOntoGPU( vertPositions, indices, vertNormals, textureCoordinates );
	}
	
	// Move the data from Java arrays to OpenGL buffers (these are most likely on the GPU)
	protected void loadDataOntoGPU( float[] vertPositions, int[] indices, float[] vertNormals, float[] textureCoordinates ) {

		vertexArrayObj = glGenVertexArrays(); // Get a OGL "name" for a vertex-array object
		glBindVertexArray(vertexArrayObj); // Create a new vertex-array object with that name

		// ---------------------------------------------------------------
		// LOAD VERTEX POSITIONS
		// ---------------------------------------------------------------

		// Construct the vertex buffer in CPU memory
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertPositions.length);
		vertexBuffer.put(vertPositions); // Put the vertex array into the CPU buffer
		vertexBuffer.flip(); // "flip" is used to change the buffer from read to write mode

		vertexHandle = glGenBuffers(); // Get an OGL name for a buffer object
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle); // Bring that buffer object into existence on GPU
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); // Load the GPU buffer object with data

		// ---------------------------------------------------------------
		// LOAD VERTEX NORMALS
		// ---------------------------------------------------------------
		FloatBuffer normalBuffer = BufferUtils.createFloatBuffer(vertNormals.length);
		normalBuffer.put(vertNormals); // Put the normal array into the CPU buffer
		normalBuffer.flip(); // "flip" is used to change the buffer from read to write mode

		normalHandle = glGenBuffers(); // Get an OGL name for a buffer object
		glBindBuffer(GL_ARRAY_BUFFER, normalHandle); // Bring that buffer object into existence on GPU
		glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW); // Load the GPU buffer object with data


		// ---------------------------------------------------------------
		// LOAD VERTEX INDICES
		// ---------------------------------------------------------------

		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices).flip();
		int indexHandle = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexHandle);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

		// ---------------------------------------------------------------
		// LOAD Texture coordinates
		// ---------------------------------------------------------------

		// Put texture coordinate array into a buffer in CPU memory
		FloatBuffer texBuffer = BufferUtils.createFloatBuffer(textureCoordinates.length);
		texBuffer.put(textureCoordinates).flip();

		// Create an OpenGL buffer and load it with texture coordinate data
		texHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, texHandle);
		glBufferData(GL_ARRAY_BUFFER, texBuffer, GL_STATIC_DRAW);

	}
}
