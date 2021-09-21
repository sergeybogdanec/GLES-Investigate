#extension GL_OES_EGL_image_external : require

uniform samplerExternalOES sTexture;
varying highp vec2 vTextureCoord;

void main() {
    // texture2D(sTexture, vTextureCoord)
    gl_FragColor = vec4(255.0, 255.0, 0.0, 255.0);
}