package Model;

import GUI.Main;

public class GameThread extends Thread{
    private boolean roll = false;
    @Override
    public void run()
    {
        while(true)
        {
            boolean temp = false;
            synchronized (this)
            {
                temp = roll;
            }
            if(temp)
            {
                for(int i = 0; i < 1000; i++)
                {
                    Main.game.roll1.move(15);
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                roll = false;
            }
        }
    }
    public synchronized void roll()
    {
        roll = true;
    }
}
