uniform vec2 size;
uniform vec2 tile;
uniform float zoom;

#define M_PI 3.1415926535897932384626433832795

varying vec4 vertColor;

float sin_h(float x) {
    return 0.5 * (exp(x) - exp(-x));
}

float tileYToLat(float y) {
    y = tile.y * 2.0 - y;
    return atan(sin_h(M_PI - (M_PI * y / exp2(zoom - 1.0)))) * 180.0 / M_PI;
}

float tileXToLon(float x) {
    x = x + tile.x * 2.0;
    return x * 45.0 / exp2(zoom - 3.0) - 180.0;
}

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    float x = gl_Position.x;
    float y = gl_Position.y;
    float lon = tileXToLon(x) + 180.0;
    float lat = tileYToLat(y) + 90.0;
    vertColor = vec4(lon / 360.0, lat / 180.0, 0.0, 0.8);
    //vertColor.xy = vec2((1.0 + gl_Position.x) * 0.5, -gl_Position.y);
}