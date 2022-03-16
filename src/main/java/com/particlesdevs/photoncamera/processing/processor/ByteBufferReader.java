package com.particlesdevs.photoncamera.processing.processor;

import util.Log.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferReader {
    public static ByteBuffer read(File input){
        try {
            FileInputStream fs = new FileInputStream(input);
            byte[] fsBytes = fs.readAllBytes();
            return ByteBuffer.wrap(fsBytes);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ByteBufferReader","Error IOException");
            return ByteBuffer.allocateDirect(0);
        }

    }
}
