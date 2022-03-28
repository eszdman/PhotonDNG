package dngCamera;

import com.particlesdevs.photoncamera.api.Settings;
import com.particlesdevs.photoncamera.processing.processor.ByteBufferReader;
import com.particlesdevs.photoncamera.processing.processor.FileProcessor;
import ui.forms.MainUI;
import util.AssetLoader;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;

public class PhotonCamera {
    public PhotonCamera(String[] args){
        sPhotonCamera = this;
        mAssetLoader = new AssetLoader();
        mSettings = new Settings();
        ProcessingEventsListener processingEventsListener = new ProcessingManager();
        mJpegProcessor = new FileProcessor(processingEventsListener);
        System.out.println("args:"+ Arrays.toString(args));
        if(args.length < 1) {
            JFrame frame = new JFrame("PhotonDNG");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 300);
            mMainUI = new MainUI(frame);
        } else {
            File consoleF = new File(args[0]);
            PhotonCamera.getJpegProcessor().Add(ByteBufferReader.read(consoleF));
            PhotonCamera.getJpegProcessor().namePatch = consoleF.getName();
            PhotonCamera.getJpegProcessor().Run();
        }
    }
    private static PhotonCamera sPhotonCamera;
    private AssetLoader mAssetLoader;
    private FileProcessor mJpegProcessor;
    private Settings mSettings;
    private MainUI mMainUI;

    public static AssetLoader getAssetLoader() {
        return sPhotonCamera.mAssetLoader;
    }
    public static Settings getSettings(){
        return sPhotonCamera.mSettings;
    }
    public static FileProcessor getJpegProcessor(){
        return sPhotonCamera.mJpegProcessor;
    }
    public static MainUI getMainUI(){
        return sPhotonCamera.mMainUI;
    }
}
