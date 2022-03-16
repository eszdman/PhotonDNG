precision mediump float;
precision mediump sampler2D;
uniform sampler2D InputBuffer;
uniform int CfaPattern;
uniform int yOffset;
out vec3 Output;
vec3 firstdiag(in ivec2 xy){
   return (texelFetch(InputBuffer, (xy), 0)+texelFetch(InputBuffer, (xy+ivec2(1,1)), 0));
}
vec3 seconddiag(in ivec2 xy){
    return (texelFetch(InputBuffer, (xy+ivec2(0,1)), 0).x+texelFetch(InputBuffer, (xy+ivec2(1,0)), 0).x);
}
void main() {
    ivec2 xy = ivec2(gl_FragCoord.xy);
    xy+=ivec2(0,yOffset);
    xy*=2;
    float firstW = 1.45;//45% GreenChannel boost
    if(CfaPattern == 1 || CfaPattern == 2) firstW = 0.6;
    vec3 outp=firstdiag(xy)*(firstW);
    outp+=seconddiag(xy)*(2.0-firstW);
    outp/=8.0;
    Output = outp;
}
