package com.particlesdevs.photoncamera.processing.opengl.postpipeline;


import com.particlesdevs.photoncamera.processing.opengl.GLFormat;
import com.particlesdevs.photoncamera.processing.opengl.GLImage;
import com.particlesdevs.photoncamera.processing.opengl.GLTexture;
import com.particlesdevs.photoncamera.processing.opengl.nodes.Node;
import com.particlesdevs.photoncamera.processing.opengl.scripts.GLHistogram;
import dngCamera.PhotonCamera;
import util.BufferUtils;
import util.FileManager;
import util.Log.Log;
import util.RANSAC;
import util.SplineInterpolator;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL43.*;
import static util.Math2.*;

public class Equalization extends Node {
    public Equalization() {
        super("","Equalization");
    }
    @Override
    public void Compile() {}
    private GLHistogram Analyze(){

        int resize = 6;
        GLTexture r1 = new GLTexture(previousNode.WorkingTexture.mSize.x/resize,
                previousNode.WorkingTexture.mSize.y/resize,previousNode.WorkingTexture.mFormat);
        //glProg.setDefine("BR",(float)shadowW*0.4f);
        glProg.setDefine("SAMPLING",resize);
        glProg.setDefine("ANALYZEINTENSE", analyzeIntensity);
        glProg.setDefine("LUT",true);
        glProg.useAssetProgram("analyze");
        File customAnalyzelut = new File(FileManager.sPHOTON_TUNING_DIR,"analyze_lut.png");
        GLImage analyze_lutbm = null;
        GLTexture analyze_lut = null;
        boolean loaded = false;
        if(customAnalyzelut.exists()){
            analyze_lutbm = new GLImage(customAnalyzelut);
            analyze_lut = new GLTexture(analyze_lutbm,GL_LINEAR,GL_CLAMP_TO_EDGE,0);
        } else {
            //try {
                analyze_lutbm = new GLImage(PhotonCamera.getAssetLoader().getInputStream("analyze_lut.png"));
                analyze_lut = new GLTexture(analyze_lutbm,GL_LINEAR,GL_CLAMP_TO_EDGE,0);
                loaded = true;
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}

        }
        if(loaded)
            glProg.setTexture("LookupTable",analyze_lut);
        glProg.setTexture("InputBuffer",previousNode.WorkingTexture);
        glProg.setVar("stp",0);
        glProg.drawBlocks(r1);
        //GLImage bmp = glUtils.GenerateGLImage(r1.mSize);
        if(loaded) {
            analyze_lut.close();
            analyze_lutbm.close();
        }
        /*float [] brArr = new float[r1.mSize.x*r1.mSize.y * 4];
        FloatBuffer fb = ByteBuffer.allocateDirect(brArr.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        fb.mark();
        glReadPixels(0, 0, r1.mSize.x, r1.mSize.y, GL_RGBA, GL_FLOAT, fb.reset());
        fb.get(brArr);
        fb.reset();
        r1.close();*/
        endT("Equalization Part 01");
        startT();
        GLHistogram histogram = new GLHistogram(basePipeline.glint.glProcessing);
        endT("Equalization Part 02");
        r1.close();

