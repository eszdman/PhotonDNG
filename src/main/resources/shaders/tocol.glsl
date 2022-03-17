precision highp float;
precision highp int;
layout(rgba16f, binding = 0) uniform highp readonly image2D inTexture;
layout(rgba16f, binding = 2) uniform highp writeonly image2D outTexture;
#define WB 1.0,1.0,1.0
#define BL 0.0,0.0,0.0
#define LAYOUT //
#define OUTSET 0,0
LAYOUT
void main() {
    ivec2 xyIn = ivec2(gl_GlobalInvocationID.xy);
    if(xyIn.x >= ivec2(OUTSET).x) return;
    if(xyIn.y >= ivec2(OUTSET).y) return;

    vec4 inp = imageLoad(inTexture,xyIn);

    inp.a = (inp.r*0.2+inp.g*0.5+inp.b*0.3);

    imageStore(outTexture, xyIn, inp);
}