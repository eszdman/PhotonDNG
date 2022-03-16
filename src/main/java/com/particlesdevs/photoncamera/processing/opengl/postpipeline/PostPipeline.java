package com.particlesdevs.photoncamera.processing.opengl.postpipeline;

import com.particlesdevs.photoncamera.processing.opengl.*;
import com.particlesdevs.photoncamera.processing.render.NoiseModeler;
import com.particlesdevs.photoncamera.processing.render.Parameters;
import dngCamera.PhotonCamera;
import util.Log.Log;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class PostPipeline extends GLBasePipeline {
    public ByteBuffer stackFrame;
    public ByteBuffer lowFrame;
    public ByteBuffer highFrame;
    public GLTexture FusionMap;
    public GLTexture GainMap;
    public ArrayList<GLImage> debugData = new ArrayList<>();
    //public ArrayList<ImageFrame> SAGAIN;
    public float[] analyzedBL = new float[]{0.f,0.f,0.f};;
    float regenerationSense = 1.f;
    float totalGain = 1.f;
    float AecCorr = 1.f;
    float fusionGain = 1.f;
    float softLight = 1.f;
    public int getRotation() {
        int rotation = mParameters.cameraRotation;
        String TAG = "ParseExif";
        return rotation;
    }
    @SuppressWarnings("SuspiciousNameCombination")
    private Point getRotatedCoords(Point in){
        switch (getRotation()){
            case 0:
            case 180:
                return in;
            case 90:
            case 270:
                return new Point(in.y,in.x);
        }
        return in;
    }
    float constShift = 0.0f;
    public GLImage Run(ByteBuffer inBuffer, Parameters parameters){
        mParameters = parameters;
        mSettings = PhotonCamera.getSettings();
        workSize = new Point(mParameters.rawSize.x,mParameters.rawSize.y);
        NoiseModeler modeler = mParameters.noiseModeler;
        noiseS = modeler.computeModel[0].first.floatValue()+
                modeler.computeModel[1].first.floatValue()+
                modeler.computeModel[2].first.floatValue();
        noiseO = modeler.computeModel[0].second.floatValue()+
                modeler.computeModel[1].second.floatValue()+
                modeler.computeModel[2].second.floatValue();
        noiseS/=3.f;
        noiseO/=3.f;
        double noisempy = Math.pow(2.0,mSettings.noiseRstr+constShift);
        Log.d("PostPipeline","noisempy:"+noisempy);
        noiseS*=noisempy;
        noiseO*=noisempy;
        Log.d("PostPipeline","NoiseS:"+noiseS+"\n"+"NoiseO:"+noiseO);
        /*if(!PhotonCamera.getSettings().hdrxNR){
            noiseO = 0.f;
            noiseS = 0.f;
        }*/
        noiseO = Math.max(noiseO,Float.MIN_NORMAL);

        Point rotated = getRotatedCoords(parameters.rawSize);
        GLDrawParams.TileSize = 256;
        /*if (PhotonCamera.getSettings().selectedMode == CameraMode.NIGHT) {
            rotated.x/=2;
            rotated.y/=2;
        }*/
        GLFormat format = new GLFormat(GLFormat.DataType.UNSIGNED_8,4);
        GLImage output = new GLImage(new Point(rotated.x,rotated.y), format);

        GLCoreBlockProcessing glproc = new GLCoreBlockProcessing(rotated,output, format);
        glint = new GLInterface(glproc);
        stackFrame = inBuffer;
        glint.parameters = parameters;

        BuildDefaultPipeline();

        return runAll();
    }
    private void BuildDefaultPipeline(){

        add(new Bayer2Float());
        //add(new ExposureFusionBayer2());
        switch (PhotonCamera.getSettings().cfaPattern) {
            case -2 -> {
                add(new DemosaicQUAD());
            }
            case 4 -> {
                add(new MonoDemosaic());
            }
            default -> {
                //if(nightMode)
                //    add(new HotPixelFilter());
                //if(PhotonCamera.getSettings().hdrxNR) {
                //add(new ESD3DBayerCS());
                //}
                add(new Demosaic2());

                //add(new ImpulsePixelFilter());
            }
        }
        /*
         * * * All filters after demosaicing * * *
         */

        //if(PhotonCamera.getSettings().hdrxNR) {
            //if(nightMode)
            //    add(new Wavelet());
            //add(new ESD3D());
        //}
        //add(new AWB());

        add(new Initial());

        //add(new Equalization());

        add(new GlobalToneMapping());

        add(new CaptureSharpening());

        add(new CorrectingFlow());

        //add(new ChromaticFlow());

        add(new Sharpen2());

        add(new RotateWatermark(getRotation()));
    }
}
