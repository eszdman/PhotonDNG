/*
3x3 Median
Morgan McGuire and Kyle Whitson
http://graphics.cs.williams.edu


Copyright (c) Morgan McGuire and Williams College, 2006
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

#define s2(a, b)				temp = a; a = min(a, b); b = max(temp, b);
#define mn3(a, b, c)			s2(a, b); s2(a, c);
#define mx3(a, b, c)			s2(b, c); s2(a, c);

#define mnmx3(a, b, c)			mx3(a, b, c); s2(a, b);                                   // 3 exchanges
#define mnmx4(a, b, c, d)		s2(a, b); s2(c, d); s2(a, c); s2(b, d);                   // 4 exchanges
#define mnmx5(a, b, c, d, e)	s2(a, b); s2(c, d); mn3(a, c, e); mx3(b, d, e);           // 6 exchanges
#define mnmx6(a, b, c, d, e, f) s2(a, d); s2(b, e); s2(c, f); mn3(a, b, c); mx3(d, e, f); // 7 exchanges


vec4 median9(vec4 v[9]) {
    vec4 temp;
    // Starting with a subset of size 6, remove the min and max each time
    mnmx6(v[0], v[1], v[2], v[3], v[4], v[5]);
    mnmx5(v[1], v[2], v[3], v[4], v[6]);
    mnmx4(v[2], v[3], v[4], v[7]);
    mnmx3(v[3], v[4], v[8]);
    return v[4];
}
vec3 median9(vec3 v[9]) {
    vec3 temp;
    // Starting with a subset of size 6, remove the min and max each time
    mnmx6(v[0], v[1], v[2], v[3], v[4], v[5]);
    mnmx5(v[1], v[2], v[3], v[4], v[6]);
    mnmx4(v[2], v[3], v[4], v[7]);
    mnmx3(v[3], v[4], v[8]);
    return v[4];
}
vec2 median9(vec2 v[9]) {
    vec2 temp;
    // Starting with a subset of size 6, remove the min and max each time
    mnmx6(v[0], v[1], v[2], v[3], v[4], v[5]);
    mnmx5(v[1], v[2], v[3], v[4], v[6]);
    mnmx4(v[2], v[3], v[4], v[7]);
    mnmx3(v[3], v[4], v[8]);
    return v[4];
}
float median9(float v[9]) {
    float temp;
    // Starting with a subset of size 6, remove the min and max each time
    mnmx6(v[0], v[1], v[2], v[3], v[4], v[5]);
    mnmx5(v[1], v[2], v[3], v[4], v[6]);
    mnmx4(v[2], v[3], v[4], v[7]);
    mnmx3(v[3], v[4], v[8]);
    return v[4];
}
uvec2 median9(uvec2 v[9]) {
    uvec2 temp;
    // Starting with a subset of size 6, remove the min and max each time
    mnmx6(v[0], v[1], v[2], v[3], v[4], v[5]);
    mnmx5(v[1], v[2], v[3], v[4], v[6]);
    mnmx4(v[2], v[3], v[4], v[7]);
    mnmx3(v[3], v[4], v[8]);
    return v[4];
}
ivec2 median9(ivec2 v[9]) {
    ivec2 temp;
    // Starting with a subset of size 6, remove the min and max each time
    mnmx6(v[0], v[1], v[2], v[3], v[4], v[5]);
    mnmx5(v[1], v[2], v[3], v[4], v[6]);
    mnmx4(v[2], v[3], v[4], v[7]);
    mnmx3(v[3], v[4], v[8]);
    return v[4];
}
vec4 median7(vec4 v[7]) {
    vec4 temp;
    // Starting with a subset of size 6, remove the min and max each time
    mnmx5(v[0], v[1], v[2], v[3], v[4]);
    mnmx4(v[2], v[3], v[4], v[5]);
    mnmx3(v[3], v[4], v[6]);
    return v[4];
}
float median5(float v[5]) {
    float temp;
    mnmx4(v[0], v[1], v[2], v[3]);
    mnmx3(v[1], v[2], v[4]);
    return v[2];
}
vec2 median5(vec2 v[5]) {
    vec2 temp;
    mnmx4(v[0], v[1], v[2], v[3]);
    mnmx3(v[1], v[2], v[4]);
    return v[2];
}
vec3 median5(vec3 v[5]) {
    vec3 temp;
    mnmx4(v[0], v[1], v[2], v[3]);
    mnmx3(v[1], v[2], v[4]);
    return v[2];
}
ivec2 median5(ivec2 v[5]) {
    ivec2 temp;
    mnmx4(v[0], v[1], v[2], v[3]);
    mnmx3(v[1], v[2], v[4]);
    return v[2];
}
uvec2 median5(uvec2 v[5]) {
    uvec2 temp;
    mnmx4(v[0], v[1], v[2], v[3]);
    mnmx3(v[1], v[2], v[4]);
    return v[2];
}
uvec4 median5(uvec4 v[5]) {
    uvec4 temp;
    mnmx4(v[0], v[1], v[2], v[3]);
    mnmx3(v[1], v[2], v[4]);
    return v[2];
}
float median4(float v[4]) {
    float temp;
    mnmx4(v[0], v[1], v[2],v[3]);
    return v[1];
}