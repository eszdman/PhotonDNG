vec4 cubic(float x)
{
    float x22 = x * x;
    float x33 = x22 * x;
    vec4 w;
    w.x =   -x33 + 3.0*x22 - 3.0*x + 1.0;
    w.y =  3.0*x33 - 6.0*x22       + 4.0;
    w.z = -3.0*x33 + 3.0*x22 + 3.0*x + 1.0;
    w.w =  x33;
    return w / 6.0;
}
vec4 textureLinear (sampler2D sampler, vec2 pixel) {
    // Nearest sampling:
    // Software bilinear sampling
    vec2 texSize = vec2(textureSize(sampler, 0));
    pixel*=texSize;
    float fracu = fract(pixel.x);
    float fracv = fract(pixel.y);
    vec2 floorPixel = floor(pixel) + vec2(0.5,0.5);
    vec4 A = texture(sampler, (floorPixel / (texSize)));
    vec4 B = texture(sampler, ((floorPixel+vec2(1.0,0.0)) / (texSize)));
    vec4 C = texture(sampler, ((floorPixel+vec2(0.0,1.0)) / (texSize)));
    vec4 D = texture(sampler, ((floorPixel+vec2(1.0,1.0)) / (texSize)));
    return mix(mix(A,B,fracu), mix(C,D,fracu), fracv);
}
vec4 mix4(vec4 inp[4],vec2 mixing){
    return mix(mix(inp[0],inp[1],mixing.x), mix(inp[2],inp[3],mixing.x), mixing.y);
}
vec4 textureLinear1D (sampler2D sampler, vec2 pixel) {
    // Nearest sampling:
    // Software linear sampling
    vec2 texSize = vec2(textureSize(sampler, 0));
    pixel*=texSize;
    float fracu = fract(pixel.x);
    vec2 floorPixel = floor(pixel) + vec2(0.5,0.0);
    vec4 A = texture(sampler, (floorPixel / (texSize)));
    vec4 B = texture(sampler, ((floorPixel+vec2(1.0,0.0)) / (texSize)));
    return mix(A,B,fracu);
}
vec4 textureCubic(sampler2D sampler, vec2 texCoords){
    // Nearest sampling:
    // Software bicubic sampling
    vec2 texSize = vec2(textureSize(sampler, 0));
    vec2 invTexSize = 1.0 / texSize;
    texCoords = texCoords * texSize - 0.5;
    vec2 fxy = fract(texCoords);
    texCoords -= fxy;
    vec4 xcubic = cubic(fxy.x);
    vec4 ycubic = cubic(fxy.y);
    vec4 c = texCoords.xxyy + vec2 (-0.5, +1.5).xyxy;
    vec4 s = vec4(xcubic.xz + xcubic.yw, ycubic.xz + ycubic.yw);
    vec4 offset = c + vec4 (xcubic.yw, ycubic.yw) / s;
    offset *= invTexSize.xxyy;
    vec4 sample2 = textureLinear1D(sampler, offset.xw);
    vec4 sample3 = textureLinear1D(sampler, offset.yw);
    float sx = s.x / (s.x + s.y);
    return mix(sample3, sample2, sx);
}
vec4 textureCubicHardware(sampler2D sampler, vec2 texCoords){
    // Nearest sampling:
    // Software bicubic sampling
    vec2 texSize = vec2(textureSize(sampler, 0));
    vec2 invTexSize = 1.0 / texSize;
    texCoords = texCoords * texSize - 0.5;
    vec2 fxy = fract(texCoords);
    texCoords -= fxy;
    vec4 xcubic = cubic(fxy.x);
    vec4 ycubic = cubic(fxy.y);
    vec4 c = texCoords.xxyy + vec2 (-0.5, +1.5).xyxy;
    vec4 s = vec4(xcubic.xz + xcubic.yw, ycubic.xz + ycubic.yw);
    vec4 offset = c + vec4 (xcubic.yw, ycubic.yw) / s;
    offset *= invTexSize.xxyy;
    vec4 sample2 = texture(sampler, offset.xw);
    vec4 sample3 = texture(sampler, offset.yw);
    float sx = s.x / (s.x + s.y);
    return mix(sample3, sample2, sx);
}
vec4 textureBicubic(sampler2D sampler, vec2 texCoords){
    // Nearest sampling:
    // Software bicubic sampling
    vec2 texSize = vec2(textureSize(sampler, 0));
    vec2 invTexSize = 1.0 / texSize;
    texCoords = texCoords * texSize - 0.5;
    vec2 fxy = fract(texCoords);
    texCoords -= fxy;
    vec4 xcubic = cubic(fxy.x);
    vec4 ycubic = cubic(fxy.y);
    vec4 c = texCoords.xxyy + vec2 (-0.5, +1.5).xyxy;
    vec4 s = vec4(xcubic.xz + xcubic.yw, ycubic.xz + ycubic.yw);
    vec4 offset = c + vec4 (xcubic.yw, ycubic.yw) / s;
    offset *= invTexSize.xxyy;
    vec4 sample0 = textureLinear(sampler, offset.xz);
    vec4 sample1 = textureLinear(sampler, offset.yz);
    vec4 sample2 = textureLinear(sampler, offset.xw);
    vec4 sample3 = textureLinear(sampler, offset.yw);
    float sx = s.x / (s.x + s.y);
    float sy = s.z / (s.z + s.w);
    return mix(
    mix(sample3, sample2, sx), mix(sample1, sample0, sx)
    , sy);
}

vec4 textureBicubicHardware(sampler2D sampler, vec2 texCoords){
    // Linear sampling:
    // Software bicubic sampling with hardware acceleration
    vec2 texSize = vec2(textureSize(sampler, 0));
    vec2 invTexSize = 1.0 / texSize;
    texCoords = texCoords * texSize - 0.5;
    vec2 fxy = fract(texCoords);
    texCoords -= fxy;
    vec4 xcubic = cubic(fxy.x);
    vec4 ycubic = cubic(fxy.y);
    vec4 c = texCoords.xxyy + vec2 (-0.5, +1.5).xyxy;
    vec4 s = vec4(xcubic.xz + xcubic.yw, ycubic.xz + ycubic.yw);
    vec4 offset = c + vec4 (xcubic.yw, ycubic.yw) / s;
    offset *= invTexSize.xxyy;
    vec4 sample0 = texture(sampler, offset.xz);
    vec4 sample1 = texture(sampler, offset.yz);
    vec4 sample2 = texture(sampler, offset.xw);
    vec4 sample3 = texture(sampler, offset.yw);
    float sx = s.x / (s.x + s.y);
    float sy = s.z / (s.z + s.w);
    return mix(
    mix(sample3, sample2, sx), mix(sample1, sample0, sx)
    , sy);
}