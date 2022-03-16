

precision highp float;
precision highp sampler2D;
uniform sampler2D InputBuffer;
uniform sampler2D GainMap;
uniform sampler2D PolarMap;
uniform float start;
uniform float avrbr;
uniform float intens;
uniform ivec2 size;
uniform int yOffset;
out vec3 Output;
#import interpolation
void main() {
    vec2 xy = vec2(gl_FragCoord.xy);
    xy+=vec2(0,yOffset);

    //float mapbr = length(textureLinear(GainMap,vec2(xy)/vec2(size)))/avrbr;
    vec2 inxy = vec2(xy)/vec2(size);
    inxy-=0.5;
    float s = length(inxy);
    float mapbr = (textureLinear(PolarMap,vec2(s*0.8,0.5)).r)/avrbr;
    //float len = length(inxy)/0.707106781186;
    //len=1.0+clamp((len-start),0.0,1.0)*intens;

    //Output = texture(InputBuffer,0.5 + inxy*len);
    Output = textureBicubic(InputBuffer,0.5 + inxy*(avrbr/mix(mapbr,avrbr,0.3))).rgb;
}