        return histogram;
    }
    private float gauss(float[] in,int ind){
        float sum = 0.f;
        float pdf = 0.f;
        for(int i =-8;i<=8;i++){
            int cind = ind+i;
            float w = pdf(i,5.5f);
            if(cind < 0) cind = 0;
            sum+=w*in[cind];
            pdf+=w;
        }
        return sum/pdf;
    }
    private float[] bezier(float in1, float in2, float in3,float in4,int size,int size2){
        float[] output = new float[size];
        float k = (float)(size)/size2;
        for(int i =0; i<size;i++){
            float s = (float)(i)*k/size;
            float p0 = mix(in1,in2,s);
            float p1 = mix(in2,in3,s);
            float p2 = mix(in3,in4,s);
            float p3 = mix(p0,p1,s);
            float p4 = mix(p1,p2,s);
            output[i] = mix(p3,p4,s);
        }
        return output;
    }
    private float[] bezier1(float in1,float in3,float in4,int size){
        float[] output = new float[size];
        for(int i =0; i<size;i++){
            float s = (float)(i)/size;
            float p0 = mix(in1,in3,s);
            float p2 = mix(in3,in4,s);
            float p3 = mix(p0,p2,s);
            float p4 = mix(p2,p3,s);
            output[i] = mix(p3,p4,s);
        }
        return output;
    }
    private float findWL(float[] inputR,float[] inputG, float[] inputB){
        boolean nightMode = false;
        float wlind = inputG.length-1;
        for(int i =0; i<inputR.length;i++){
            if(inputR[i] > 0.99) {
                wlind = i;
                //wlind = (i*8.f+wlind)/(8.0f + 1.f);
                break;
            }
        }
        for(int i =0; i<inputG.length;i++){
            if(inputG[i] > 0.99) {
                wlind = i;
                //wlind = (i*8.f+wlind)/(8.0f + 1.f);
                break;
            }
        }
        for(int i =0; i<inputB.length;i++){
            if(inputB[i] > 0.99) {
                wlind = i;
                //wlind = (i*8.f+wlind)/(8.0f + 1.f);
                break;
            }
        }
        /*if(!nightMode){
            wlind = (wlind + (input.length-1.f))/(1.f + 1.f);
        } else {
            wlind = (wlind*8.f+(input.length-1.f))/(8.0f + 1.f);
        }*/
        wlind = Math.min(wlind+128,inputG.length-1);
        return wlind;
    }
    private float findBL(float[] inputR,float[] inputG, float[] inputB){
        boolean nightMode = false;
        float blind = 0;
        for(int i =inputR.length-1; i>=0;i--){
            if(inputR[i] < 0.01) {
                blind = i;
                //wlind = (i*8.f+wlind)/(8.0f + 1.f);
                break;
            }
        }
        for(int i =inputG.length-1; i>=0;i--){
            if(inputG[i] < 0.01) {
                blind = i;
                //wlind = (i*8.f+wlind)/(8.0f + 1.f);
                break;
            }
        }
        for(int i =inputB.length-1; i>=0;i--){
            if(inputB[i] < 0.01) {
                blind = i;
                //wlind = (i*8.f+wlind)/(8.0f + 1.f);
                break;
            }
        }
        /*if(!nightMode){
            blind = (blind + (input.length-1.f))/(1.f + 1.f);
        } else {
            blind = (blind*8.f+(input.length-1.f))/(8.0f + 1.f);
        }*/
        blind = Math.max(blind-16,0);
        return blind;
    }
    private float contrastLevel(float[] input, int wlind){
        double integin = 0.0;
        double intege = 0.0;
        for(int i =0; i<wlind;i++){
            intege +=(double)(i)/(wlind-1.0);
            integin += input[i];
        }
        return (float)(intege/integin);
    }
    private float[] bSpline(float[] input, float[] blwl){
        boolean nightMode = false;
        float bilateralk = edgesBilateralSmooth;
        if(nightMode) bilateralk = edgesBilateralSmoothNight;
        blwl[0] = pdf(blwl[0]/input.length,bilateralk)*blwl[0]*edgesStretchShadows;
        blwl[1] = input.length - pdf(1.f - blwl[1]/input.length,bilateralk*highLightSmoothAmplify)*(input.length - blwl[1])*edgesStretchHighLight;
        blwl[1] = Math.min(blwl[1],input.length-1);
        ArrayList<Float> my,mx;
        my = new ArrayList<>();
        mx = new ArrayList<>();
        int wlind = (int)blwl[1];
        float[] output = new float[wlind];
        float clevel = contrastLevel(input,wlind);
        Log.d(Name,"ContrastLevel:"+clevel);

        int count = Math.min((int)(13 - Math.max(clevel,1.0/clevel)*3),10);
        count = Math.max(count,2);
        Log.d(Name,"Count:"+count);
        float aggressiveness = 1.2f;
        float k = (wlind-1.f)/(count-1.f);
        for(int xi = 0; xi<count; xi++){
            int x = (int)(xi*k);
            mx.add((float)xi/(float)(count-1));
            my.add(input[x]);
        }
        float k2 = (my.get(my.size()-1))/(my.size()-1);
        if(wlind <= input.length-16){
            float wl = my.get(my.size()-1);
            for(int i =0; i<my.size();i++){
                float ik = i*k2;
                if(my.get(i) < ik){
                    my.set(i,ik);
                }
            }
            my.set(my.size()-1,wl);
        }
        for(int xi = 1; xi<count-1; xi++){
            my.set(xi,(my.get(xi-1)+my.get(xi)*aggressiveness+my.get(xi+1))/(aggressiveness+2.f));
        }
        my.set(0,0.0f);
        SplineInterpolator splineInterpolator = SplineInterpolator.createMonotoneCubicSpline(mx,my);
        for(int i =0; i<output.length;i++){
            output[i] = splineInterpolator.interpolate(i/(float)(output.length-1));
        }
        return output;
    }
    private float[] bilateralSmoothCurve(float[]input, float[] blwl){
        boolean nightMode = false;
        float bilateralk = edgesBilateralSmooth;
        if(nightMode) bilateralk = edgesBilateralSmoothNight;
        ArrayList<Float> my,mx;
        my = new ArrayList<>();
        mx = new ArrayList<>();
        Log.d(Name,"BL0:"+blwl[0]);
        Log.d(Name,"WL0:"+blwl[1]);
        blwl[0] = pdf(blwl[0]/input.length,bilateralk)*blwl[0]*edgesStretchShadows;
        blwl[1] = input.length - pdf(1.f - blwl[1]/input.length,bilateralk*highLightSmoothAmplify)*(input.length - blwl[1])*edgesStretchHighLight;
        blwl[1] = Math.min(blwl[1],input.length-1);
        float centerY = 0.f;
        float msum = 0.f;
        float centerX = mix(blwl[0],blwl[1],curveCenter);
        for(int i =(int)blwl[0]; i<(int)blwl[1];i++){
            float k = pdf((i-mix(blwl[0],blwl[1],analyzeCenter))/blwl[1],1.f);
            centerY+=k*input[i];
            msum+=k;
        }
        centerY = (centerY+0.0001f)/(msum+0.0001f);

        mx.add(blwl[0]);
        //mx.add(blwl[0]+0.01f);
        mx.add(centerX);
        mx.add(blwl[1]);
        //mx.add(blwl[1]+0.01f);


        my.add(0.f);
        //my.add(0.f);
        my.add(centerY);
        my.add(1.f);
        //my.add(1.f);
        Log.d(Name,"blwl[0]:"+blwl[0]);
        Log.d(Name,"blwl[1]:"+blwl[1]);
        Log.d(Name,"Mx:"+mx.toString());
        Log.d(Name,"My:"+my.toString());
        float[]output = new float[input.length];
        SplineInterpolator splineInterpolator = SplineInterpolator.createMonotoneCubicSpline(mx,my);
        for(int i =0; i<output.length;i++){
            output[i] = splineInterpolator.interpolate(i);
            if(i < blwl[0]) output[i] = 0.f;
        }
        return output;
    }
    private float[] SmoothCurve(float[]input, float BL,float WL){
        boolean nightMode = false;
        float bilateralk = edgesBilateralSmooth;
        if(nightMode) bilateralk = edgesBilateralSmoothNight;
        Log.d(Name,"BL0:"+BL);
        Log.d(Name,"WL0:"+WL);
        BL = pdf(BL/input.length,bilateralk)*BL*edgesStretchShadows;
        WL = input.length - pdf(1.f - WL/input.length,bilateralk*highLightSmoothAmplify)*(input.length - WL)*edgesStretchHighLight;
        WL = Math.min(WL,input.length-1);
        int size;
        float[]output = input.clone();
        for(int k = 0; k<1;k++) {
            input = output.clone();
            for (int i = 0; i < output.length; i++) {
                if (i >= (int) BL && i < (int) WL) {
                    size = Math.min(i-(int)BL,(int)WL - i);
                    float temp = 0.f;
                    float pdf = 0.f;
                    for (int j = -size; j < size; j++) {
                        if (j + i >= (int) BL && j + i < (int) WL) {
                            float ker = pdf(j / 512.f, 1.f);
                            temp += ker * input[i + j];
                            pdf += ker;
                        }
                    }
                    output[i] = (temp + 0.001f) / (pdf + 0.001f);
                } else if (i <= BL) output[i] = 0.f;
                else output[i] = 1.f;
            }
        }
        return output;
    }
    private float[] bezierIterate(float[] input, int iterations){
        float[] inchanging = input.clone();
        float wlind = findWL(input,input,input);
        float[] params = new float[]{input[0],input[(int)(wlind/3.f)],input[(int)(wlind/1.5f)],input[(int)wlind]};
        float k = (params[3])/(params.length-1);

        if(wlind <= input.length-16){
            float wl = params[3];
            for(int i =0; i<params.length;i++){
                float ik = i*k;
                if(params[i] < ik){
                    params[i] = ik;
                }
            }
            params[3] = wl;
        }
        float[] bezier = bezier(params[0],params[1],params[2],params[3],input.length,(int)wlind);

        for(int j = 0; j<iterations;j++){
            for(int i =0; i<inchanging.length;i++){
                inchanging[i] += (float)i/inchanging.length - bezier[i];
            }
            float[] bezier2 = bezier(inchanging[0],inchanging[(int)(wlind/3.f)],inchanging[(int)(wlind/1.5f)],inchanging[(int)wlind],input.length,(int)wlind);
            for(int i =0; i<inchanging.length;i++){
                bezier[i] -=(float)i/inchanging.length - bezier2[i];
            }
        }
        return bezier;
    }
    static class Point2D{
        float x,y;
    }
    private Point2D mixp(Point2D in, Point2D in2, float t){
        Point2D outp = new Point2D();
        outp.x = in.x*(1.f-t) + in2.x*t;
        outp.y = in.y*(1.f-t) + in2.y*t;
        return outp;
    }
    private void ApplyLaplace(float[] currentCurve, float[] eqCurve){
        float laplacianAMP = 1.5f;
        int laplaceSize = 128;
        float[] laplaceArr = new float[eqCurve.length];
        for(int i =0; i<laplaceArr.length;i++){
            float blur = 0.f;
            float pdf = 0.f;
            for(int j = -laplaceSize/2; j<=laplaceSize/2;j++){
                float mp = pdf((float)j/(laplaceSize/2.f),1.5f);
                blur+=eqCurve[MirrorCoords(i+j,eqCurve.length)]*mp;
                pdf+=mp;
            }
            blur/=pdf;
            laplaceArr[i] = eqCurve[i]-blur;
        }
        for(int i =0; i<currentCurve.length-1;i++){
            float mp1 = Math.min(1.f,i*10.f/(currentCurve.length-1.f));
            float nc = currentCurve[i]+laplaceArr[i]*laplacianAMP*(Math.min(i,400)/400.f)*mp1;
            //if(nc > currentCurve[i+1]) nc = currentCurve[i];
            currentCurve[i] = nc;
        }
    }
    private float[] bezier2(float[] input){
        return input;
    }
    /*private float[] bezier(float[]in,int size){
        float[] output = new float[size];
        float[] reduct = new float[in.length];
        for(int i =0; i<size;i++){
            for(int reduction = 0;reduction<in.length;reduction++){
                for(int j =0; j<reduction;j++){
                    reduct[j] =
                }
            }
        }
        return output;
    }*/
    class Minindexes implements Comparable<Minindexes>{
        public float r,g,b;
        public int ind;
        public double dist(){
            float r2,g2,b2;
            r2 = r/(r+g+b);
            g2 = g/(r+g+b);
            b2 = b/(r+g+b);
            return Math.sqrt(r2*r2 + g2*g2 + b2*b2);
        }
        public Minindexes(float r, float g, float b, int ind){
            this.r = r+0.0001f;
            this.g = g+0.0001f;
            this.b = b+0.0001f;
            this.ind = ind;
        }
        @Override
        public int compareTo(Minindexes o) {
            double out = ((dist()-o.dist())*1000.);
            return Double.compare(out, 0.0);
        }
    }
    private float[] getWB(float[] histr, float[] histg, float[] histb, float[] blwl){
        int searchMax = (int) mix(blwl[0],blwl[1],whiteBalanceSearch/((float)histSize));
        float rk = 0.f;
        float gk = 0.f;
        float bk = 0.f;
        float cnt = 0.f;
        float mindist = 1000.f;
        /*Minindexes[] minindexes = new Minindexes[histSize];
        for(int i =0; i<histSize;i++){
            minindexes[i] = new Minindexes(histr[i],histg[i],histb[i],i);
        }
        Arrays.sort(minindexes);
        Log.d(Name,"0:"+minindexes[0].dist()+",1:"+minindexes[histSize-1].dist());
         */
        List<Double> dataR = new ArrayList<>();
        List<Double> dataG = new ArrayList<>();
        List<Double> dataB = new ArrayList<>();
        for(int i = (int)mix(blwl[0],blwl[1],0.05); i<(int)mix(blwl[0],blwl[1],0.9);i++){
            //Minindexes minindexes1 = minindexes[i];

            dataR.add((histr[i]+0.0001)/(histr[i]+histb[i]+histr[i]+0.0001));
            dataG.add((histg[i]+0.0001)/(histr[i]+histb[i]+histr[i]+0.0001));
            dataB.add((histb[i]+0.0001)/(histr[i]+histb[i]+histr[i]+0.0001));
            //cnt+=1.f;
        }
        List<Double> res = RANSAC.perform(dataR, 2, 1500, 1, 0.2);
        rk = res.get(1).floatValue();
        res = RANSAC.perform(dataG, 2, 1500, 1, 0.2);
        gk = res.get(1).floatValue();
        res = RANSAC.perform(dataB, 2, 1500, 1, 0.2);
        bk = res.get(1).floatValue();
        /*
        for(int i = 0; i<searchMax;i++){
            rk+=
            cnt+=1.f;
        }*/
        //float rk = Utilities.linearRegressionK(Arrays.copyOfRange(histr,histr.length-1-searchMax,histr.length))+0.0001f;
        //float gk = Utilities.linearRegressionK(Arrays.copyOfRange(histg,histg.length-1-searchMax,histg.length))+0.0001f;
        //float bk = Utilities.linearRegressionK(Arrays.copyOfRange(histb,histb.length-1-searchMax,histb.length))+0.0001f;
        float[] outp = new float[]{rk+0.0001f,gk+0.0001f,bk+0.0001f};
        float mink = Math.min(Math.min(outp[0],outp[1]),outp[2]);
        outp[0]/=mink;
        outp[1]/=mink;
        outp[2]/=mink;
        //outp = Utilities.saturate(outp,whiteBalanceSaturation);
        Log.d(Name,"WBK:"+Arrays.toString(outp));
        mink = Math.min(Math.min(outp[0],outp[1]),outp[2]);
        outp[0]/=mink;
        outp[1]/=mink;
        outp[2]/=mink;
        return new float[]{outp[0],outp[1],outp[2]};
    }
    GLTexture lut;
    GLImage lutbm;
    float analyzeIntensity = -2.0f;
    float analyzeCenter = 0.5f;
    float curveCenter = 0.5f;
    float edgesStretchShadows = 2.25f;
    float edgesStretchHighLight = 0.0f;
    int histSize = 4096;
    int blackLevelSearch = 384;
    float edgesBilateralSmooth = 3.5f;
    float edgesBilateralSmoothNight = 3.0f;
    float highLightSmoothAmplify = 2.5f;
    float shadowsSensitivity = 0.5f;
    float blackLevelSensitivity = 1.0f;
    int whiteBalanceSearch = 400;
    float whiteBalanceSaturation = 1.35f;
    float[] tonemapCoeffs = new float[]{-0.78360f / 1.0063f, 0.84690f / 1.0063f, 0.9430f / 1.0063f, 0f};
    boolean disableEqualization = false;
    boolean enableTonemap = true;
    float highlightCompress = 0.4f;
    float contrast = 0.2f;
    boolean useOldEqualization = false;
    boolean removeUnderexpose = true;
    @Override
    public void Run() {
        startT();
        disableEqualization = getTuning("DisableEqualization",disableEqualization);
        if(disableEqualization){
            WorkingTexture = previousNode.WorkingTexture;
            glProg.closed = true;
            return;
        }
        highlightCompress = getTuning("HighlightCompress",highlightCompress);
        contrast = getTuning("Contrast",contrast);
        enableTonemap = getTuning("EnableTonemap",enableTonemap);
        removeUnderexpose = getTuning("RemoveUnderexpose",removeUnderexpose);
        useOldEqualization = getTuning("UseOldEqualization",useOldEqualization);
        analyzeIntensity = getTuning("AnalyzeIntensity", analyzeIntensity);
        edgesStretchShadows = getTuning("EdgesStretchShadows", edgesStretchShadows);
        edgesStretchHighLight = getTuning("EdgesStretchHighLight", edgesStretchHighLight);
        edgesBilateralSmooth = getTuning("EdgesBilateralSmooth", edgesBilateralSmooth);
        edgesBilateralSmoothNight = getTuning("EdgesBilateralSmoothNight", edgesBilateralSmoothNight);
        highLightSmoothAmplify = getTuning("HighLightSmoothAmplify", highLightSmoothAmplify);
        analyzeCenter = getTuning("AnalyzeCenter", analyzeCenter);
        curveCenter = getTuning("CurveCenter", curveCenter);
        shadowsSensitivity = getTuning("ShadowsSensitivity", shadowsSensitivity);
        histSize = getTuning("HistSize", histSize);
        blackLevelSearch = getTuning("BlackLevelSearch", blackLevelSearch);
        blackLevelSensitivity = getTuning("BlackLevelSensitivity", blackLevelSensitivity);
        whiteBalanceSearch = getTuning("WhiteBalanceSearch", whiteBalanceSearch);
        tonemapCoeffs = getTuning("TonemapCoeffs", tonemapCoeffs);
        WorkingTexture = basePipeline.getMain();
        float rmax = (float)(Math.sqrt(basePipeline.mParameters.noiseModeler.computeModel[0].second) + Math.sqrt(basePipeline.mParameters.noiseModeler.computeModel[0].first));
        float gmax = (float)(Math.sqrt(basePipeline.mParameters.noiseModeler.computeModel[1].second) + Math.sqrt(basePipeline.mParameters.noiseModeler.computeModel[1].first));
        float bmax = (float)(Math.sqrt(basePipeline.mParameters.noiseModeler.computeModel[2].second) + Math.sqrt(basePipeline.mParameters.noiseModeler.computeModel[2].first));
        Log.d("Equalization","rgb max shift:"+rmax+","+gmax+","+bmax);
        endT("Equalization Part 00");
        GLHistogram histParser = Analyze();
        float[] histr = buildCumulativeHist(histParser.outputArr[0],1024);
        float[] histg = buildCumulativeHist(histParser.outputArr[1],1024);
        float[] histb = buildCumulativeHist(histParser.outputArr[2],1024);
        float[] hist = buildCumulativeHist(histParser.outputArr[3],1024);
        startT();
        //Bitmap lutbm = BitmapFactory.decodeResource(PhotonCamera.getResourcesStatic(), R.drawable.lut2);
        int wrongHist = 0;
        int brokeHist = 0;
        for(int i =0; i<hist.length;i++){
            float val = ((float)(i))/hist.length;
            //if(3.f < hist[i] || val*0.25 > hist[i]) {
            //wrongHist++;
            //}
            if(hist[i] > 15.f){
                brokeHist++;
            }
            if(Float.isNaN(hist[i])){
                brokeHist+=2;
            }
        }
        if(brokeHist >= 10){
            wrongHist = hist.length;
        }
        Log.d(Name,"WrongHistFactor:"+wrongHist);
        if(wrongHist != 0){
            float wrongP = ((float)wrongHist)/hist.length;
            wrongP-=0.5f;
            if(wrongP > 0.0) wrongP*=1.6f;
            wrongP+=0.5f;
            wrongP = Math.min(wrongP,1.f);
            Log.d(Name,"WrongHistPercent:"+wrongP);
            for(int i =0; i<hist.length;i++){
                hist[i] = (((float)(i))/hist.length)*wrongP + hist[i]*(1.f-wrongP);
            }
        }

        float[] averageCurve = new float[hist.length];
        for(int i =0; i<averageCurve.length;i++){
            averageCurve[i] = (histr[i]+histg[i]+histb[i])/3.f;
        }
        endT("Equalization Part 1");
        startT();
        //if(basePipeline.mSettings.DebugData) GenerateCurveBitm(histr,histg,histb);
        float max = 0.f;
        float WL = findWL(histr,histg,histb);
        float BL = findBL(histr,histg,histb);
        float[] blwl = new float[]{BL,WL};
        double compensation = averageCurve.length/WL;
        if(useOldEqualization){
            hist = bSpline(hist,blwl);
        } else
            hist = bilateralSmoothCurve(hist,blwl);


        float[] WB = new float[]{1.0f,1.0f,1.0f};// = getWB(histr,histg,hist,blwl);

        //Use kx+b prediction for curve start
        //Depurple Degreen
        float[] BLPredict = new float[3];
        float[] BLPredictShift = new float[3];
        //int maxshift = (int) (blwl[0] + blwl[1]*blackLevelSearch/4096.f);
        int maxshift = (int) mix(blwl[0],WL,blackLevelSearch/((float)histSize));
        maxshift = Math.max(maxshift,10);
        Log.d(Name,"BlSearch:"+maxshift);
        int cnt = 0;
        for(int i =5; i<maxshift;i++){
            float x = (float)(i)/histSize;
            BLPredict[0]+= histr[i]/x;
            BLPredict[1]+= histg[i]/x;
            BLPredict[2]+= histb[i]/x;
            cnt++;
        }
        BLPredict[0]/=cnt;
        BLPredict[1]/=cnt;
        BLPredict[2]/=cnt;
        ((PostPipeline)basePipeline).totalGain *=Math.max(BLPredict[0],Math.max(BLPredict[1],BLPredict[2]));
        Log.d(Name,"TotalGain:"+((PostPipeline)basePipeline).totalGain);
        cnt = 0;
        for(int i =5; i<maxshift;i++){
            float x = (float)(i)/histSize;
            BLPredictShift[0]+=histr[i]-x*BLPredict[0];
            BLPredictShift[1]+=histg[i]-x*BLPredict[1];
            BLPredictShift[2]+=histb[i]-x*BLPredict[2];
            cnt++;
        }
        BLPredictShift[0]/=cnt;
        BLPredictShift[1]/=cnt;
        BLPredictShift[2]/=cnt;

        //Saturate shift
        float avr = (BLPredictShift[0]+BLPredictShift[1]+BLPredictShift[2])/3.f;
        float saturation = 0.0f;
        BLPredictShift[0] = -(BLPredictShift[0]-avr*saturation) / (1.f-avr*saturation);
        BLPredictShift[1] = -(BLPredictShift[1]-avr*saturation) / (1.f-avr*saturation);
        BLPredictShift[2] = -(BLPredictShift[2]-avr*saturation) / (1.f-avr*saturation);

        float mins = Math.min(BLPredictShift[0],Math.min(BLPredictShift[1],BLPredictShift[2]));
        if(mins < 0.0) {
            BLPredictShift[0]-=mins;
            BLPredictShift[1]-=mins;
            BLPredictShift[2]-=mins;
        }
        if(false) {
            float oldr = BLPredictShift[0];
            float oldb = BLPredictShift[2];
            BLPredictShift[2] = Math.min(BLPredictShift[0],BLPredictShift[2]);
            BLPredictShift[0] = BLPredictShift[2];
            BLPredictShift[0] += oldr*0.15f;
            BLPredictShift[2] += oldb*0.15f;
        }

        //Limit blacklevel to 10% of detected range
        float length  = (float) Math.sqrt(BLPredictShift[0]*BLPredictShift[0] + BLPredictShift[1]*BLPredictShift[1] + BLPredictShift[2]*BLPredictShift[2])+0.0001f;
        float length2 = Math.min(length,WL-blwl[0])/length;
        BLPredictShift[0]*=blackLevelSensitivity*length2;
        BLPredictShift[1]*=blackLevelSensitivity*length2;
        BLPredictShift[2]*=blackLevelSensitivity*length2;

        Log.d(Name,"PredictedBLShift:"+Arrays.toString(BLPredictShift));
        Log.d(Name,"PredictedWBKoeff:"+Arrays.toString(WB));
        //if(basePipeline.mSettings.DebugData)GenerateCurveBitm(histParser.outputArr[1],histParser.outputArr[2],histParser.outputArr[3]);

        double shadowW = (basePipeline.mSettings.shadows);
        double avrbr = 0.0;

        for(int i =0; i<hist.length;i++){
            float line = i/(hist.length-1.f);
            double linepi = line*Math.PI - Math.PI/2.0;
            double contrastCurve = (Math.sin(linepi) + 1.0)/2.0;
            if(removeUnderexpose) hist[i] = Math.max(hist[i],line);
            if(shadowW != 0.f) {
                if(shadowW > 0.f)
                    hist[i] = (float)mix(hist[i],Math.sqrt(hist[i]),(shadowW)*shadowsSensitivity);
                else hist[i] = (float)mix(hist[i],(hist[i])*(hist[i]),-(shadowW)*shadowsSensitivity);
            }

            hist[i] = mix(hist[i],line,line*line*highlightCompress);
            hist[i] = (float) mix(hist[i],hist[i]*contrastCurve,contrast);
            avrbr+=hist[i];
        }
        avrbr/=hist.length;
        float desaturate = 0.5f/(float)avrbr;
        desaturate = Math.max(1.f,desaturate);
        desaturate*=1.0f;
        desaturate-=1.0f;
        //if(basePipeline.mSettings.DebugData) GenerateCurveBitmWB(hist,BLPredictShift,new float[]{WL,WL,WL});
        GLTexture histogram = new GLTexture(hist.length,1,new GLFormat(GLFormat.DataType.FLOAT_16),
                BufferUtils.getFrom(hist), GL_LINEAR, GL_CLAMP_TO_EDGE);
        //GLTexture shadows = new GLTexture(hist.length,1,new GLFormat(GLFormat.DataType.FLOAT_16,3),
        //        FloatBuffer.wrap(shadowCurve), GL_LINEAR, GL_CLAMP_TO_EDGE);
        glProg.setDefine("BL2",BLPredictShift);
        glProg.setDefine("BR",(float)(shadowW)*shadowsSensitivity);
        File customlut = new File(FileManager.sPHOTON_TUNING_DIR,"lut.png");
        glProg.setDefine("TONEMAP",enableTonemap);
        glProg.setDefine("DESAT",desaturate);
        if(customlut.exists()){
            lutbm = new GLImage(customlut);
            lut = new GLTexture(lutbm,GL_LINEAR,GL_CLAMP_TO_EDGE,0);
            glProg.setDefine("LUT",true);
            int lutBase = (int)(0.1f+Math.pow(lutbm.size.x,1.0/3.0));
            Log.d(Name,"LutBase:"+lutBase);
            glProg.setDefine("LUTSIZETILES", (float) lutBase);
            glProg.setDefine("LUTSIZE", (float) (lutBase*lutBase));
        }
        endT("Equalization Part 2");
        startT();
        glProg.useAssetProgram("equalize");
        if(lut != null) glProg.setTexture("LookupTable",lut);
        glProg.setTexture("Histogram",histogram);
        //glProg.setTexture("Shadows",shadows);
        GLTexture TonemapCoeffs = new GLTexture(new Point(256, 1),
                new GLFormat(GLFormat.DataType.FLOAT_16,1),BufferUtils.getFrom(basePipeline.mSettings.toneMap),GL_LINEAR,GL_CLAMP_TO_EDGE);
        glProg.setTexture("TonemapTex",TonemapCoeffs);
        glProg.setVar("toneMapCoeffs", tonemapCoeffs);
        glProg.setTexture("InputBuffer",previousNode.WorkingTexture);
        glProg.drawBlocks(WorkingTexture);
        histogram.close();
        if(lutbm != null) lutbm.close();
        if(lut != null) lut.close();
        TonemapCoeffs.close();
        glProg.closed = true;
        endT("Equalization Part 3");
    }
}
