#ifdef GL_ES
precision mediump float;
#endif

in vec3 a_position;
in vec2 a_texCoord0;
in vec3 a_normal;
in vec3 a_tangent;
in vec3 a_binormal;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform mat3 u_normalWorldTrans;

out vec3 v_position;
out vec2 v_texCoord0;
out vec3 v_normal;
out mat3 v_tangentWorldTrans;

void main() {
	v_texCoord0 = a_texCoord0;
	v_normal = u_normalWorldTrans * a_normal;

	vec3 T = normalize(vec3(u_normalWorldTrans * a_tangent));
	vec3 B = normalize(vec3(u_normalWorldTrans * a_binormal));
	vec3 N = normalize(vec3(u_normalWorldTrans * a_normal));
	v_tangentWorldTrans = mat3(T, B, N);

	vec4 position = u_worldTrans * vec4(a_position, 1.0);
	v_position = position.xyz / position.w;

	vec4 pos = u_projViewTrans * position;
	gl_Position = pos;
}
