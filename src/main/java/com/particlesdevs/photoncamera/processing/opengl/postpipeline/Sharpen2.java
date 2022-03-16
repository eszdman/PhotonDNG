package com.particlesdevs.photoncamera.processing.opengl.postpipeline;


import com.particlesdevs.photoncamera.processing.opengl.nodes.Node;
public class Sharpen2 extends Node {
    public Sharpen2() {
        super(0, "Sharpening");
    }

    @Override
    public void Compile() {
    }
    float blurSize = 0.20f;
    float sharpSize = 1.5f;
    float sharpMin = 0.5f;
    float sharpMax = 1.f;
    float denoiseActivity = 1.f;
    @Override
    public void Run() {
        denoiseActivity = getTuning("DenoiseActivity",denoiseActivity);
        blurSize = getTuning("BlurSize", blurSize);
        sharpSize = getTuning("SharpSize", sharpSize);
        sharpMin = getTuning("SharpMin",sharpMin);
        sharpMax = getTuning("SharpMax",sharpMax);
        glProg.setDefine("INTENSE",denoiseActivity);
        glProg.setDefine("INSIZE",basePipeline.mParameters.rawSize);
        glProg.setDefine("SHARPSIZE",sharpSize);
        glProg.setDefine("SHARPMIN",sharpMin);
        glProg.setDefine("SHARPMAX",sharpMax);
        glProg.setDefine("NOISES",basePipeline.noiseS);
        glProg.setDefine("NOISEO",basePipeline.noiseO);
        glProg.useAssetProgram("lsharpening2",false);
        glProg.setVar("size", sharpSize);
        glProg.setVar("strength", 0.3f);
        glProg.setTexture("InputBuffer", previousNode.WorkingTexture);
        glProg.setTexture("BlurBuffer",previousNode.WorkingTexture);
        WorkingTexture = basePipeline.getMain();
        glProg.drawBlocks(WorkingTexture);
        glProg.closed = true;
    }
}
