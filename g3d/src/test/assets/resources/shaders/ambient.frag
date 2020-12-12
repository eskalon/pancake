#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_ambient_occlusion;
uniform sampler2D u_albedo;
uniform vec3 u_ambient_color;

in vec2 v_texCoord0;

out vec4 out_Color;

void main() {
	float ao = texture(u_ambient_occlusion, v_texCoord0).b;
	vec4 albedo = texture(u_albedo, v_texCoord0);
	out_Color = vec4(u_ambient_color, 1.0) * ao * albedo;
}
