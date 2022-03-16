package com.particlesdevs.photoncamera.processing.opengl;

import com.particlesdevs.photoncamera.processing.opengl.GLSquareModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL30.*;

public class Program {
    private int mCurrentProgramActive;
    private final GLSquareModel mSquare = new GLSquareModel();
    static String shaderVersions = "#version 330";
    public Program(String sourceName){
        mCurrentProgramActive = initShader(sourceName,false);
    }
    public void useProgram(){
        glUseProgram(mCurrentProgramActive);
    }
    private int vPosition() {
        return glGetAttribLocation(mCurrentProgramActive, "vPosition");
    }
    public String loadShader(String name) throws IOException {
        File shader = new File("shaders/"+name);
        return shaderVersions+"\n"+ Files.readString(shader.toPath());
    }
    /**
     * Helper function to compile a shader.
     *
     * @param shaderType   The shader type.
     * @param shaderSource The shader source code.
     * @return An OpenGL handle to the shader.
     */
    public int compileShader(final int shaderType, final String shaderSource) {
        int shaderHandle = glCreateShader(shaderType);
        if (shaderHandle != 0) {
            // Pass in the shader source.
            glShaderSource(shaderHandle, shaderSource);
            // Compile the shader.
            glCompileShader(shaderHandle);
            // Get the compilation status.
            final int[] compileStatus = new int[1];
            glGetShaderiv(shaderHandle, GL_COMPILE_STATUS, compileStatus);
            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                System.err.println("Error compiling shader: " + glGetShaderInfoLog(shaderHandle));
                glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }
        if (shaderHandle == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        return shaderHandle;
    }
    private static int baseVert = 0;
    private int initShader(String shaderName, boolean useBaseVertex){
        int programHandle = glCreateProgram();
        if(!useBaseVertex) {
            try {
                glAttachShader(programHandle, compileShader(GL_VERTEX_SHADER, loadShader(shaderName + ".vert")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if(baseVert ==0){
                try {
                    baseVert = compileShader(GL_VERTEX_SHADER, loadShader("base.vert"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            glAttachShader(programHandle,baseVert);
        }
        try {
            glAttachShader(programHandle, compileShader(GL_FRAGMENT_SHADER, loadShader(shaderName + ".frag")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return programHandle;
    }
    public void draw() {
        mSquare.draw(vPosition());
        glFlush();
    }
}
