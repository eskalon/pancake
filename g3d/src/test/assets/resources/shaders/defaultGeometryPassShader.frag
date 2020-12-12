#ifdef GL_ES
precision mediump float;
#endif

in vec3 v_position;
in vec2 v_texCoord0;
in vec3 v_normal;
in mat3 v_tangentWorldTrans;

uniform sampler2D u_albedoTexture;
uniform sampler2D u_normalTexture;
uniform sampler2D u_metallicTexture;
uniform sampler2D u_roughnessTexture;
uniform sampler2D u_ambientTexture;

out vec3 albedoOut;
out vec3 normalOut;
out vec3 materialOut;

void main() {
	vec3 albedo = texture(u_albedoTexture, v_texCoord0).rgb;

	vec2 normalTexCoord = vec2(v_texCoord0.x, -v_texCoord0.y);
	vec3 normal = texture(u_normalTexture, normalTexCoord).rgb;
	normal = normalize(normal * 2.0 - 1.0);
	normal = normalize(v_tangentWorldTrans * normal);

	float metallic = texture(u_metallicTexture, v_texCoord0).r;
	float roughness = texture(u_roughnessTexture, v_texCoord0).r;
	float ambient = texture(u_ambientTexture, v_texCoord0).r;
	
	materialOut = vec3(metallic, roughness, ambient);
	normalOut = normal;
	albedoOut = albedo;
}
