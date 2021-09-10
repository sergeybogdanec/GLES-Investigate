#version 300 es

in vec3 aPosition;
in vec2 aTextureCoord;

uniform mat4 uMVPMatrix;
uniform mat4 uTMatrix;

out vec2 vTextureCoord;

void main() {
    vTextureCoord = aTextureCoord;
    gl_Position = uMVPMatrix * uTMatrix * vec4(aPosition, 1.0);
}