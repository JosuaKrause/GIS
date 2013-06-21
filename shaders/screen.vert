uniform vec2 size;
uniform vec2 tile;
uniform float zoom;

#define M_PI 3.1415926535897932384626433832795

varying vec4 vertColor;

float sin_h(float x) {
    return 0.5 * (exp(x) - exp(-x));
}

float latToTileY(float lat) {
    float l = lat / 180.0 * M_PI;
    float pf = log(tan(l) + 1.0 / cos(l));
    float res = exp2(zoom - 1.0) * (M_PI - pf) / M_PI;
    return (res - tile.y) / size.y;
}

float lonToTileX(float lon) {
    float res = exp2(zoom - 3.0) * (lon + 180.0) / 45.0;
    return (res - tile.x) / size.x;
}

float tileYToLat(float y) {
    y = (y * size.y) + tile.y;
    return atan(sin_h(M_PI - (M_PI * y / exp2(zoom - 1.0)))) * 180.0 / M_PI;
}

float tileXToLon(float x) {
    x = (x * size.x) + tile.x;
    return x * 45.0 / exp2(zoom - 3.0) - 180.0;
}

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    float x = (gl_Position.x + 1.0) / 2.0;
    float y = 1.0 - (gl_Position.y + 1.0) / 2.0;
    float lon = tileXToLon(x) + 180.0;
    float lat = tileYToLat(y) + 180.0;
    vertColor = vec4(lon / 360.0, lat / 360.0, 1, 0.8);
}