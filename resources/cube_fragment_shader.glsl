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
	// Calculate colour using Phong illumination model
	vec3 point_light_position = vec3(0, 100000, 0);
    vec3 point_light_colour = vec3(1, 1, 1);
    float point_light_intensity = 1000000000;

    float ambient_intensity = 0.2;
    float k_diffuse = 0.9;
    float k_specular = 0.1;
    float alpha = 10;

    vec3 view_ray = normalize(wc_frag_pos - wc_camera_position);
    vec3 reflected_ray = normalize(reflect(point_light_position - wc_frag_pos, wc_frag_normal));
    vec3 light_ray = normalize(point_light_position - wc_frag_pos);

    float light_intensity = point_light_intensity / pow(length(point_light_position - wc_frag_pos), 2);

    // Sample texture to obtain c_diffuse
    vec3 c_diffuse = vec3(texture(tex, frag_texcoord));

	// Calculate Phong illumination
    vec3 ambient = c_diffuse * ambient_intensity;
    vec3 diffuse = c_diffuse * k_diffuse * light_intensity * max(0, dot(light_ray, wc_frag_normal));
    vec3 specular = point_light_colour * k_specular * light_intensity * pow(max(0, dot(reflected_ray, view_ray)), alpha);

    linear_color = ambient + diffuse + specular;

	color = tonemap(linear_color);
}

