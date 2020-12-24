#ifdef GL_ES
precision mediump float;
#endif

in vec3 a_position;

uniform mat4 u_view;
uniform mat4 u_proj;

out vec3 v_texCoord0;

void main() {
    v_texCoord0 = a_position;
    gl_Position = (u_proj * u_view * vec4(a_position, 1.0)).xyww;
}
