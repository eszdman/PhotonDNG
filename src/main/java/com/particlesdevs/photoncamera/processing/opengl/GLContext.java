package com.particlesdevs.photoncamera.processing.opengl;


import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLContext implements AutoCloseable {
    //private Display mDisplay;
    long mWindow;
    //private EGLContext mContext;
    //private EGLSurface mSurface;
    public GLProg mProgram;

    public GLContext(int surfaceWidth, int surfaceHeight) {
        createContext(surfaceWidth,surfaceHeight);
        GLUtil.setupDebugMessageCallback();
    }
    public void createContext(int surfaceWidth, int surfaceHeight){
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        //glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable


        // Create the window
        mWindow = glfwCreateWindow(surfaceWidth, surfaceHeight/2, "GLFW IMGPROC CONTEXT", NULL, NULL);

        if ( mWindow == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        /*
        glfwSetKeyCallback(mWindow, (windowLocal, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(windowLocal, true); // We will detect this in the rendering loop
        });
         */

        /*
        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(mWindow, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());


            // Center the window
        } // the stack frame is popped automatically

         */

        // Make the OpenGL context current
        glfwMakeContextCurrent(mWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        //glfwShowWindow(mWindow);
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        mProgram = new GLProg();
    }

    @Override
    public void close() {
        mProgram.close();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(mWindow);
        glfwDestroyWindow(mWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
