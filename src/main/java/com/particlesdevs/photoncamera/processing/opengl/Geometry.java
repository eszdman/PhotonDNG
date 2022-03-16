package com.particlesdevs.photoncamera.processing.opengl;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Geometry {
    public static void CreateVAO(int[] geoVAO, int geoVBO)
    {
        if (geoVAO == null){
            return;
        }
        if(geoVAO.length > 0)
            glDeleteVertexArrays(geoVAO);

        glGenVertexArrays(geoVAO);
        glBindVertexArray(geoVAO[0]);
        glBindBuffer(GL_ARRAY_BUFFER, geoVBO);
        float[] pos = new float[18];
        // POSITION
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, FloatBuffer.wrap(pos));
        glEnableVertexAttribArray(0);

        float[] norm = new float[18];
        // NORMAL
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, FloatBuffer.wrap(norm));
        glEnableVertexAttribArray(1);

        float[] tex = new float[18];
        // TEXCOORD
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, FloatBuffer.wrap(tex));
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    public static int CreatePlane(int[] vbo, float sx, float sy)
    {
        float halfX = sx / 2;
        float halfY = sy / 2;

        float[] planeData = {
                halfX,
                halfY,
                0,
                0.0f,
                0.0f,
                1.0f,
                1.0f,
                1.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                1,
                1,
                halfX,
                -halfY,
                0,
                0.0f,
                0.0f,
                1.0f,
                1.0f,
                0.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                1,
                1,
                -halfX,
                -halfY,
                0,
                0.0f,
                0.0f,
                1.0f,
                0.0f,
                0.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                1,
                1,
                -halfX,
                halfY,
                0,
                0.0f,
                0.0f,
                1.0f,
                0.0f,
                1.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                1,
                1,
                halfX,
                halfY,
                0,
                0.0f,
                0.0f,
                1.0f,
                1.0f,
                1.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                1,
                1,
                -halfX,
                -halfY,
                0,
                0.0f,
                0.0f,
                1.0f,
                0.0f,
                0.0f,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                1,
                1,
                1,
        };

        // create vbo
        glGenBuffers(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferData(GL_ARRAY_BUFFER, planeData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        int[] vao = new int[1];
        CreateVAO(vao, vbo[0]);

        return vao[0];
    }
    public static int CreateScreenQuadNDC(int[] vbo)
    {
        float[] sqData = {
                -1,
                -1,
                0.0f,
                0.0f,
                1,
                -1,
                1.0f,
                0.0f,
                1,
                1,
                1.0f,
                1.0f,
                -1,
                -1,
                0.0f,
                0.0f,
                1,
                1,
                1.0f,
                1.0f,
                -1,
                1,
                0.0f,
                1.0f,
        };

        int[] vao = new int[1];

        // create vao
        glGenVertexArrays(vao);
        glBindVertexArray(vao[0]);

        // create vbo
        glGenBuffers(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);

        // vbo data
        glBufferData(GL_ARRAY_BUFFER, sqData, GL_STATIC_DRAW);

        float[] pos = new float[4];
        // vertex positions
        glVertexAttribPointer(0, 2, GL_FLOAT, false,0, FloatBuffer.wrap(pos));
        glEnableVertexAttribArray(0);

        float[] coords = new float[4];
        // vertex texture coords
        glVertexAttribPointer(1, 2, GL_FLOAT, false,0, FloatBuffer.wrap(coords));
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        return vao[0];
    }
}
