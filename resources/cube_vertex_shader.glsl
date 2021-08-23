#version 140

in vec3 oc_position;       	// vertex position in object coordinates (oc_)
in vec3 oc_normal;          // vertex normal in object coordinates
in vec2 texcoord;			// texture coordinate in texture space

out vec3 wc_frag_normal;    // fragment normal in world coordinates
out vec2 frag_texcoord;    	// fragment texture coordinate in world coordinates
out vec3 wc_frag_pos; 		// fragment position in world coordinates

uniform mat4 mvp_matrix; 	// model-view-projection matrix
uniform mat4 m_matrix;		// model matrix
uniform mat3 normal_matrix;	// inverse transpose of the model matrix

void main()
{
    frag_texcoord = texcoord;  // We just copy texture coordinates and pass to fragment shader

    // Transform vertex position from local to world coordinates

    vec4 homogeneous_oc_position = m_matrix * vec4(oc_position, 1.0);
    wc_frag_pos = vec3(homogeneous_oc_position) / homogeneous_oc_position[3];

    // Transform vertex normal from local to world coordinates

    wc_frag_normal = normal_matrix * oc_normal;

    // The position is projected to the screen coordinates using mvp_matrix
    gl_Position = mvp_matrix * vec4(oc_position, 1.0);
}

