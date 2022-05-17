package com.particlesdevs.photoncamera.processing.opengl;


import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL43.*;

public class GLContext implements AutoCloseable {
    //private Display mDisplay;
    long mWindow;
    //private EGLContext mContext;
    //private EGLSurface mSurface;
    public GLProg mProgram;
    public final int[] bindFB = new int[1];
    public final int[] bindRB = new int[1];

    public GLContext(int surfaceWidth, int surfaceHeight) {
        createContext(surfaceWidth,surfaceHeight);
        //GLUtil.setupDebugMessageCallback();

        glGenFramebuffers(bindFB);
        glGenRenderbuffers(bindRB);
        glBindRenderbuffer(GL_RENDERBUFFER,bindRB[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_RGBA8, surfaceWidth, surfaceHeight);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,bindFB[0]);
        glFramebufferRenderbuffer(GL_DRAW_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, bindRB[0]);

    }
    public void createContext(int surfaceWidth, int surfaceHeight){
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");
        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR,3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR,3);
        glfwWindowHint(GLFW_OPENGL_PROFILE,GLFW_OPENGL_COMPAT_PROFILE);
        //glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT,GLFW_FALSE);


        // Create the window
        mWindow = glfwCreateWindow(surfaceWidth, surfaceHeight, "", NULL, NULL);

        if ( mWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        /*
        glfwSetKeyCallback(mWindow, (windowLocal, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(windowLocal, true); // We will detect this in the rendering loop
        });
         */



        // Make the OpenGL context current
        glfwMakeContextCurrent(mWindow);

        // Make the window visible
        //glfwShowWindow(mWindow);
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        mProgram = new GLProg();
    }

    @Override
    public void close() {
        glDeleteFramebuffers(bindFB);
        glDeleteRenderbuffers(bindRB);
        mProgram.close();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(mWindow);
        glfwDestroyWindow(mWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
