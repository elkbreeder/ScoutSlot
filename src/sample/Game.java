package sample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;

public class Game extends JPanel implements KeyListener {
    private DrawThread drawThread;
    public ImageIcon img;
    public int x,y = 0;
    public Game()
    {
        img = new ImageIcon("pic.jpeg");
        Image image = img.getImage(); // transform it
        Image newimg = image.getScaledInstance(50, 50,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        img = new ImageIcon(newimg);
        drawThread = new DrawThread();
        drawThread.start();

    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        img.paintIcon(this, g, x,y);
    }
    public void stop()
    {
        drawThread.stopRedraw();
    }
    /*
    *
    * Keypressing Part
    * */
    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == 'w')
        {
            drawThread.start();
        }
        if(e.getKeyChar() == 'a')
        {
            drawThread.stopRedraw();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
