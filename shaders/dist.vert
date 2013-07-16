uniform sampler1D sizes;
uniform sampler1D lines;
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

int pointCrossingsForLine(float px, float py, float x1, float y1, float x2, float y2) {
    if(py < y1 && py < y2) return 0;
    if(py >= y1 && py >= y2) return 0;
    if(px >= x1 && px >= x2) return 0;
    if(px < x1 && px < x2) return (y1 < y2) ? 1 : -1;
    float xintercept = x1 + (py - y1) * (x2 - x1) / (y2 - y1);
    if(px >= xintercept) return 0;
    return (y1 < y2) ? 1 : -1;
}

bool contains(int off, int size, float x, float y) {
    int numCross = 0;
    for(int p = off;p < off + size;p += 4) {
        float x1 = lines[p];
        float y1 = lines[p+1];
        float x2 = lines[p+2];
        float y2 = lines[p+3];
        if(y1 != y2) {
            numCross += pointCrossingsForLine(x, y, x1, y1, x2, y2);
        }
    }
    return (numCross % 2) == 0;
}

void main() {
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    float x = gl_Position.x;
    float y = gl_Position.y;
    bool in = false;
    int off = 0;
    for(int i = 0;i < lines.length(); ++i) {
        in = in || contains(off, sizes[i], x, y);
        off += sizes[i];
    }
    if(in) {
        vertColor = vec4(1, 0, 0, 0);
    } else {
        vertColor = vec4(1, 1, 1, 1);
    }
    //float lon = tileXToLon(x) + 180.0;
    //float lat = tileYToLat(y) + 90.0;
    //vertColor = vec4(lon / 360.0, lat / 180.0, 0.0, 0.8);
    //vertColor.xy = vec2((1.0 + gl_Position.x) * 0.5, -gl_Position.y);
}