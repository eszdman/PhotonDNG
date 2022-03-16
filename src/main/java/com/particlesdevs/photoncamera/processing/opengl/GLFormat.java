package com.particlesdevs.photoncamera.processing.opengl;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL43.*;

public class GLFormat {
    public final int mChannels;
    final DataType mFormat;
    public int filter = GL_NEAREST;
    public int wrap = GL_CLAMP_TO_EDGE;
    public enum DataType {
        NONE(0, 0),
        FLOAT_16(1, 2),
        FLOAT_32(2, 4),
        FLOAT_64(3, 8),
        SIGNED_8(4, 1),
        SIGNED_16(5, 2),
        SIGNED_32(6, 4),
        SIGNED_64(7, 8),
        UNSIGNED_8(8, 1),
        UNSIGNED_16(9, 2),
        UNSIGNED_32(10, 4),
        UNSIGNED_64(11, 8),
        BOOLEAN(12, 1),
        SIMPLE_8(13, 1);
        final int mID;
        public final int mSize;

        DataType(int id, int size) {
            mID = id;
            mSize = size;
        }
        DataType(DataType in) {
            mID = in.mID;
            mSize = in.mSize;
        }
    }
    public GLFormat(GLFormat in){
        mFormat = in.mFormat;
        mChannels = in.mChannels;
    }
    public GLFormat(DataType format) {
        mFormat = format;
        mChannels = 1;
    }
    public int getBufferedImageConfig() {
        switch (mFormat) {
            case NONE:
                break;
            case FLOAT_16:
                return BufferedImage.TYPE_CUSTOM;
            case FLOAT_32:
            case UNSIGNED_16:
                return BufferedImage.TYPE_USHORT_GRAY;
            case UNSIGNED_8:
            case SIGNED_8:
            case SIMPLE_8:
                return BufferedImage.TYPE_INT_ARGB;
        }
        return BufferedImage.TYPE_INT_ARGB;
    }

    public GLFormat(DataType format, int channels) {
        mFormat = format;
        mChannels = channels;
    }
    public int getGLFormatInternal() {
        switch (mFormat) {
            case NONE:
                return -1;
            case FLOAT_16:
                switch (mChannels) {
                    case 1:
                        return GL_R16F;
                    case 2:
                        return GL_RG16F;
                    case 3:
                        return GL_RGB16F;
                    case 4:
                        return GL_RGBA16F;
                }
            case FLOAT_32:
                switch (mChannels) {
                    case 1:
                        return GL_R32F;
                    case 2:
                        return GL_RG32F;
                    case 3:
                        return GL_RGB32F;
                    case 4:
                        return GL_RGBA32F;
                }
            case UNSIGNED_8:
                switch (mChannels) {
                    case 1:
                        return GL_R8UI;
                    case 2:
                        return GL_RG8UI;
                    case 3:
                        return GL_RGB8UI;
                    case 4:
                        return GL_RGBA8UI;
                }
            case UNSIGNED_16:
                switch (mChannels) {
                    case 1:
                        return GL_R16UI;
                    case 2:
                        return GL_RG16UI;
                    case 3:
                        return GL_RGB16UI;
                    case 4:
                        return GL_RGBA16UI;
                }
            case UNSIGNED_32:
                switch (mChannels) {
                    case 1:
                        return GL_R32UI;
                    case 2:
                        return GL_RG32UI;
                    case 3:
                        return GL_RGB32UI;
                    case 4:
                        return GL_RGBA32UI;
                }
            case SIGNED_8:
                switch (mChannels) {
                    case 1:
                        return GL_R8I;
                    case 2:
                        return GL_RG8I;
                    case 3:
                        return GL_RGB8I;
                    case 4:
                        return GL_RGBA8I;
                }
            case SIMPLE_8:
                switch (mChannels) {
                    case 1:
                        return GL_R8;
                    case 2:
                        return GL_RG8;
                    case 3:
                        return GL_RGB8;
                    case 4:
                        return GL_RGBA8;
                }
            case SIGNED_16:
                switch (mChannels) {
                    case 1:
                        return GL_R16I;
                    case 2:
                        return GL_RG16I;
                    case 3:
                        return GL_RGB16I;
                    case 4:
                        return GL_RGBA16I;
                }
            case SIGNED_32:
                switch (mChannels) {
                    case 1:
                        return GL_R32I;
                    case 2:
                        return GL_RG32I;
                    case 3:
                        return GL_RGB32I;
                    case 4:
                        return GL_RGBA32I;
                }
        }
        return 0;
    }


