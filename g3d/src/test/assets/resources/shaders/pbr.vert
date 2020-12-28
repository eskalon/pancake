#ifdef GL_ES
precision mediump float;
#endif

in vec3 a_position;

uniform mat4 u_projView;
uniform vec3 u_position;
uniform float u_radius;

void main() {
	gl_Position = u_projView * vec4(a_position * u_radius + u_position, 1.0);
}
