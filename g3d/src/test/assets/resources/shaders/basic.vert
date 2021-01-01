#ifdef GL_ES
precision mediump float;
#endif

in vec3 a_position;

uniform mat4 u_projView;
uniform mat4 u_model;

out vec3 v_position;

void main() {
	v_position = a_position;
    gl_Position = u_projView * u_model * vec4(a_position, 1.0);
}
