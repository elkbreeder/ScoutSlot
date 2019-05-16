package picview;

import com.jcraft.jsch.SftpException;

public class UpdateThread extends Thread {

    private static final int WAIT_SLEEP = 100;
    private static final int INTERVAL_SLEEP = 20000;
    private boolean end;
    private MainFrame mainFrame;
    private Synchronizer synchronizer;

    public UpdateThread(MainFrame mainFrame, Synchronizer synchronizer) {
        this.mainFrame = mainFrame;
        this.synchronizer = synchronizer;
        start();
    }

    @Override
    public void run() {
        while (!end) {
            while (mainFrame.isLoading() || !(synchronizer.isInitialized() || synchronizer.isInitFailed())) {
                try {
                    Thread.sleep(WAIT_SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                synchronizer.sync();
            } catch (SftpException e) {
                e.printStackTrace();
            }

            mainFrame.updatePics();

            try {
                Thread.sleep(INTERVAL_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void quit() {
        end = true;
    }
}

