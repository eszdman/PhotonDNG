package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Math.min;

public class Utilities {
    //private static final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.ADD);

    public static BufferedImage drawKernels(float[][][] inputKernels, Point kernelSize, Point kernelCount){
        /*
        int width = kernelSize.x*kernelCount.x;
        int height = kernelSize.y*kernelCount.y;
        BufferedImage output = BufferedImage.createBufferedImage(width,height, BufferedImage.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawColor(Color.BLACK);
        Paint pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        for(int i =0; i<inputKernels.length;i++)
            for(int j =0; j<inputKernels[i].length;j++)
                for(int k =0; k<inputKernels[i][j].length;k++){
                    int x = k%kernelSize.x;
                    int y = k/kernelSize.x;
                    int br = min((int)(inputKernels[i][j][k]*255),255);
                    pointPaint.setARGB(255,br,br,br);
                    //canvas.drawPoint(i*kernelSize.x + kernelSize.x/2.f + x,j*kernelSize.y + kernelSize.y/2.f +y,pointPaint);
                    canvas.drawCircle(i*kernelSize.x + x,j*kernelSize.y + y,5.f,pointPaint);
                }
        pointPaint.setARGB(100, 255, 255, 0);
        for(int i =1; i<kernelCount.x;i++)
            canvas.drawLine(i*kernelSize.x,0,i*kernelSize.x+2,kernelSize.y*kernelCount.y,pointPaint);
        for(int i =1; i<kernelCount.y;i++)
            canvas.drawLine(0,i*kernelSize.y,kernelSize.x*kernelCount.x,i*kernelSize.y+2,pointPaint);

        return output;

         */
        return null;
    }
    public static void drawPoints(Point[] inputPoints, float pointSize,BufferedImage io){
        /*Canvas canvas = new Canvas(io);
        Paint wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.FILL);
        wallPaint.setARGB(255, 0, 255, 0);
        for(Point p : inputPoints)
            canvas.drawCircle(p.x,p.y,pointSize,wallPaint);
         */
    }
    public static void saveBufferedImage(BufferedImage in, String name){
        /*File debug = new File(ImageSaver.Util.newJPGFilePath().toString().replace(".jpg","") + name + ".png");
        FileOutputStream fOut = null;
        try {
            debug.createNewFile();
            fOut = new FileOutputStream(debug);
        } catch (IOException e) {
            e.printStackTrace();
        }
        in.compress(BufferedImage.CompressFormat.PNG, 100, fOut);
         */
    }
    public static void drawBL(float[] rgb, BufferedImage io){
        /*
        float max = 0.f;
        int width = io.getWidth();
        int height = io.getHeight();
        Canvas canvas = new Canvas(io);
        Paint wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.FILL);
        wallPaint.setARGB(255, (int)(rgb[0]*255.f), (int)(rgb[1]*255.f), (int)(rgb[2]*255.f));
        canvas.drawRect(width*0.50f, height, width*0.50f+32.f, height-32, wallPaint);

         */
    }
    public static void drawWB(float[] rgb, BufferedImage io){
        /*
        float max = 1f;//max(max(rgb[0],rgb[1]),rgb[2]);
        //max = 1.f;
        int width = io.getWidth();
        int height = io.getHeight();
        Canvas canvas = new Canvas(io);
        Paint wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.FILL);
        wallPaint.setARGB(255, (int)(rgb[0]*255.f/max), (int)(rgb[1]*255.f/max), (int)(rgb[2]*255.f/max));
        canvas.drawRect(height, width*0.50f, height-32, width*0.50f+32.f, wallPaint);

         */
    }
    public static void drawArray(float[] input, BufferedImage output){
        /*
        float max = 0.f;
        for(float ccur : input)
            if(ccur > max)
                max = ccur;
        int width = output.getWidth();
        int height = output.getHeight();
        Canvas canvas = new Canvas(output);
        Paint wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setARGB(100, 255, 255, 255);
        canvas.drawRect(0, 0, width, height, wallPaint);
        canvas.drawLine(width / 3.f, 0, width / 3.f, height, wallPaint);
        canvas.drawLine(2.f * width / 3.f, 0, 2.f * width / 3.f, height, wallPaint);
        float xInterval = ((float) width / ((float) input.length + 1));
        Path wallPath = new Path();
        wallPaint.setARGB(255, 255, 255, 255);
        wallPaint.setXfermode(porterDuffXfermode);
        wallPaint.setStyle(Paint.Style.FILL);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < input.length; j++) {
            float value = (((float) input[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(input.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);

         */
    }
    public static void drawArray(int[] input, BufferedImage output){
        /*
        float max = 0.f;
        for(float ccur : input)
            if(ccur > max) max = ccur;
        int width = output.getWidth();
        int height = output.getHeight();
        Canvas canvas = new Canvas(output);
        Paint wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setARGB(100, 255, 255, 255);
        canvas.drawRect(0, 0, width, height, wallPaint);
        canvas.drawLine(width / 3.f, 0, width / 3.f, height, wallPaint);
        canvas.drawLine(2.f * width / 3.f, 0, 2.f * width / 3.f, height, wallPaint);
        float xInterval = ((float) width / ((float) input.length + 1));
        Path wallPath = new Path();
        wallPaint.setARGB(255, 255, 255, 255);
        wallPaint.setXfermode(porterDuffXfermode);
        wallPaint.setStyle(Paint.Style.FILL);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < input.length; j++) {
            float value = (((float) input[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(input.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);

         */
    }
    public static void drawArray(int[] r,int[] g,int[] b, BufferedImage output){
        /*
        int width = output.getWidth();
        int height = output.getHeight();
        Canvas canvas = new Canvas(output);

        float max = 0.f;
        for(float ccur : r)
            if(ccur > max)
                max = ccur;
        Paint wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setARGB(100, 255, 255, 255);
        canvas.drawRect(0, 0, width, height, wallPaint);
        canvas.drawLine(width / 3.f, 0, width / 3.f, height, wallPaint);
        canvas.drawLine(2.f * width / 3.f, 0, 2.f * width / 3.f, height, wallPaint);
        float xInterval = ((float) width / ((float) r.length + 1));
        Path wallPath = new Path();
        wallPaint.setARGB(255, 255, 0, 0);
        wallPaint.setXfermode(porterDuffXfermode);
        wallPaint.setStyle(Paint.Style.FILL);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < r.length; j++) {
            float value = (((float) r[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(r.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);

        max = 0.f;
        for(float ccur : g)
            if(ccur > max)
                max = ccur;
        xInterval = ((float) width / ((float) g.length + 1));
        wallPath = new Path();
        wallPaint.setARGB(255, 0, 255, 0);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < g.length; j++) {
            float value = (((float) g[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(g.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);

        max = 0.f;
        for(float ccur : b)
            if(ccur > max)
                max = ccur;
        xInterval = ((float) width / ((float) b.length + 1));
        wallPath = new Path();
        wallPaint.setARGB(255, 0, 0, 255);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < b.length; j++) {
            float value = (((float) b[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(b.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);
         */
    }

