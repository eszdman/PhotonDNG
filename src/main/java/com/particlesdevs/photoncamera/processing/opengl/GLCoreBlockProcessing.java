package com.particlesdevs.photoncamera.processing.opengl;

import util.Log.Log;

import static org.lwjgl.opengl.GL43.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class GLCoreBlockProcessing extends GLContext {
    private static String TAG = "GLCoreBlockProcessing";
    public GLImage mOut = null;
    private final int mOutWidth, mOutHeight;
    public ByteBuffer mBlockBuffer;
    public ByteBuffer mOutBuffer;
    private final GLFormat mglFormat;

    public GLDrawParams.Allocate allocation = GLDrawParams.Allocate.Heap;

    public static void checkEglError(String op) {
        int error = glGetError();
        if (error != GL_NO_ERROR) {
            //String msg = op + ": glError: " + GLUtils.getEGLErrorString(error) + " (" + Integer.toHexString(error) + ")";
            String TAG = "GLCoreBlockProcessing";
            Log.v(TAG, "Error"+error);
        }
    }
    public GLCoreBlockProcessing(Point size, GLImage out, GLFormat glFormat, GLDrawParams.Allocate alloc) {
        this(size, glFormat,alloc);
        mOut = out;
    }
    public GLCoreBlockProcessing(Point size, GLImage out, GLFormat glFormat) {
        this(size, glFormat, GLDrawParams.Allocate.Heap);
        mOut = out;
    }
    public GLCoreBlockProcessing(Point size, GLFormat glFormat) {
        this(size,glFormat, GLDrawParams.Allocate.Heap);
    }
    public GLCoreBlockProcessing(Point size, GLFormat glFormat, GLDrawParams.Allocate alloc) {
        super(size.x, GLDrawParams.TileSize);
        mglFormat = glFormat;
        mOutWidth = size.x;
        mOutHeight = size.y;
        mBlockBuffer = ByteBuffer.allocateDirect(mOutWidth * GLDrawParams.TileSize * mglFormat.mFormat.mSize * mglFormat.mChannels);
        final int capacity = mOutWidth * mOutHeight * mglFormat.mFormat.mSize * mglFormat.mChannels;
        if(alloc == GLDrawParams.Allocate.None) return;
        if(alloc == GLDrawParams.Allocate.Direct) mOutBuffer = ByteBuffer.allocateDirect(capacity);
        else {
            mOutBuffer = ByteBuffer.allocate(capacity);
        }
    }
    public GLCoreBlockProcessing(Point size, GLImage out, GLFormat glFormat,ByteBuffer output) {
        super(size.x, GLDrawParams.TileSize);
        output.position(0);
        mglFormat = glFormat;
        mOutWidth = size.x;
        mOutHeight = size.y;
        mBlockBuffer = ByteBuffer.allocate(mOutWidth * GLDrawParams.TileSize * mglFormat.mFormat.mSize * mglFormat.mChannels);
        mOutBuffer = output;
        mOut = out;
    }

    public void drawBlocksToOutput() {
        GLProg program = super.mProgram;
        GLBlockDivider divider = new GLBlockDivider(mOutHeight, GLDrawParams.TileSize);
        int[] row = new int[2];
        mOutBuffer.position(0);
        mBlockBuffer.position(0);
        while (divider.nextBlock(row)) {
            int y = row[0];
            int height = row[1];
            glViewport(0, 0, mOutWidth, height);
            checkEglError("glViewport");
            program.setVar("yOffset", y);
            program.draw();
            checkEglError("program");
            mBlockBuffer.position(0);
            glReadPixels(0, 0, mOutWidth, height, mglFormat.getGLFormatExternal(), mglFormat.getGLType(), mBlockBuffer);
            checkEglError("glReadPixels");
            if (height < GLDrawParams.TileSize) {
                // This can only happen 2 times at edges
                byte[] data = new byte[mOutWidth * height * mglFormat.mFormat.mSize * mglFormat.mChannels];
                mBlockBuffer.get(data);
                mOutBuffer.put(data);
            } else {
                mOutBuffer.put(mBlockBuffer);
            }
        }
        mOutBuffer.position(0);
        mBlockBuffer = null;
        if (mOut != null) mOut.byteBuffer.put(mOutBuffer);
    }

    private final int[] bind = new int[1];
    public ByteBuffer drawBlocksToOutput(Point size, GLFormat glFormat) {
        return drawBlocksToOutput(size,glFormat, GLDrawParams.Allocate.Direct);
    }
    public ByteBuffer drawBlocksToOutput(Point size, GLFormat glFormat,GLDrawParams.Allocate alloc) {
        ByteBuffer mOutBuffer;
        if(alloc == GLDrawParams.Allocate.Direct) mOutBuffer = ByteBuffer.allocateDirect(size.x * size.y * glFormat.mFormat.mSize * glFormat.mChannels);
        else
            mOutBuffer = ByteBuffer.allocate(size.x * size.y * glFormat.mFormat.mSize * glFormat.mChannels);
        return drawBlocksToOutput(size,glFormat,mOutBuffer);
    }
    ByteBuffer mBlockBuffert = null;
    public ByteBuffer drawBlocksToOutput(Point size, GLFormat glFormat,ByteBuffer mOutBuffer) {
        glBindFramebuffer(GL_FRAMEBUFFER, bind[0]);
        checkEglError("glBindFramebuffer");
        GLProg program = super.mProgram;
        GLBlockDivider divider = new GLBlockDivider(size.y, GLDrawParams.TileSize);
        int[] row = new int[2];
        ByteBuffer mBlockBuffert = mBlockBuffer;
        //if(mBlockBuffert == null ||
        //        mBlockBuffert.position(0).capacity() < size.x * GLDrawParams.TileSize * glFormat.mFormat.mSize * glFormat.mChannels)
        //    mBlockBuffert = ByteBuffer.allocate((size.x) * GLDrawParams.TileSize * glFormat.mFormat.mSize * glFormat.mChannels * 2);
        //Log.d(TAG,"mBlockBuffert:"+mBlockBuffert.toString());
        mOutBuffer.position(0);
        mBlockBuffert.position(0);
        while (divider.nextBlock(row)) {
            int y = row[0];
            int height = row[1];
            glViewport(0, 0, size.x, height);
            checkEglError("glViewport");
            program.setVar("yOffset", y);
            program.draw();
            checkEglError("program");
            mBlockBuffert.position(0);
            glReadPixels(0, 0, size.x, height, glFormat.getGLFormatExternal(), glFormat.getGLType(), mBlockBuffert);
            checkEglError("glReadPixels");
            if (height < GLDrawParams.TileSize) {
                // This can only happen 2 times at edges
                byte[] data = new byte[size.x * height * glFormat.mFormat.mSize * glFormat.mChannels];
                mBlockBuffert.get(data);
                mOutBuffer.put(data);
            } else {
                int lim = mBlockBuffert.limit();
                mOutBuffer.put((ByteBuffer) mBlockBuffert.limit(size.x * GLDrawParams.TileSize * glFormat.mFormat.mSize * glFormat.mChannels));
                mBlockBuffert.limit(lim);
            }
        }
        mOutBuffer.position(0);
        //mBlockBuffer = null;
        return mOutBuffer;
    }
}
