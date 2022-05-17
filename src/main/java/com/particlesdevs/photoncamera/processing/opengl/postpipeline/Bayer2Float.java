package com.particlesdevs.photoncamera.processing.opengl.postpipeline;

import com.particlesdevs.photoncamera.processing.opengl.GLDrawParams;
import com.particlesdevs.photoncamera.processing.opengl.GLFormat;
import com.particlesdevs.photoncamera.processing.opengl.GLInterface;
import com.particlesdevs.photoncamera.processing.opengl.GLTexture;
import com.particlesdevs.photoncamera.processing.opengl.nodes.Node;
import util.BufferUtils;
import util.Log.Log;

import java.awt.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL43.*;

public class Bayer2Float extends Node {

    public Bayer2Float() {
        super(0, "Bayer2Float");
    }

    @Override
    public void Compile() {}

    @Override
    public void Run() {
        PostPipeline postPipeline = (PostPipeline)basePipeline;
        GLTexture in = new GLTexture(basePipeline.mParameters.rawSize, new GLFormat(GLFormat.DataType.SIMPLE_16), ((PostPipeline)(basePipeline)).stackFrame);
        GLTexture GainMapTex = new GLTexture(basePipeline.mParameters.mapSize, new GLFormat(GLFormat.DataType.FLOAT_16,4),
                BufferUtils.getFrom(basePipeline.mParameters.gainMap),GL_LINEAR,GL_CLAMP_TO_EDGE);
        Log.d(Name,"whitelevel:"+basePipeline.mParameters.whiteLevel);
        glProg.setDefine("AGAIN",65535.f/(basePipeline.mParameters.whiteLevel));
        glProg.setDefine("QUAD", basePipeline.mSettings.cfaPattern == -2);
        glProg.useAssetProgram("tofloat",false);
        glProg.setTexture("InputBuffer",in);
        glProg.setVar("CfaPattern",basePipeline.mParameters.cfaPattern);
        glProg.setVar("patSize",2);
        glProg.setVar("whitePoint",basePipeline.mParameters.whitePoint);
        glProg.setVar("RawSize",basePipeline.mParameters.rawSize);

        glProg.setTexture("GainMap",GainMapTex);
        for(int i =0; i<4;i++){
            basePipeline.mParameters.blackLevel[i]/=basePipeline.mParameters.whiteLevel*postPipeline.regenerationSense;
        }
        glProg.setVar("blackLevel",basePipeline.mParameters.blackLevel);
        Log.d(Name,"CfaPattern:"+basePipeline.mParameters.cfaPattern);
        postPipeline.regenerationSense = 10.f;
        int minimal = -1;
        for(int i =0; i<basePipeline.mParameters.whitePoint.length;i++){
            if(i == 1) continue;
            if(basePipeline.mParameters.whitePoint[i] < postPipeline.regenerationSense){
                postPipeline.regenerationSense = basePipeline.mParameters.whitePoint[i];
                minimal = i;
            }
        }
        if(basePipeline.mParameters.cfaPattern == 4) postPipeline.regenerationSense = 1.f;
        postPipeline.regenerationSense = 1.f/postPipeline.regenerationSense;
        postPipeline.regenerationSense = 1.f;
        Log.d(Name,"Regeneration:"+postPipeline.regenerationSense);
        glProg.setVar("Regeneration",postPipeline.regenerationSense);
        glProg.setVar("MinimalInd",minimal);
        Point wsize = new Point(basePipeline.mParameters.rawSize);
        basePipeline.main2 = new GLTexture(wsize, new GLFormat(GLFormat.DataType.FLOAT_16, GLDrawParams.WorkDim));
        WorkingTexture = basePipeline.main2;
        glProg.drawBlocks(WorkingTexture);
        basePipeline.main1 = new GLTexture(wsize, new GLFormat(GLFormat.DataType.FLOAT_16, GLDrawParams.WorkDim));
        basePipeline.main3 = new GLTexture(wsize, new GLFormat(GLFormat.DataType.FLOAT_16, GLDrawParams.WorkDim));
        ((PostPipeline)basePipeline).GainMap = GainMapTex;
        glProg.closed = true;
        in.close();
        GainMapTex.close();
    }
}