    public static float linearRegressionK(float[] input){
        float k = 0.f;
        float cnt = 0.f;
        for(int i = 1; i<input.length;i++){
            float x = (float)(i)/input.length;
            k+=input[i]/x;
            cnt+=1.f;
        }
        k/=cnt;
        return k;
    }
    public static float linearRegressionC(float[] input){
        float k = 0.f;
        float cnt = 0.f;
        for(int i = 1; i<input.length;i++){
            float x = (float)(i)/input.length;
            k+=input[i]/x;
            cnt+=1.f;
        }
        k/=cnt;
        float c = 0.f;
        cnt = 0.f;
        for(int i = 1; i<input.length;i++){
            float x = (float)(i)/input.length;
            c+=input[i] - x*k;
            cnt+=1.f;
        }
        c/=cnt;
        return c;
    }
    public static void drawArray(float[] r,float[] g,float[] b, BufferedImage output){
        /*int width = output.getWidth();
        int height = output.getHeight();
        Canvas canvas = new Canvas(output);

        float max = 0.f;
        for(float ccur : r)
            if(ccur > max)
                max = ccur;
        for(float ccur : g)
            if(ccur > max)
                max = ccur;
        for(float ccur : b)
            if(ccur > max)
                max = ccur;
        Paint wallPaint = new Paint();
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setARGB(100, 255, 255, 255);
        canvas.drawRect(0, 0, width, height, wallPaint);
        canvas.drawLine(width / 3.f, 0, width / 3.f, height, wallPaint);
        canvas.drawLine(2.f * width / 3.f, 0, 2.f * width / 3.f, height, wallPaint);
        float xInterval = ((float) width / ((float) r.length + 1));
        Path wallPath = new Path();
        wallPaint.setARGB(255, 255, 0, 0);
        wallPaint.setXfermode(porterDuffXfermode);
        wallPaint.setStyle(Paint.Style.FILL);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < r.length; j++) {
            float value = (((float) r[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(r.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);

        max = 0.f;
        for(float ccur : g)
            if(ccur > max)
                max = ccur;
        xInterval = ((float) width / ((float) g.length + 1));
        wallPath = new Path();
        wallPaint.setARGB(255, 0, 255, 0);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < g.length; j++) {
            float value = (((float) g[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(g.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);

        max = 0.f;
        for(float ccur : b)
            if(ccur > max)
                max = ccur;
        xInterval = ((float) width / ((float) b.length + 1));
        wallPath = new Path();
        wallPaint.setARGB(255, 0, 0, 255);
        wallPath.reset();
        wallPath.moveTo(0, height);
        for (int j = 0; j < b.length; j++) {
            float value = (((float) b[j]) * ((float) (height) / max));
            wallPath.lineTo(j * xInterval, height - value);
        }
        wallPath.lineTo(b.length * xInterval, height);
        canvas.drawPath(wallPath, wallPaint);
        */
    }
    public static float[] interpolateArr(float[] in, int requiredSize){
        float[] output = new float[requiredSize];
        ArrayList<Float> mY,mx;
        mY = new ArrayList<>();
        mx = new ArrayList<>();
        for(int xi = 0; xi<in.length; xi++){
            mx.add((float)xi/(float)(in.length-1));
            mY.add(in[xi]);
        }
        SplineInterpolator splineInterpolator = SplineInterpolator.createMonotoneCubicSpline(mx,mY);
        for(int i =0; i<output.length;i++)
            output[i] = splineInterpolator.interpolate(i/(float)(output.length-1));
        return output;
    }
    public static float[] interpolateTonemap(float[] in, int requiredSize){
        float[] output = new float[requiredSize];
        ArrayList<Float> mY,mx;
        mY = new ArrayList<>();
        mx = new ArrayList<>();
        for(int xi = 0; xi<in.length; xi+=2){
            float line = xi / (in.length-1.f);
            mx.add(in[xi]);
            mY.add((float) Math.pow(line,1.0/2.0));
        }
        SplineInterpolator splineInterpolator = SplineInterpolator.createMonotoneCubicSpline(mx,mY);
        for(int i =0; i<output.length;i++)
            output[i] = splineInterpolator.interpolate(i/(float)(output.length-1));
        return output;
    }
    public static float luminocity(float[] in){
        return (in[0]*0.299f+in[1]*0.587f+in[2]*0.114f);
    }
    public static float[] saturate(float[] in, float saturation){
        float br = luminocity(in);
        float[] vec = new float[]{in[0],in[1],in[2]};
        vec[0]=br*(-saturation) + vec[0]*(1.f+saturation);
        vec[1]=br*(-saturation) + vec[1]*(1.f+saturation);
        vec[2]=br*(-saturation) + vec[2]*(1.f+saturation);
        float min = min(min(vec[0],vec[1]),vec[2]);
        /*if(min < 0.f) {
            vec[0] -= min;
            vec[1] -= min;
            vec[2] -= min;
        }*/
        //float br2 = luminocity(vec);
        return vec;
    }
    public static Point div(Point in, int divider){
        return new Point(in.x/divider,in.y/divider);
    }
    public static Point mpy(Point in, int mpy){
        return new Point(in.x*mpy,in.y*mpy);
    }
    public static Point addP(Point in, int add){
        return new Point(in.x+add,in.y+add);
    }

    public static String formatExposureTime(final double value) {
        String output;

        if (value < 1.0f)
            output = String.format(Locale.getDefault(), "%d/%d", 1, (int) (0.5f + 1 / value));
        else {
            final int integer = (int) value;
            final double time = value - integer;
            output = String.format(Locale.getDefault(), "%d''", integer);

            if (time > 0.0001f)
                output += String.format(Locale.getDefault(), " %d/%d", 1, (int) (0.5f + 1 / time));
        }

        return output;
    }
}
