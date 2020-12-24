#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

in vec2 v_texCoord0;

out vec4 out_Color;

void main() {
	float depth = texture(u_texture, v_texCoord0).r;
	out_Color = vec4(1- depth, 1- depth, 1- depth, 1);
}
