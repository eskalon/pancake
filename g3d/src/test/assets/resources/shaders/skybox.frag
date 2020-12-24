#ifdef GL_ES
precision mediump float;
#endif

uniform samplerCube u_cube;

in vec3 v_texCoord0;

out vec4 out_Color;

void main() {

	out_Color = texture(u_cube, v_texCoord0);
}
