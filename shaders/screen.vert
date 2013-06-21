varying vec4 vertColor;

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    float x = (gl_Position.x + 1.0) / 2.0;
    float y = 1.0 - (gl_Position.y + 1.0) / 2.0;
    vertColor = vec4(x, y, 1, 1);
}