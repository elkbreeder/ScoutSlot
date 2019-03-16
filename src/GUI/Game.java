package GUI;

import Model.Card;
import Model.GameThread;
import Model.Roll;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Game extends JPanel implements KeyListener {
    public static final int cardcount = 2;
    public static final int slots = 3;
    public static final int positionSlot1x = 50;
    public static final int positionSlot1y = 50;

    public GameThread gameThread;
    public Roll roll1;
    public Game()
    {
        Card[] cards = new Card[4];
        cards[0] = new Card("cards200x300/pic1.png");
        cards[0].setY(Card.cardy);
        cards[1] = new Card("cards200x300/pic2.png");
        cards[1].setY(0);
        cards[2] = new Card("cards200x300/pic3.png");
        cards[2].setY(-20000);
        cards[3] = new Card("cards200x300/pic4.png");
        cards[3].setY(-20000);
        roll1 = new Roll(cards);
        Card[] cards2 = new Card[4];
        cards2[0] = new Card("cards200x300/pic1.png");
        cards2[0].setY(Card.cardy);
        cards2[1] = new Card("cards200x300/pic2.png");
        cards2[1].setY(0);
        cards2[2] = new Card("cards200x300/pic3.png");
        cards2[2].setY(-20000);
        cards2[3] = new Card("cards200x300/pic4.png");
        cards2[3].setY(-20000);
        //img = (new Card("pic.jpeg")).getImage();
        gameThread = new GameThread();
        gameThread.start();
        DrawThread drawThread = new DrawThread();
        drawThread.start();

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Card c;
        for (int i = 0; i < roll1.getCardCount(); i++) {
            c = roll1.getCard(i);
            if(c.getY()+Card.cardy<0||c.getY()>Main.sizeY)continue;
            c.getImage().paintIcon(this, g, c.getX(), c.getY());
        }
    }
    /*
    *
    * Keypressing Part
    * */
    @Override
    public void keyTyped(KeyEvent e) {
        if(e.getKeyChar() == 'w')
        {
            gameThread.roll();
        }
        if(e.getKeyChar() == 'a')
        {
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