    public int getGLFormatExternal() {
        switch (mFormat) {
            case NONE:
                break;
            case FLOAT_16:
            case FLOAT_32:
            case FLOAT_64:
            case SIGNED_8:
            case SIMPLE_8:
            case UNSIGNED_8:
                switch (mChannels) {
                    case 1:
                        return GL_RED;
                    case 2:
                        return GL_RG;
                    case 3:
                        return GL_RGB;
                    case 4:
                        return GL_RGBA;
                }
            case UNSIGNED_16:
            case UNSIGNED_32:
            case UNSIGNED_64:
            case SIGNED_16:
            case SIGNED_32:
            case SIGNED_64:
                switch (mChannels) {
                    case 1:
                        return GL_RED_INTEGER;
                    case 2:
                        return GL_RG_INTEGER;
                    case 3:
                        return GL_RGB_INTEGER;
                    case 4:
                        return GL_RGBA_INTEGER;
                }
        }
        return 0;
    }

    public int getGLType() {
        switch (mFormat) {
            case FLOAT_32:
            case FLOAT_64:
            case FLOAT_16:
                return GL_FLOAT;
            case UNSIGNED_8:
                return GL_UNSIGNED_BYTE;
            case UNSIGNED_16:
                return GL_UNSIGNED_SHORT;
            case UNSIGNED_32:
                return GL_UNSIGNED_INT;
            case SIGNED_8:
            case SIMPLE_8:
                return GL_BYTE;
            case SIGNED_16:
                return GL_SHORT;
            case SIGNED_32:
                return GL_INT;
        }
        return 0;
    }

    public String getTemVar() {
        switch (mFormat) {
            case NONE:
                break;
            case FLOAT_16:
            case FLOAT_32:
            case FLOAT_64:
                switch (mChannels) {
                    case 1:
                        return "float";
                    case 2:
                        return "vec2";
                    case 3:
                        return "vec3";
                    case 4:
                        return "vec4";
                }

            case UNSIGNED_8:
            case UNSIGNED_16:
            case UNSIGNED_32:
            case UNSIGNED_64:
                switch (mChannels) {
                    case 1:
                        return "uint";
                    case 2:
                        return "uvec2";
                    case 3:
                        return "uvec3";
                    case 4:
                        return "uvec4";
                }
            case SIGNED_8:
            case SIMPLE_8:
            case SIGNED_16:
            case SIGNED_32:
            case SIGNED_64:
                switch (mChannels) {
                    case 1:
                        return "int";
                    case 2:
                        return "ivec2";
                    case 3:
                        return "ivec3";
                    case 4:
                        return "ivec4";
                }
        }
        return "vec4";
    }
    public String getTemExt(){
        switch (mChannels) {
            case 1: return "";
            case 2: return ".rg";
            case 3: return ".rgb";
            case 4: return ".rgba";
        }
        return ".rgba";
    }
    public String getLimExt(){
        switch (mChannels) {
            case 1: return "";
            case 2: return ".rg";
            case 3:
            case 4: return ".rgb";
        }
        return ".rgb";
    }
    public String getScalar() {
        switch (mFormat) {
            case NONE:
                break;
            case FLOAT_16:
            case FLOAT_32:
            case FLOAT_64:
                return "float";
            case UNSIGNED_8:
            case UNSIGNED_16:
            case UNSIGNED_32:
            case UNSIGNED_64:
                return "uint";
            case SIGNED_8:
            case SIMPLE_8:
            case SIGNED_16:
            case SIGNED_32:
            case SIGNED_64:
                return "int";
        }
        return "float";
    }

    public String getTemSamp() {
        switch (mFormat) {
            case FLOAT_32:
            case FLOAT_64:
            case FLOAT_16:
                return "sampler2D";
            case SIGNED_8:
            case SIMPLE_8:
            case SIGNED_16:
            case SIGNED_32:
            case SIGNED_64:
                return "isampler2D";
            case UNSIGNED_8:
            case UNSIGNED_16:
            case UNSIGNED_32:
            case UNSIGNED_64:
                return "usampler2D";
        }
        return "sampler2D";
    }

    @Override
    public String toString() {
        return "GLFormat{" +
                "mChannels=" + mChannels +
                ", mFormat=" + mFormat +
                '}';
    }
}