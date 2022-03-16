package com.particlesdevs.photoncamera.processing.render;

import data.Pair;
import util.Log.Log;
import data.Rect;
import util.os.Environment;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Parameters {
    private static final String TAG = "Parameters";
    public int analogIso;
    public byte cfaPattern;
    public Point rawSize;
    public boolean usedDynamic = false;
    public float[] blackLevel = new float[4];
    public float[] whitePoint = new float[3];
    public int whiteLevel = 1023;
    public int realWL = -1;
    public boolean hasGainMap;
    public Point mapSize;
    public Rect sensorPix;
    public float[] gainMap;
    public float[] proPhotoToSRGB = new float[9];
    public float[] sensorToProPhoto = new float[9];
    public float tonemapStrength = 1.4f;
    public float[] customTonemap;
    public Point[] hotPixels;
    public float focalLength;
    public int cameraRotation;
    public NoiseModeler noiseModeler;
    public ColorCorrectionTransform CCT;
    //public SizeF sensorSize;
    public double angleX;
    public double angleY;
    public double perXAngle;
    public double perYAngle;
    public double XPerMm;
    public double YPerMm;
    public double[] cameraIntrinsic = new double[9];
    public double[] cameraIntrinsicRev = new double[9];
    public float[][] tonemapCurves = new float[3][];
    public float gammaCurve = 2.0f;
    public SpecificSettingSensor sensorSpecifics;

    //WB TEMP PARAMETERS
    public int referenceIlluminant1,referenceIlluminant2;
    public float[][] calibrationTransform1 = new float[3][3];
    public float[][] calibrationTransform2 = new float[3][3];
    public float[][] colorMatrix1 = new float[3][3];
    public float[][] colorMatrix2 = new float[3][3];
    public float[][] forwardTransform1 = new float[3][3];
    public float[][] forwardTransform2 = new float[3][3];

    //Noise model parameters
    public Pair<Double,Double>[] NoiseModel;
    public int iso;


    public void FillConstParameters(Point size) {
        rawSize = size;

        hasGainMap = false;
        mapSize = new Point(1, 1);
        gainMap = new float[4];
        gainMap[0] = 1.f;
        gainMap[1] = 1.f;
        gainMap[2] = 1.f;
        gainMap[3] = 1.f;
        //sensorPix = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        if (sensorPix == null) {
            sensorPix = new Rect(0, 0, rawSize.x, rawSize.y);
        }
        //hotPixels = PhotonCamera.getCameraFragment().mHotPixelMap;
    }
    public void FillDynamicParameters() {
        //sensorSpecifics = PhotonCamera.getSpecificSensor().selectedSensorSpecifics;
        noiseModeler = new NoiseModeler( NoiseModel,analogIso,iso,cfaPattern,sensorSpecifics);
        int[] blarr = new int[4];
        //BlackLevelPattern level = CaptureController.mCameraCharacteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN);
        //if (result != null) {
            //boolean isHuawei = Build.BRAND.equals("Huawei");

            /*float[] dynbl = result.get(CaptureResult.SENSOR_DYNAMIC_BLACK_LEVEL);
            if (dynbl != null) {
                System.arraycopy(dynbl, 0, blackLevel, 0, 4);
                usedDynamic = true;
            }*/

//            LensShadingMap lensMap = result.get(CaptureResult.STATISTICS_LENS_SHADING_CORRECTION_MAP);
//            if (lensMap != null) {
//                gainMap = new float[lensMap.getGainFactorCount()];
//                mapSize = new Point(lensMap.getColumnCount(), lensMap.getRowCount());
//                lensMap.copyGainFactors(gainMap, 0);
//                hasGainMap = true;
//                if ((gainMap[(gainMap.length / 8) - (gainMap.length / 8) % 4]) == 1.0 &&
//                        (gainMap[(gainMap.length / 2) - (gainMap.length / 2) % 4]) == 1.0 &&
//                        (gainMap[(gainMap.length / 2 + gainMap.length / 8) - (gainMap.length / 2 + gainMap.length / 8) % 4]) == 1.0) {
//                    hasGainMap = false;
//                    if(isHuawei) {
//                        Log.d(TAG, "DETECTED FAKE GAINMAP, REPLACING WITH STATIC GAINMAP");
//                        gainMap = new float[Const.gainMap.length];
//                        for (int i = 0; i < Const.gainMap.length; i += 4) {
//                            float in = (float) Const.gainMap[i] + (float) Const.gainMap[i + 1] + (float) Const.gainMap[i + 2] + (float) Const.gainMap[i + 3];
//                            in /= 4.f;
//                            gainMap[i] = in;
//                            gainMap[i + 1] = in;
//                            gainMap[i + 2] = in;
//                            gainMap[i + 3] = in;
//                        }
//                        mapSize = Const.mapSize;
//                    }
//                }
//            }
            ReCalcColor();
        //}
    }


    public float[] customNeutral;

    public void ReCalcColor() {
        //CameraCharacteristics characteristics = CaptureController.mCameraCharacteristics;
        //Rational[] neutralR = result.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT);
        //if (!customNeutr)
        //    for (int i = 0; i < neutralR.length; i++) {
        //        whitePoint[i] = neutralR[i].floatValue();
        //    }
        //else {
        //    whitePoint = customNeutral;
        //}

        /*
        if(sensorSpecifics.CCTExists){
            if(sensorSpecifics.CalibrationTransform1 != null){
                calibrationTransform1 = sensorSpecifics.CalibrationTransform1;
            }
            if(sensorSpecifics.CalibrationTransform2 != null){
                calibrationTransform2 = sensorSpecifics.CalibrationTransform2;
            }

            if(sensorSpecifics.ColorTransform1 != null){
                colorMatrix1 = sensorSpecifics.ColorTransform1;
            }
            if(sensorSpecifics.ColorTransform2 != null){
                colorMatrix2 = sensorSpecifics.ColorTransform2;
            }

            if(sensorSpecifics.ColorTransform2 != null){
                forwardTransform1 = sensorSpecifics.ColorTransform2;
            }
            if(sensorSpecifics.ForwardMatrix2 != null){
                forwardTransform2 = sensorSpecifics.ForwardMatrix2;
            }
            if(sensorSpecifics.referenceIlluminant1 != -1){
                referenceIlluminant1 = sensorSpecifics.referenceIlluminant1;
            }
            if(sensorSpecifics.referenceIlluminant2 != -1){
                referenceIlluminant2 = sensorSpecifics.referenceIlluminant2;
            }

        }

         */

        float[] normalizedCalibrationTransform1 = new float[9];
        float[] normalizedCalibrationTransform2 = new float[9];
        float[] normalizedForwardTransform1 = new float[9];
        float[] normalizedColorMatrix1 = new float[9];
        float[] normalizedColorMatrix2 = new float[9];
        float[] normalizedForwardTransform2 = new float[9];

        Converter.convertColorspaceSpecific(calibrationTransform1, normalizedCalibrationTransform1);
        Converter.convertColorspaceSpecific(calibrationTransform2, normalizedCalibrationTransform2);
        Converter.convertColorspaceSpecific(forwardTransform1, normalizedForwardTransform1);
        Converter.convertColorspaceSpecific(forwardTransform2, normalizedForwardTransform2);
        Converter.convertColorspaceSpecific(colorMatrix1, normalizedColorMatrix1);
        Converter.convertColorspaceSpecific(colorMatrix2, normalizedColorMatrix2);

        Converter.normalizeFM(normalizedForwardTransform1);
        Converter.normalizeFM(normalizedForwardTransform2);

        Converter.normalizeFM(normalizedColorMatrix1);
        Converter.normalizeFM(normalizedColorMatrix2);
        float[] sensorToXYZ = new float[9];

        double interpolationFactor = Converter.findDngInterpolationFactor(referenceIlluminant1,
                referenceIlluminant2, normalizedCalibrationTransform1, normalizedCalibrationTransform2,
                normalizedColorMatrix1, normalizedColorMatrix2, whitePoint);
        Converter.calculateCameraToXYZD50Transform(normalizedForwardTransform1, normalizedForwardTransform2,
                normalizedCalibrationTransform1, normalizedCalibrationTransform2, whitePoint,
                interpolationFactor, /*out*/sensorToXYZ);
        Converter.multiply(Converter.sXYZtoProPhoto, sensorToXYZ, /*out*/sensorToProPhoto);
        File customCCT = new File(Environment.getExternalStorageDirectory() + "//DCIM//PhotonCamera//", "customCCT.txt");
        assert this.calibrationTransform2 != null;
        assert forwardTransform1 != null;
        assert forwardTransform2 != null;
        CCT = new ColorCorrectionTransform();

        Converter.multiply(Converter.HDRXCCM, Converter.sProPhotoToXYZ, /*out*/proPhotoToSRGB);

        Log.d(TAG, "customCCT exist:" + customCCT.exists());
        Scanner sc = null;
        CCT.matrix = proPhotoToSRGB;
        if (customCCT.exists()) {
            try {
                sc = new Scanner(customCCT);
            } catch (FileNotFoundException ignored) {
            }
            assert sc != null;
            CCT.FillCCT(sc);
            /*sc.useDelimiter(",");
            sc.useLocale(Locale.US);
            for (int i = 0; i < 9; i++) {
                String inp = sc.next();
                proPhotoToSRGB[i] = Float.parseFloat(inp);
                //Log.d(TAG, "Read1:" + proPhotoToSRGB[i]);
            }*/
        }
        customTonemap = new float[]{
                -2f + 2f * tonemapStrength,
                3f - 3f * tonemapStrength,
                tonemapStrength,
                0f
        };
    }
    private void normalize(float [] in){
        float avr = in[0]+in[1]+in[2];
        in[0]/=avr;
        in[1]/=avr;
        in[2]/=avr;
        avr = in[3]+in[4]+in[5];
        in[3]/=avr;
        in[4]/=avr;
        in[5]/=avr;
        avr = in[6]+in[7]+in[8];
        in[6]/=avr;
        in[7]/=avr;
        in[8]/=avr;
    }
    private static void PrintMat(float[] mat) {
        StringBuilder outp = new StringBuilder();
        for (int i = 0; i < mat.length; i++) {
            outp.append(mat[i]).append(" ");
            if (i % 3 == 2) outp.append("\n");
        }
        Log.d(TAG, "matrix:\n" + outp);
    }

    protected Parameters Build() {
        Parameters params = new Parameters();
        params.cfaPattern = cfaPattern;
        params.usedDynamic = usedDynamic;
        params.blackLevel = blackLevel.clone();
        params.whitePoint = whitePoint.clone();
        params.whiteLevel = whiteLevel;
        params.realWL = realWL;
        params.hasGainMap = hasGainMap;
        params.mapSize = new Point(mapSize);
        params.sensorPix = new Rect(sensorPix);
        params.gainMap = gainMap.clone();
        params.proPhotoToSRGB = proPhotoToSRGB.clone();
        params.sensorToProPhoto = sensorToProPhoto.clone();
        params.tonemapStrength = tonemapStrength;
        params.customTonemap = customTonemap.clone();
        params.hotPixels = hotPixels.clone();
        params.focalLength = focalLength;
        params.cameraRotation = cameraRotation;
        params.CCT = CCT;
        return params;
    }


    //TODO do it

    //@Override
    //public String toString() {
    //    return "parameters:\n" +
    //            "\n hasGainMap=" + hasGainMap +
    //            "\n FrameCount=" + FrameNumberSelector.frameCount +
    //            "\n CameraID=" + PhotonCamera.getSettings().mCameraID +
    //            "\n Sat=" + FltFormat(PreferenceKeys.getSaturationValue()) +
    //            "\n Shadows=" + FltFormat(PhotonCamera.getSettings().shadows) +
    //            "\n Sharp=" + FltFormat(PreferenceKeys.getSharpnessValue()) +
    //            "\n Denoise=" + FltFormat(PreferenceKeys.getFloat(PreferenceKeys.Key.KEY_NOISESTR_SEEKBAR)) +
    //            "\n DenoiseOn=" + PhotonCamera.getSettings().hdrxNR +
    //            "\n FocalL=" + FltFormat(focalLength) +
    //            "\n Version=" + PhotonCamera.getVersion();
    //}

    private String FltFormat(Object in) {
        return String.format("%.2f", Float.parseFloat(in.toString()));
    }
}
