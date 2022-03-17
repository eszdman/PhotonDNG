package util;

import com.sun.tools.javac.Main;
import dngCamera.PhotonCamera;
import util.Log.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class AssetLoader {
    private static String TAG = "AssetLoader";
    public AssetLoader() {}

    public InputStream getInputStream(String name) {
        InputStream in
                = getClass().getResourceAsStream(name);
        if(in == null) in = ClassLoader.getSystemClassLoader().getResourceAsStream(name);
        if(in == null){
            try {
                in = new FileInputStream("resources\\"+name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return in;
    }
    public void Test(){
        List<String> filenames = new ArrayList<>();
        try (
                InputStream in = getInputStream("");
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;
            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String s : filenames){
            Log.d(TAG, "s:"+s);
        }
    }
    public String getString(String name) {
        //Test();
        InputStream initialStream = getInputStream(name);
        BufferedReader br = new BufferedReader(new InputStreamReader(initialStream, StandardCharsets.UTF_8));
        String str = null;
        StringBuilder sb = new StringBuilder();
        while (true) {
            try {
                if ((str = br.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append(str).append("\n");
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
