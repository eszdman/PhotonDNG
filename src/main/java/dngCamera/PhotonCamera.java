package dngCamera;

import com.particlesdevs.photoncamera.api.Settings;
import com.particlesdevs.photoncamera.processing.processor.JpegProcessor;
import ui.forms.MainUI;
import util.AssetLoader;

import javax.swing.*;

public class PhotonCamera {
    public PhotonCamera(){
        sPhotonCamera = this;
        mAssetLoader = new AssetLoader();
        mSettings = new Settings();
        ProcessingEventsListener processingEventsListener = new ProcessingManager();
        mJpegProcessor = new JpegProcessor(processingEventsListener);
        JFrame frame = new JFrame("PhotonDNG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        mMainUI = new MainUI(frame);
    }
    private static PhotonCamera sPhotonCamera;
    private AssetLoader mAssetLoader;
    private JpegProcessor mJpegProcessor;
    private Settings mSettings;
    private MainUI mMainUI;

    public static AssetLoader getAssetLoader() {
        return sPhotonCamera.mAssetLoader;
    }
    public static Settings getSettings(){
        return sPhotonCamera.mSettings;
    }
    public static JpegProcessor getJpegProcessor(){
        return sPhotonCamera.mJpegProcessor;
    }
    public static MainUI getMainUI(){
        return sPhotonCamera.mMainUI;
    }
}
