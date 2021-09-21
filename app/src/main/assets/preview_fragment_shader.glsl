
#extension GL_OES_EGL_image_external_essl3 : enable

precision mediump float;
out vec4 fragColor;

in vec2 vTextureCoord;
uniform samplerExternalOES sTexture;

void main() {
    fragColor = vec4(255.0, 255.0, 0.0, 255.0);
}