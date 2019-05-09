package picview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

public class DiaFrame extends JFrame implements WindowListener {

    private static final int SLEEPING_TIME = 5000;
    private File directory;
    private boolean end;

    public DiaFrame(File dir){
        directory = dir;

        // Configure Frame
        setSize(600, 400);
        setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(this);

        new CycleThread();
    }

    private File[] loadFiles(){
        return directory.listFiles(
                MyFilenameFilter.imageFilter());
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        end = true;
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }

    private class CycleThread extends Thread {

        public CycleThread(){
            start();
        }

        @Override
        public void run() {
            super.run();

            while (!end){
                File[] files = loadFiles();
                for (int i = 0; i < files.length; i++) {
                    if (end) {
                        break;
                    }
                    try {
                        PicPane panel = new PicPane(files[i], getHeight());
                        getContentPane().removeAll();
                        getContentPane().add(panel);
                        ((JPanel)getContentPane()).updateUI();
                        Thread.sleep(SLEEPING_TIME);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
