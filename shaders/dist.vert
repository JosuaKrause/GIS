varying vec2 pos;

void main() {
    gl_Position.xy = (gl_Vertex.xy - vec2(1, 1));
    pos = (gl_Position.xy + vec2(1, 1)) * 0.5;
}