package ui.forms;

import com.particlesdevs.photoncamera.processing.ImagePath;
import dngCamera.PhotonCamera;
import com.particlesdevs.photoncamera.processing.processor.ByteBufferReader;

import javax.swing.*;
import java.io.File;

public class MainUI {
    public JPanel mainPanel;
    private JMenu File;
    private JMenu Edit;
    private JMenuItem OpenDNG;
    private JFrame frame;
    public MainUI(JFrame frame){
        this.frame = frame;
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
        OpenDNG.addActionListener(e -> {
            JFileChooser j = new JFileChooser(new File("./"));

            int r = j.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION){
                PhotonCamera.getJpegProcessor().Add(ByteBufferReader.read(j.getSelectedFile()));
                PhotonCamera.getJpegProcessor().namePatch = j.getSelectedFile().getName();
                PhotonCamera.getJpegProcessor().Run();
            }
        });
    }
}
