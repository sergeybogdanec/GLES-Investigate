#extension GL_OES_EGL_image_external : require

uniform samplerExternalOES sTexture;
varying highp vec2 vTextureCoord;

void main() {
    gl_FragColor = texture2D(sTexture, vTextureCoord);
}