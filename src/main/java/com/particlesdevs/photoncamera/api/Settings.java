package com.particlesdevs.photoncamera.api;

public class Settings {
    private final String TAG = "Settings";
    //Preferences
    public int frameCount;
    public int lumenCount;
    public int chromaCount;
    public boolean enhancedProcess;
    public boolean watermark;
    public boolean energySaving;
    public boolean DebugData;
    public boolean roundEdge;
    public boolean align;
    public boolean hdrx;
    public boolean hdrxNR;
    public double exposureCompensation;
    public double saturation = 1.0;
    public double sharpness;
    public double contrastMpy = 1.0;
    public int contrastConst = 0;//TODO
    public double noiseRstr = 1.0;
    public double mergeStrength;
    public double compressor;
    public double gain;
    public double shadows;
    public boolean rawSaver;
    public boolean QuadBayer;
    public int cfaPattern;
    public int theme;
    public boolean remosaic;//TODO
    public boolean eisPhoto;
    public boolean fpsPreview;
    public int alignAlgorithm;
    public String mCameraID;
    public float[] toneMap;
    public float[] gamma;

    //Camera direct related
    //public int noiseReduction = NOISE_REDUCTION_MODE_OFF;
    //public CameraMode selectedMode;


}
