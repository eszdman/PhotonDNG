package com.particlesdevs.photoncamera.processing.processor;

import dngCamera.ProcessingEventsListener;
import com.particlesdevs.photoncamera.processing.opengl.GLImage;
import com.particlesdevs.photoncamera.processing.opengl.postpipeline.PostPipeline;
import com.particlesdevs.photoncamera.processing.render.Parameters;
import dngCamera.parser.DNGReader;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class FileProcessor {
    protected final ProcessingEventsListener processingEventsListener;
    private static final String TAG = "JpegProcessor";
    public String namePatch = "processed";
    private ArrayList<ByteBuffer> mByteBuffersToProcess = new ArrayList<>();

    public FileProcessor(ProcessingEventsListener processingEventsListener) {
        this.processingEventsListener = processingEventsListener;
    }

    public void Add(ByteBuffer buffer){
        if(Math.max(buffer.capacity(),buffer.remaining()) > 1){
            mByteBuffersToProcess.add(buffer);
        }
    }

    public GLImage Run(){
        PostPipeline pipeline = new PostPipeline();
        Parameters parameters = new Parameters();
        GLImage last = null;
        for(int i =0; i<mByteBuffersToProcess.size();i++){
            DNGReader dngReader = new DNGReader(mByteBuffersToProcess.get(i));
            dngReader.FillParameters(parameters);
            last = pipeline.Run(dngReader.ReadRawBuffer(),parameters);
            //last.save(new File(namePatch+i+".png"));
        }
        mByteBuffersToProcess.clear();
        pipeline.close();
        return last;
    }
}
