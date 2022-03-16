package com.particlesdevs.photoncamera.processing.opengl;



import util.Log.Log;

import java.awt.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL43.*;
import static com.particlesdevs.photoncamera.processing.opengl.GLCoreBlockProcessing.checkEglError;


public class GLTexture implements AutoCloseable {
    public Point mSize;
    public final int mGLFormat;
    public final int mTextureID;
    public int mBuffer;
    public boolean isBuffered = false;
    private static int count = 0;
    public final GLFormat mFormat;
    private static boolean[] ids = new boolean[256];
    public GLTexture(GLTexture in,GLFormat format) {
        this(in.mSize,new GLFormat(format),null,in.mFormat.filter,in.mFormat.wrap,0);
    }
    public GLTexture(GLTexture in) {
        this(in.mSize,in.mFormat,null,in.mFormat.filter,in.mFormat.wrap,0);
    }
    public GLTexture(int sizeX, int sizeY, GLFormat glFormat, Buffer pixels) {
        this(new Point(sizeX, sizeY), new GLFormat(glFormat), pixels, GL_NEAREST, GL_CLAMP_TO_EDGE,0);
    }
    public GLTexture(int sizeX, int sizeY, GLFormat glFormat, Buffer pixels,int textureFilter, int textureWrapper) {
        this(new Point(sizeX, sizeY), new GLFormat(glFormat), pixels, textureFilter, textureWrapper,0);
    }
    public GLTexture(Point size, GLFormat glFormat, Buffer pixels,int textureFilter, int textureWrapper) {
        this(new Point(size), new GLFormat(glFormat), pixels, textureFilter, textureWrapper,0);
    }
    public GLTexture(Point size, GLFormat glFormat, Buffer pixels) {
        this(new Point(size), new GLFormat(glFormat), pixels, GL_NEAREST, GL_CLAMP_TO_EDGE,0);
    }
    public GLTexture(int sizeX, int sizeY, GLFormat glFormat,int level) {
        this(new Point(sizeX, sizeY), new GLFormat(glFormat), null, GL_NEAREST, GL_CLAMP_TO_EDGE,level);
    }
    public GLTexture(int sizeX, int sizeY, GLFormat glFormat) {
        this(new Point(sizeX, sizeY), new GLFormat(glFormat), null, GL_NEAREST, GL_CLAMP_TO_EDGE,0);
    }
    public GLTexture(int sizeX, int sizeY, GLFormat glFormat,int textureFilter, int textureWrapper) {
        this(new Point(sizeX, sizeY), new GLFormat(glFormat), null, textureFilter, textureWrapper,0);
    }
    public GLTexture(Point size, GLFormat glFormat,int level) {
        this(new Point(size), new GLFormat(glFormat), null, GL_NEAREST, GL_CLAMP_TO_EDGE,level);
    }
    public GLTexture(Point size, GLFormat glFormat) {
        this(new Point(size), new GLFormat(glFormat), null, glFormat.filter, glFormat.wrap,0);
    }
    public GLTexture(Point point, GLFormat glFormat, int textureFilter, int textureWrapper) {
        this(new Point(point),new GLFormat(glFormat),null,textureFilter,textureWrapper);
    }
    public GLTexture(GLImage bmp){
        this(bmp,0);
    }
    public GLTexture(GLImage bmp,int level){
        this(bmp,GL_NEAREST,GL_CLAMP_TO_EDGE,level);
    }
    public GLTexture(GLImage bmp, int textureFilter, int textureWrapper,int level) {
        this.mSize = bmp.size;
        this.mFormat = bmp.glFormat;
        this.mGLFormat = mFormat.getGLFormatInternal();
        mFormat.filter = textureFilter;
        mFormat.wrap = textureWrapper;
        int[] TexID = new int[1];
        for(int i = 1; i<ids.length;i++){
            if(!ids[i]){
                Log.d("GLTexture","get:"+i);
                if(count < i){
                    count = i;
                    glGenTextures(TexID);
                }
                TexID[0] = i;
                ids[i] = true;
                break;
            }
        }
        mTextureID = TexID[0];
        //Log.d("GLTexture","Size:"+size+" ID:"+mTextureID);
        glActiveTexture(GL_TEXTURE1+mTextureID);
        glBindTexture(GL_TEXTURE_2D, mTextureID);
        glTexStorage2D(GL_TEXTURE_2D, 1, mFormat.getGLFormatInternal(),  mSize.x, mSize.y);
        checkEglError("glTexStorage2D");
        glTexSubImage2D(GL_TEXTURE_2D, level,0,0,mSize.x,mSize.y,mFormat.getGLFormatExternal(),mFormat.getGLType(), bmp.byteBuffer);
        checkEglError("glTexSubImage2D");
        reSetParameters();
        checkEglError("Tex glTexParameter");
    }
    public GLTexture(Point size, GLFormat glFormat, Buffer pixels, int textureFilter, int textureWrapper,int level) {
        mFormat = glFormat;
        mFormat.filter = textureFilter;
        mFormat.wrap = textureWrapper;
        this.mSize = size;
        this.mGLFormat = glFormat.getGLFormatInternal();
        int[] TexID = new int[1];
        for(int i = 1; i<ids.length;i++){
            if(!ids[i]){
                Log.d("GLTexture","get:"+i);
                if(count < i){
                    count = i;
                    glGenTextures(TexID);
                }
                TexID[0] = i;
                ids[i] = true;
                break;
            }
        }
        mTextureID = TexID[0];
        //Log.d("GLTexture","Size:"+size+" ID:"+mTextureID);
        glActiveTexture(GL_TEXTURE1+mTextureID);
        glBindTexture(GL_TEXTURE_2D, mTextureID);
        glTexStorage2D(GL_TEXTURE_2D, 1, glFormat.getGLFormatInternal(),  size.x, size.y);
        checkEglError("glTexStorage2D");
        if(pixels != null)
            glTexSubImage2D(GL_TEXTURE_2D, level, 0, 0, size.x, size.y, glFormat.getGLFormatExternal(), glFormat.getGLType(), (ByteBuffer) pixels);
        else
            glTexSubImage2D(GL_TEXTURE_2D, level, 0, 0, size.x, size.y, glFormat.getGLFormatExternal(), glFormat.getGLType(), 0);
        checkEglError("glTexSubImage2D");
        reSetParameters();
        checkEglError("Tex glTexParameter");
    }
    void reSetParameters(){
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, mFormat.filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, mFormat.filter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, mFormat.wrap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, mFormat.wrap);
    }
    public void Bufferize(){
        if(!isBuffered) {
            int[] frameBuffer = new int[1];
            glGenFramebuffers(frameBuffer);
            mBuffer = frameBuffer[0];
            isBuffered = true;
        }
    }

    public void BindBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER, mBuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mTextureID, 0);
    }

    public void BufferLoad() {
        Bufferize();
        glBindFramebuffer(GL_FRAMEBUFFER, mBuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mTextureID, 0);
        glViewport(0, 0, mSize.x, mSize.y);
        checkEglError("Tex BufferLoad");
    }

    public void bind(int slot) {
        glActiveTexture(slot);
        glBindTexture(GL_TEXTURE_2D, mTextureID);
        checkEglError("Tex bind");
    }

    public void textureBuffer(GLFormat outputFormat,ByteBuffer output) {
        glReadPixels(0, 0, mSize.x, mSize.y, outputFormat.getGLFormatExternal(), outputFormat.getGLType(), output);
    }

    public ByteBuffer textureBuffer(GLFormat outputFormat,boolean direct) {
        ByteBuffer buffer;
        if(!direct) buffer = ByteBuffer.allocate(mSize.x * mSize.y * outputFormat.mFormat.mSize * outputFormat.mChannels);
        else buffer = ByteBuffer.allocateDirect(mSize.x * mSize.y * outputFormat.mFormat.mSize * outputFormat.mChannels);
        glReadPixels(0, 0, mSize.x, mSize.y, outputFormat.getGLFormatExternal(), outputFormat.getGLType(), buffer);
        return buffer;
    }
    public ByteBuffer textureBuffer(GLFormat outputFormat) {
        ByteBuffer buffer = ByteBuffer.allocate(mSize.x * mSize.y * outputFormat.mFormat.mSize * outputFormat.mChannels);
        glReadPixels(0, 0, mSize.x, mSize.y, outputFormat.getGLFormatExternal(), outputFormat.getGLType(), buffer);
        return buffer;
    }
    public int getByteCount(){
        return mSize.x * mSize.y * mFormat.mFormat.mSize * mFormat.mChannels;
    }


    @Override
    public String toString() {
        return "GLTexture{" +
                "mSize=" + mSize +
                ", mGLFormat=" + mGLFormat +
                ", mTextureID=" + mTextureID +
                ", mFormat=" + mFormat +
                '}';
    }
    public static void notClosed(){
        StringBuilder str = new StringBuilder();
        for(int i =0; i<ids.length;i++){
            if(ids[i]) {
                str.append(i);
                str.append(" ");
            }
        }
        Log.d("GLTexture","notClosed:"+str.toString());
    }

    @Override
    public void close() {
        glDeleteTextures(new int[]{mTextureID});
        ids[mTextureID] = false;
        //Log.d("GLTexture","close ID:"+mTextureID);
        if(isBuffered) glDeleteBuffers(new int[]{mBuffer});
    }
}
