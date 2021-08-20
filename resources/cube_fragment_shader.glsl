#version 140

in vec3 wc_frag_normal;        	// fragment normal in world coordinates (wc_)
in vec2 frag_texcoord;			// texture UV coordinates
in vec3 wc_frag_pos;			// fragment position in world coordinates

out vec3 color;			        // pixel colour

uniform sampler2D tex;  		  // 2D texture sampler
uniform vec3 wc_camera_position;  // Position of the camera in world coordinates

// Tone mapping and display encoding combined
vec3 tonemap(vec3 linearRGB)
{
    float L_white = 0.7; // Controls the brightness of the image

    float inverseGamma = 1./2.2;
    return pow(linearRGB/L_white, vec3(inverseGamma)); // Display encoding - a gamma
}

void main()
{
	vec3 linear_color = vec3(0, 0, 0);
	// TODO: Calculate colour using Phong illumination model
	// colour = ambient + diffuse + specular
	// TODO: Sample the texture and replace diffuse surface colour (C_diff) with texel value


	color = tonemap(linear_color);
}

