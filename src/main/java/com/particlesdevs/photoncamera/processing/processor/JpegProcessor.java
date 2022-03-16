package com.particlesdevs.photoncamera.processing.processor;

import dngCamera.ProcessingEventsListener;
import com.particlesdevs.photoncamera.processing.opengl.GLImage;
import com.particlesdevs.photoncamera.processing.opengl.postpipeline.PostPipeline;
import com.particlesdevs.photoncamera.processing.render.Parameters;
import dngCamera.parser.DNGReader;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class JpegProcessor {
    protected final ProcessingEventsListener processingEventsListener;
    private static final String TAG = "JpegProcessor";
    public String namePatch = "processed";
    private ArrayList<ByteBuffer> mByteBuffersToProcess = new ArrayList<>();

    public JpegProcessor(ProcessingEventsListener processingEventsListener) {
        this.processingEventsListener = processingEventsListener;
    }

    public void Add(ByteBuffer buffer){
        if(Math.max(buffer.capacity(),buffer.remaining()) > 1){
            mByteBuffersToProcess.add(buffer);
        }
    }
    public void Run(){
        int cnt = 0;
        PostPipeline pipeline = new PostPipeline();
        Parameters parameters = new Parameters();

        for (ByteBuffer buffer : mByteBuffersToProcess){
            DNGReader dngReader = new DNGReader(buffer);
            dngReader.FillParameters(parameters);
            GLImage out = pipeline.Run(dngReader.ReadRawBuffer(),parameters);
            out.save(new File(namePatch+cnt+".png"));
            cnt++;
        }
    }
}
