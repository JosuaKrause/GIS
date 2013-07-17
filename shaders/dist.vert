uniform sampler1D lines;
uniform vec2 size;
uniform vec2 tile;
uniform float zoom;
uniform int lines_length;

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

vec4 getLine(int pos) {
    return texture1D(lines, float(pos) / float(4 * lines_length));
}

int pointCrossingsForLine(float px, float py, vec4 line) {
    float x1 = line.x;
    float y1 = line.y;
    float x2 = line.z;
    float y2 = line.w;
    if(py < y1 && py < y2) return 0;
    if(py >= y1 && py >= y2) return 0;
    if(px >= x1 && px >= x2) return 0;
    if(px < x1 && px < x2) return (y1 < y2) ? 1 : -1;
    float xintercept = x1 + (py - y1) * (x2 - x1) / (y2 - y1);
    if(px >= xintercept) return 0;
    return (y1 < y2) ? 1 : -1;
}

int mod(int x, int y) {
    return x - y*int(x / y);
}

bool contains(float x, float y) {
    int numCross = 0;
    for(int p = 0;p < lines_length;++p) {
        vec4 line = getLine(p);
        if(line.y != line.w) {
            numCross += pointCrossingsForLine(x, y, line);
        }
    }
    return mod(numCross, 2) == 0;
}

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    float x = gl_Position.x;
    float y = gl_Position.y;
    bool within = contains(x, y);
    if(within) {
        vertColor = vec4(1, 0, 0, 1);
    } else {
        vertColor = vec4(0, 0, 0, 1);
    }
    vertColor = vec4(1, 0, 0, 1);
    if(x < 0.5) {
        vertColor.y = 1.0;
    }
    if(y < 0.5) {
        vertColor.z = 1.0;
    }
    //float lon = tileXToLon(x) + 180.0;
    //float lat = tileYToLat(y) + 90.0;
    //vertColor = vec4(lon / 360.0, lat / 180.0, 0.0, 0.8);
    //vertColor.xy = vec2((1.0 + gl_Position.x) * 0.5, -gl_Position.y);
}