#version 310

precision mediump float;

varying highp vec2 vTextureCoord;
uniform lowp sampler2D sTexture;

void main() {
    gl_FragColor = texture2D(sTexture, vTextureCoord);
}