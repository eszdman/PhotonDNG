package ui.forms;

import javax.swing.*;

public class MainUI {
    public JPanel mainPanel;
    private JMenu File;
    private JMenu Edit;
    private JFrame frame;
    public MainUI(JFrame frame){
        this.frame = frame;
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
