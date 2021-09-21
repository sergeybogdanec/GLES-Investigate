attribute vec3 aPosition;
attribute highp vec2 aTextureCoord;
uniform mat4 uMVPMatrix;
uniform mat4 uTMatrix;

varying highp vec2 vTextureCoord;

void main() {
    vTextureCoord = aTextureCoord;
    gl_Position = uMVPMatrix * uTMatrix * vec4(aPosition, 1.0);
}