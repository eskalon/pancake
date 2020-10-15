#ifdef GL_ES
precision mediump float;
#endif

in vec3 a_position;
in vec2 a_texCoord0;

uniform mat4 u_projTrans;

out vec2 v_texCoord0;

void main() {
    v_texCoord0 = a_texCoord0;
    gl_Position = u_projTrans * vec4(a_position, 1.0);
}
