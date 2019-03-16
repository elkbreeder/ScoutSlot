package GUI;

public class DrawThread extends Thread {
    public static final int fps = 30;
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
                Thread.sleep((1000/fps - t1) > 0 ? 1000/fps - t1 : 0);
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
