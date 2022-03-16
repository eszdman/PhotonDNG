

precision highp float;
precision highp sampler2D;

uniform sampler2D upsampled;
uniform bool useUpsampled;

// Weighting is done using these.
uniform sampler2D normalExpo;
uniform sampler2D highExpo;

// Blending is done using these.
uniform sampler2D normalExpoDiff;
uniform sampler2D highExpoDiff;

uniform ivec2 upscaleIn;

out float result;

#import gaussian
#import interpolation
float laplace(sampler2D tex, float mid, ivec2 xyCenter) {
    float left = texelFetch(tex, xyCenter - ivec2(1, 0), 0).r,
    right = texelFetch(tex, xyCenter + ivec2(1, 0), 0).r,
    top = texelFetch(tex, xyCenter - ivec2(0, 1), 0).r,
    bottom = texelFetch(tex, xyCenter + ivec2(0, 1), 0).r;

    return distance(4.f * mid, left + right + top + bottom);
}

void main() {
    ivec2 xyCenter = ivec2(gl_FragCoord.xy);

    // If this is the lowest layer, start with zero.
    //if(useUpsampled == 2) mpy = 2.0;
    float base = (useUpsampled)
    //? texelFetch(upsampled, xyCenter, 0).xyz
    ? textureBicubic(upsampled, vec2(gl_FragCoord.xy)/vec2(upscaleIn)).r*10.0
    : float(1.);
    // How are we going to blend these two?
    float normal = 0.3;
    float high = 3.0;

    // To know that, look at multiple factors.
    vec2 midNormal = texelFetch(normalExpo, xyCenter, 0).rg;
    vec2 midHigh = texelFetch(highExpo, xyCenter, 0).rg;

    float normalWeight = 1000.;
    float highWeight = 1000.;

    // Factor 1: Well-exposedness.

    float midNormalToAvg = sqrt(unscaledGaussian(midNormal.r - 0.35, 0.50));
    float midHighToAvg = sqrt(unscaledGaussian(midHigh.r - 0.35, 0.50));

    normalWeight *= midNormalToAvg;
    highWeight *= midHighToAvg;

    // Factor 2: Contrast.
    float laplaceNormal = laplace(normalExpo, midNormal.r, xyCenter);
    float laplaceHigh = laplace(highExpo, midHigh.r, xyCenter);

    normalWeight *= sqrt(laplaceNormal + 0.01);
    highWeight *= sqrt(laplaceHigh + 0.01);

    // Factor 3: Saturation.
    float normalStddev = midNormal.g;
    float highStddev = midHigh.g;

    normalWeight *= sqrt(normalStddev + 0.01);
    highWeight *= sqrt(highStddev + 0.01);

    float blend = highWeight / (normalWeight + highWeight); // [0, 1]
    result = (base + mix(normal, high, blend))/2.0;
    result/=10.0;
}
