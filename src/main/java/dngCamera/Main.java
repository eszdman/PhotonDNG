package dngCamera;

import com.particlesdevs.photoncamera.processing.opengl.*;
import com.particlesdevs.photoncamera.processing.opengl.postpipeline.PostPipeline;
import com.particlesdevs.photoncamera.processing.render.Parameters;
import dngCamera.PhotonCamera;
import dngCamera.parser.DNGReader;
import com.particlesdevs.photoncamera.processing.processor.ByteBufferReader;
import ui.forms.MainUI;
import util.FileManager;
import javax.swing.*;
import java.io.File;
import java.nio.ByteBuffer;


public class Main {
    static int width,height;
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        FileManager.CreateFolders();
        new PhotonCamera(args);

//        Parameters parameters = new Parameters();
//
//        File dng = new File("./IMG_20220316_175154.dng");
//
//        DNGReader dngReader = new DNGReader(ByteBufferReader.read(dng));
//        dngReader.FillParameters(parameters);
//        ByteBuffer rawBuffer =dngReader.ReadRawBuffer();
//
//        PostPipeline pipeline = new PostPipeline();
//
//        GLImage out = pipeline.Run(rawBuffer,parameters);
//
//        out.save(new File("processed.png"));

    }
}
