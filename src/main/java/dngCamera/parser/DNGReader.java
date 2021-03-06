package dngCamera.parser;

import com.particlesdevs.photoncamera.processing.render.Parameters;
import data.Pair;
import data.Rational;
import util.Log.Log;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

public class DNGReader {
    HashMap<Integer, TIFFTag> tags;
    ByteBuffer dngBuffer;
    private static String TAG = "FillParametersByDNG";
    private static TIFFTag getTag(HashMap<Integer,TIFFTag> tags, int id) {
        TIFFTag output = tags.get(id);
        if(output == null) Log.e("FillParametersByDNG","Error on getTag:"+id);
        return output;
    }
    private static float[][] getCM(float[] input){
        float[][] output = new float[3][3];
        for(int i =0; i<input.length;i++){
            output[i/3][i%3] = input[i];
        }
        return output;
    }
    public DNGReader(ByteBuffer dngBuffer) {
        this.dngBuffer = dngBuffer;
        dngBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byte[] format = { dngBuffer.get(), dngBuffer.get() };
        if (!new String(format).equals("II")) {
            Log.e(TAG, "Can only parse LITTLE_ENDIAN");
            return;
        }

        short version = dngBuffer.getShort();
        if (version != 42) {
            Log.e("FillParametersByDNG","Can only parse v42, version:"+version);
            return;
        }
        int start = dngBuffer.getInt();
        dngBuffer.position(start);

        tags = TagParser.parse(dngBuffer);

        TIFFTag subIFD = getTag(tags, TIFF.TAG_SubIFDs);
        TIFFTag type = getTag(tags, TIFF.TAG_NewSubfileType);
        if (subIFD != null && type != null && type.getInt() == 1) {
            dngBuffer.position(subIFD.getInt());
            HashMap<Integer,TIFFTag> subTags = TagParser.parse(dngBuffer);
            tags.putAll(subTags);
            /*for (int i = 0; i < subTags.size(); i++) {
                tags.put(subTags.keyAt(i), subTags.valueAt(i));
            }*/
        }
    }

    public void FillParameters(Parameters parameters){
        parameters.cfaPattern = (byte)CFAPattern.get(getTag(tags,TIFF.TAG_CFAPattern).getByteArray());
        int[] bl = getTag(tags, TIFF.TAG_BlackLevel).getIntArray();
        for(int i =0; i<bl.length;i++)
            parameters.blackLevel[i] = (float)bl[i];
        parameters.whiteLevel = getTag(tags, TIFF.TAG_WhiteLevel).getInt();
        parameters.realWL = parameters.whiteLevel;
        parameters.referenceIlluminant1 = getTag(tags, TIFF.TAG_CalibrationIlluminant1).getInt();
        parameters.referenceIlluminant2 = getTag(tags, TIFF.TAG_CalibrationIlluminant2).getInt();

        parameters.calibrationTransform1 = getCM(tags.get(TIFF.TAG_CameraCalibration1).getFloatArray());
        parameters.calibrationTransform2 = getCM(tags.get(TIFF.TAG_CameraCalibration2).getFloatArray());

        parameters.colorMatrix1 = getCM(tags.get(TIFF.TAG_ColorMatrix1).getFloatArray());
        parameters.colorMatrix2 = getCM(tags.get(TIFF.TAG_ColorMatrix2).getFloatArray());

        parameters.forwardTransform1 = getCM(tags.get(TIFF.TAG_ForwardTransform1).getFloatArray());
        parameters.forwardTransform2 = getCM(tags.get(TIFF.TAG_ForwardTransform2).getFloatArray());

        Rational[] asShotNeutral = getTag(tags, TIFF.TAG_AsShotNeutral).getRationalArray();
        parameters.whitePoint = new float[]{
                asShotNeutral[0].floatValue(),
                asShotNeutral[1].floatValue(),
                asShotNeutral[2].floatValue()
        };
        float[] model = getTag(tags,TIFF.TAG_NoiseProfile).getFloatArray();
        if(model != null) {
            int size = model.length / 2;
            if(size >= 2) {
                parameters.NoiseModel = new Pair[model.length / 2];
                for(int i =0; i<parameters.NoiseModel.length; i++){
                    parameters.NoiseModel[i] = new Pair<>( (double) model[i / 2],(double) model[i / 2 + 1]);
                }
            }
        }
        parameters.iso = getTag(tags,TIFF.TAG_ISOSpeedRatings).getInt();
        parameters.focalLength = getTag(tags,TIFF.TAG_FocalLength).getFloat();
        Point imageSize = new Point(getTag(tags,TIFF.TAG_ImageWidth).getInt(),getTag(tags,TIFF.TAG_ImageHeight).getInt());

        parameters.FillConstParameters(imageSize);
        parameters.FillDynamicParameters();
        TIFFTag Op2 = tags.get(TIFF.TAG_OpcodeList2);
        if (Op2 != null) {
            Object[] opParsed = OpParser.parseAll(Op2.getByteArray());
            OpParser.GainMap[] mapPlanes = new OpParser.GainMap[4];
            for (Object o : opParsed) {
                Log.e(TAG, "Parsed opcode: " + o.toString());
                if (o instanceof OpParser.GainMap mapPlane) {
                    mapPlanes[(mapPlane.top << 1) | mapPlane.left] = mapPlane;
                }
            }
            if (mapPlanes[0] != null && mapPlanes[1] != null
                    && mapPlanes[2] != null && mapPlanes[3] != null) {
                parameters.mapSize = new Point(mapPlanes[0].mapPointsH, mapPlanes[0].mapPointsV);
                parameters.gainMap = new float[parameters.mapSize.x * parameters.mapSize.y * 4];
                for (int i = 0; i < parameters.gainMap.length; i++) {
                    parameters.gainMap[i] = mapPlanes[i % 4].px[i / 4];
                }
            }
        }
    }

    public ByteBuffer ReadRawBuffer(){
        Point imageSize = new Point(getTag(tags,TIFF.TAG_ImageWidth).getInt(),getTag(tags,TIFF.TAG_ImageHeight).getInt());
        int[] stripOffsets = getTag(tags, TIFF.TAG_StripOffsets).getIntArray();
        int[] stripByteCounts = getTag(tags, TIFF.TAG_StripByteCounts).getIntArray();
        int rawSizeB = imageSize.x * imageSize.y * 2;
        Log.v("FillParametersByDNG","strip:"+stripOffsets[0]+" stripCounts:"+stripByteCounts[0]+" sizeB:"+rawSizeB);
        ByteBuffer rawBuffer = ByteBuffer.allocateDirect(rawSizeB);
        byte[] rawArr = new byte[rawSizeB];
        int rawImageOffset = 0;
        for (int i = 0; i < stripOffsets.length; i++) {
            //rawBuffer.put(rawImageOffset,dngBuffer, stripOffsets[i], stripByteCounts[i]);
            dngBuffer.position(stripOffsets[i]).get(rawArr,rawImageOffset,stripByteCounts[i]);
            rawImageOffset += stripByteCounts[i];
        }

        rawBuffer.put(rawArr);

        rawBuffer.position(0);
        return rawBuffer;
    }
}
