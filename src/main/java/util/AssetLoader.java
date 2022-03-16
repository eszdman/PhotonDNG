package util;

import util.os.Environment;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AssetLoader {

    public AssetLoader() {}

    public File getFile(String name) throws IOException {
        InputStream initialStream = getInputStream(name);
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);
        File targetFile = new File(name);
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.close();
        return targetFile;
    }

    public InputStream getInputStream(String name) throws IOException {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
    public String getString(String name) {
        InputStream initialStream = null;
        try {
            initialStream = getInputStream(name);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(initialStream, StandardCharsets.UTF_8 ));
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
