package Model;

import GUI.Game;

public class Roll {
    private Card[] roll;
    private int rollPosition;
    public Roll(Card[] roll)
    {
        rollPosition = 0;
        this.roll = roll;
    }
    public void move(int y)
    {
        int height = Game.cardcount* Card.cardy;
        for(Card c:roll)
        {
            c.setY(c.getY()+y);
        }
        if(roll[rollPosition].getY() + Card.cardy > height)//falls aktuelle karte aus dem bildschirm rausschaut
        {
            moveRollPosition();
            int newPosition = roll[rollPosition].getY() - Card.cardy;
            roll[(rollPosition +1)%roll.length].setY(newPosition);
        }
    }
    public Card getCard(int index)
    {
        return roll[index];
    }
    public int getCardCount()
    {
        return roll.length;
    }
    private void moveRollPosition()
    {
        rollPosition = (rollPosition +1)%roll.length;
    }
}
