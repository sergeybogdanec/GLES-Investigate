#version 310 es

precision mediump float;

uniform vec4 vColor;

out vec4 fragmentColor;

void main() {
    fragmentColor = vColor;
}
