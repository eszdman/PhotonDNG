package com.particlesdevs.photoncamera.processing.opengl.rawpipeline;

import com.particlesdevs.photoncamera.processing.opengl.*;
import com.particlesdevs.photoncamera.processing.render.Parameters;
import dngCamera.PhotonCamera;

import java.awt.*;
import java.nio.ByteBuffer;

public class RawPipeline  extends GLBasePipeline {
    public ByteBuffer Run(ByteBuffer inBuffer, Parameters parameters){
        mParameters = parameters;
        mSettings = PhotonCamera.getSettings();
        workSize = new Point(mParameters.rawSize.x,mParameters.rawSize.y);
        GLCoreBlockProcessing glproc = new GLCoreBlockProcessing(mParameters.rawSize, new GLFormat(GLFormat.DataType.UNSIGNED_16));
        glint = new GLInterface(glproc);
        glint.parameters = mParameters;
        add(new AlignAndMerge());
        return runAllRaw();
    }
}
