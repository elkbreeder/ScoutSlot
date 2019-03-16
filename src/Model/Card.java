package Model;

import javax.swing.*;
import java.awt.*;

public class Card {
    public static final int cardx = 200;
    public static final int cardy = 300;
    private int x;
    private int y;
    private ImageIcon image;
    public Card(String imgSrc)
    {
        x = 50;
        image = new ImageIcon(imgSrc);
        Image img = image.getImage(); // transform it
        img = img.getScaledInstance(cardx, cardy,  java.awt.Image.SCALE_SMOOTH);
        image = new ImageIcon(img);
    }
    public ImageIcon getImage()
    {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void setY(int y){
        this.y = y;
    }
}
