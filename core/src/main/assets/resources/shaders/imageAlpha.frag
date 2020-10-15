#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

in vec2 v_texCoord0;

out vec4 out_Color;

void main() {
	vec4 col = texture(u_texture, v_texCoord0);
	out_Color = vec4(col.a, col.a, col.a, 1);
}
