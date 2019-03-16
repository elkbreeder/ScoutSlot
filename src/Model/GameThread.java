package Model;

import GUI.Main;

public class GameThread extends Thread{
    private boolean roll = false;
    @Override
    public void run()
    {
        while(true)
        {
            if(roll)
            {
                for(int i = 0; i < 100; i++)
                {
                    Main.game.roll1.move(10);
                    try {
                        Thread.sleep(100);
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
