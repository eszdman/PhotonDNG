package com.particlesdevs.photoncamera.processing.opengl.postpipeline;

import com.particlesdevs.photoncamera.processing.opengl.GLImage;
import com.particlesdevs.photoncamera.processing.opengl.GLTexture;
import com.particlesdevs.photoncamera.processing.opengl.nodes.Node;
import dngCamera.PhotonCamera;
import util.Log.Log;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL43.*;

public class RotateWatermark extends Node {
    private int rotate;
    private boolean watermarkNeeded;
    private GLImage watermark;
    public RotateWatermark(int rotation) {
        super(0, "Rotate");
        rotate = rotation;
        watermarkNeeded = true;
    }

    @Override
    public void Compile() {}

    @Override
    public void AfterRun() {
        if(watermark != null) watermark.close();
    }

    @Override
    public void Run() {

        //else lutbm = BitmapFactory.decodeResource(PhotonCamera.getResourcesStatic(), R.drawable.neutral_lut);
        glProg.setDefine("WATERMARK",watermarkNeeded);
        glProg.useAssetProgram("addwatermark_rotate",false);
        InputStream image;
        image = PhotonCamera.getAssetLoader().getInputStream("watermark\\photoncamera_watermark.png");
        watermark = new GLImage(image);
        glProg.setTexture("Watermark", new GLTexture(watermark,GL_LINEAR,GL_CLAMP_TO_EDGE,0));

        glProg.setTexture("InputBuffer", previousNode.WorkingTexture);
        int rot = -1;
        Log.d(Name,"Rotation:"+rotate);
        switch (rotate){
            case 0:
                //WorkingTexture = new GLTexture(size.x,size.y, previousNode.WorkingTexture.mFormat, null);
                rot = 0;
                break;
            case 90:
                //WorkingTexture = new GLTexture(size.y,size.x, previousNode.WorkingTexture.mFormat, null);
                rot = 3;
                break;
            case 180:
                //WorkingTexture = new GLTexture(size, previousNode.WorkingTexture.mFormat, null);
                rot = 2;
                break;
            case 270:
                //WorkingTexture = new GLTexture(size.y,size.x, previousNode.WorkingTexture.mFormat, null);
                rot = 1;
                break;
        }
        Log.d(Name,"selected rotation:"+rot);
        glProg.setVar("rotate",rot);
    }
}
