#version 310

uniform mat4 uMVPMatrix;
uniform mat4 uSTMatrix;
uniform float uCRatio;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;
varying highp vec2 vTextureCoord;

void main() {
    vec4 scaledPos = aPosition;
    scaledPos.x = scaledPos.x * uCRatio;
    gl_Position = uMVPMatrix * scaledPos;
    vTextureCoord = (uSTMatrix * aTextureCoord).xy;
}