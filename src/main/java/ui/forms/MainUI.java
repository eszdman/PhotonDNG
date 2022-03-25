package ui.forms;

import com.particlesdevs.photoncamera.processing.opengl.GLImage;
import dngCamera.PhotonCamera;
import com.particlesdevs.photoncamera.processing.processor.ByteBufferReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainUI {
    public JPanel mainPanel;
    private JMenu File;
    private JMenu Edit;
    private JMenuItem OpenDNG;
    private JButton reprocessButton;
    private JPanel rawDraw;
    private JMenuItem SavePNG;
    private JFrame frame;
    private File lastFile;
    private GLImage lastImage;
    public MainUI(JFrame frame){
        this.frame = frame;
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
        OpenDNG.addActionListener(e -> {
            JFileChooser j = new JFileChooser(new File("./"));
            int r = j.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION){
                PhotonCamera.getJpegProcessor().Add(ByteBufferReader.read(j.getSelectedFile()));
                lastFile = j.getSelectedFile();
                PhotonCamera.getJpegProcessor().namePatch = j.getSelectedFile().getName();
                lastImage = PhotonCamera.getJpegProcessor().Run();
                BufferedImage bufferedImage = lastImage.getBufferedImage();
                rawDraw.getGraphics().drawImage(bufferedImage,0,0,rawDraw.getWidth(),rawDraw.getHeight(),null);
            }
        });
        SavePNG.addActionListener(e -> {
            JFileChooser j = new JFileChooser(new File("./"));
            int r = j.showOpenDialog(null);
            if(r == JFileChooser.APPROVE_OPTION){
                lastImage.save(new File(j.getSelectedFile().getName()+".png"));
            }
        });
        reprocessButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if(lastFile != null && lastFile.exists()){
                    PhotonCamera.getJpegProcessor().Add(ByteBufferReader.read(lastFile));
                    PhotonCamera.getJpegProcessor().namePatch = lastFile.getName();
                    lastImage = PhotonCamera.getJpegProcessor().Run();
                    BufferedImage bufferedImage = lastImage.getBufferedImage();
                    rawDraw.getGraphics().drawImage(bufferedImage,0,0,rawDraw.getWidth(),rawDraw.getHeight(),null);
                }
            }
        });
    }
}
