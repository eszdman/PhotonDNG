package com.particlesdevs.photoncamera.processing.opengl.postpipeline;

import com.particlesdevs.photoncamera.processing.opengl.GLTexture;
import com.particlesdevs.photoncamera.processing.opengl.nodes.Node;

public class GlobalToneMapping extends Node {
    public GlobalToneMapping() {
        super(0, "GlobalToneMapping");
    }
    float intenseHigher = -0.020f;
    float intenseLower = -0.090f;
    @Override
    public void Compile() {}
    @Override
    public void Run() {
        GLTexture lowRes0 = glUtils.interpolate(previousNode.WorkingTexture,1.0/8.0);
        GLTexture lowRes = glUtils.interpolate(lowRes0,1.0/8.0);
        glProg.useAssetProgram("globaltonemaping",false);
        glProg.setTexture("InputBuffer",previousNode.WorkingTexture);
        glProg.setTexture("LowRes",lowRes);
        glProg.setVar("insize",previousNode.WorkingTexture.mSize);
        glProg.setVar("lowsize",lowRes.mSize.x,lowRes.mSize.y);
        glProg.setVar("str",intenseHigher);
        GLTexture out1 = basePipeline.getMain();
        glProg.drawBlocks(out1);
        glProg.close();
        GLTexture lowRes2 = glUtils.interpolate(lowRes,1.0/8.0);
        lowRes.close();
        lowRes0.close();
        WorkingTexture = basePipeline.getMain();
        glProg.useAssetProgram("globaltonemaping",false);
        glProg.setTexture("InputBuffer",out1);
        glProg.setTexture("LowRes",lowRes2);
        glProg.setVar("insize",previousNode.WorkingTexture.mSize);
        glProg.setVar("lowsize",lowRes2.mSize.x,lowRes2.mSize.y);
        glProg.setVar("str",intenseLower);
        glProg.drawBlocks(WorkingTexture);
        glProg.closed = true;
        lowRes2.close();
    }
}
