package com.particlesdevs.photoncamera.processing.opengl.rawpipeline;

import com.particlesdevs.photoncamera.processing.opengl.GLFormat;
import com.particlesdevs.photoncamera.processing.opengl.GLTexture;
import com.particlesdevs.photoncamera.processing.opengl.nodes.Node;
import dngCamera.PhotonCamera;

import java.util.ArrayList;

public class AlignAndMerge extends Node {
    public AlignAndMerge() {
        super("", "AlignAndMerge");
    }
    @Override
    public void Compile() {}
    private void CorrectedRaw(GLTexture out, int number) {
        float bl = Math.min(Math.min(Math.min(basePipeline.mParameters.blackLevel[0],basePipeline.mParameters.blackLevel[1]),
                basePipeline.mParameters.blackLevel[2]),basePipeline.mParameters.blackLevel[3]);
        //glProg.setDefine("BL",bl);
        glProg.useAssetProgram("precorrection");
        GLTexture inraw = new GLTexture(basePipeline.mParameters.rawSize, new GLFormat(GLFormat.DataType.UNSIGNED_16), images.get(number).buffer);
        glProg.setTexture("InputBuffer",inraw);
        glProg.setVar("WhiteLevel",(float)basePipeline.mParameters.whiteLevel);
        glProg.drawBlocks(out);
        inraw.close();
    }
    ArrayList<ImageFrame> images;
}
