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
	public int vertex_array_obj;
	public int no_of_triangles;
	public int vertex_handle;
	public int normal_handle;
	public int tex_handle;

	// Abstract methods that all subclasses should implement
	abstract float[]  initializeVertexPositions(); 
	abstract int[]  initializeVertexIndices();
	abstract float[]  initializeVertexNormals();
	abstract float[]  initializeTextureCoordinates();

	public Mesh() {
	}

	// Initialize mesh
	public void initialize() {

		float[] vert_positions = initializeVertexPositions();
		int[] indices = initializeVertexIndices();
		float[] vert_normals = initializeVertexNormals();
		float[] texture_coordinates = initializeTextureCoordinates();
		no_of_triangles = indices.length;

		loadDataOntoGPU( vert_positions, indices, vert_normals, texture_coordinates );
	}
	
	// Move the data from Java arrays to OpenGL buffers (these are most likely on the GPU)
	protected void loadDataOntoGPU( float[] vertPositions, int[] indices, float[] vertNormals, float[] textureCoordinates ) {

		vertex_array_obj = glGenVertexArrays(); // Get a OGL "name" for a vertex-array object
		glBindVertexArray(vertex_array_obj); // Create a new vertex-array object with that name

		// ---------------------------------------------------------------
		// LOAD VERTEX POSITIONS
		// ---------------------------------------------------------------

		// Construct the vertex buffer in CPU memory
		FloatBuffer vertex_buffer = BufferUtils.createFloatBuffer(vertPositions.length);
		vertex_buffer.put(vertPositions); // Put the vertex array into the CPU buffer
		vertex_buffer.flip(); // "flip" is used to change the buffer from read to write mode

		vertex_handle = glGenBuffers(); // Get an OGL name for a buffer object
		glBindBuffer(GL_ARRAY_BUFFER, vertex_handle); // Bring that buffer object into existence on GPU
		glBufferData(GL_ARRAY_BUFFER, vertex_buffer, GL_STATIC_DRAW); // Load the GPU buffer object with data

		// ---------------------------------------------------------------
		// LOAD VERTEX NORMALS
		// ---------------------------------------------------------------
		FloatBuffer normal_buffer = BufferUtils.createFloatBuffer(vertNormals.length);
		normal_buffer.put(vertNormals); // Put the normal array into the CPU buffer
		normal_buffer.flip(); // "flip" is used to change the buffer from read to write mode

		normal_handle = glGenBuffers(); // Get an OGL name for a buffer object
		glBindBuffer(GL_ARRAY_BUFFER, normal_handle); // Bring that buffer object into existence on GPU
		glBufferData(GL_ARRAY_BUFFER, normal_buffer, GL_STATIC_DRAW); // Load the GPU buffer object with data


		// ---------------------------------------------------------------
		// LOAD VERTEX INDICES
		// ---------------------------------------------------------------

		IntBuffer index_buffer = BufferUtils.createIntBuffer(indices.length);
		index_buffer.put(indices).flip();
		int index_handle = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index_handle);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, index_buffer, GL_STATIC_DRAW);

		// ---------------------------------------------------------------
		// LOAD Texture coordinates
		// ---------------------------------------------------------------

		// Put texture coordinate array into a buffer in CPU memory
		FloatBuffer tex_buffer = BufferUtils.createFloatBuffer(textureCoordinates.length);
		tex_buffer.put(textureCoordinates).flip();

		// Create an OpenGL buffer and load it with texture coordinate data
		tex_handle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, tex_handle);
		glBufferData(GL_ARRAY_BUFFER, tex_buffer, GL_STATIC_DRAW);

	}
}
