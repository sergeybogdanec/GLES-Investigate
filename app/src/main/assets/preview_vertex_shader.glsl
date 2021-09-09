#version 300 es

in vec4 aPosition;
in vec4 aTextureCoord;

uniform mat4 uMVPMatrix;
uniform mat4 uSTMatrix;
uniform float uCRatio;

out vec2 vTextureCoord;

void main() {
    vec4 scaledPos = aPosition;
    scaledPos.x = scaledPos.x * uCRatio;
    gl_Position = uMVPMatrix * scaledPos;
    vTextureCoord = (uSTMatrix * aTextureCoord).xy;
}