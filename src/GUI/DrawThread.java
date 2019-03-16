package GUI;

public class DrawThread extends Thread {
    private boolean stop;
    private boolean pause;
    private long t1;
    public DrawThread(){
        stop = false;
        pause = true;
    }
    @Override
    public void run() {

        while(true)
        {
            //System.out.println(pause);
            synchronized (this) {
                if (stop) {
                    break;
                }
            }
            t1 = java.lang.System.currentTimeMillis();
            Main.game.repaint();
            try {
                Thread.sleep((20 - t1) > 0 ? 20 - t1 : 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized void stopRedraw()
    {
        stop = true;
    }
}
