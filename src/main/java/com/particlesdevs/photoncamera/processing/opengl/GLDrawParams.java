package com.particlesdevs.photoncamera.processing.opengl;

public class GLDrawParams {
    public static int TileSize = 256;
    public final static int WorkDim = 4;
    public enum Allocate {
        None(),
        Direct(),
        Heap();
    }
}
