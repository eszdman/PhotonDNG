package dngCamera;

import com.particlesdevs.photoncamera.api.Settings;
import util.AssetLoader;

public class PhotonCamera {
    public PhotonCamera(){
        sPhotonCamera = this;
        mAssetLoader = new AssetLoader();
        mSettings = new Settings();
    }
    private static PhotonCamera sPhotonCamera;
    private AssetLoader mAssetLoader;
    private Settings mSettings;

    public static AssetLoader getAssetLoader() {
        return sPhotonCamera.mAssetLoader;
    }
    public static Settings getSettings(){
        return sPhotonCamera.mSettings;
    }
}
