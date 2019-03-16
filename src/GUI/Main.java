package GUI;


import Model.Card;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main {
    public static Game game;
    public static final int sizeX = 500;
    public static final int sizeY = Game.cardcount* Card.cardy;
    public static void main(String[] args) throws IOException {
        game = new Game();
        JFrame frame = new JFrame("GameFrame");
        frame.addKeyListener(game);
        frame.setSize(sizeX,sizeY);
        frame.add(game);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Gamecloses");
                System.exit(0);
            }
        });

    }
}
