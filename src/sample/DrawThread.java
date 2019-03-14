package sample;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class DrawThread extends Thread {
    boolean stop;
    long t1;
    public DrawThread(){
        stop = false;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(true)
        {
            if(stop)break;
            Main.game.x = Main.game.x + 1;
            t1 = java.lang.System.currentTimeMillis();
            Main.game.repaint();
            try {
                Thread.sleep((20-t1)>0?20-t1:0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Draw");


        }
    }
    public void stopRedraw()
    {
        stop = true;
    }
}
