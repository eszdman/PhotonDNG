precision highp float;
precision highp usampler2D;
precision mediump sampler2D;
uniform sampler2D InputBuffer;
uniform sampler2D GainMap;
uniform ivec2 RawSize;
uniform vec4 blackLevel;
uniform vec3 whitePoint;
uniform int CfaPattern;
uniform int patSize;
uniform uint whitelevel;
uniform float Regeneration;
#define QUAD 0
#define AGAIN 1.0
#import interpolation
#import median
out float Output;
void main() {
    ivec2 xy = ivec2(gl_FragCoord.xy);
    ivec2 fact = (xy)%2;
    xy+=ivec2(CfaPattern%2,CfaPattern/2);
    #if QUAD == 1
    fact = (xy/2)%2;
    xy+=ivec2(CfaPattern%2,CfaPattern/2);
    #endif
    float balance;
    vec4 gains = textureBicubicHardware(GainMap, vec2(xy)/vec2(RawSize));
    gains.rgb = vec3(gains.r,(gains.g+gains.b)/2.0,gains.a);
    vec3 level = vec3(blackLevel.r,(blackLevel.g+blackLevel.b)/2.0,blackLevel.a);
    if(fact.x+fact.y == 1){
        balance = whitePoint.g;
        Output = float(texelFetch(InputBuffer, (xy), 0).x)*AGAIN;
        Output = gains.g*(Output-level.g)/(1.0-level.g);
    } else {
        if(fact.x == 0){
            balance = whitePoint.r;
            Output = float(texelFetch(InputBuffer, (xy), 0).x)*AGAIN;
            Output = gains.r*(Output-level.r)/(1.0-level.r);
        } else {
            balance = whitePoint.b;
            Output = float(texelFetch(InputBuffer, (xy), 0).x)*AGAIN;
            Output = gains.b*(Output-level.b)/(1.0-level.b);
        }
    }
    Output = clamp(Output,0.0,balance)/Regeneration;
}
