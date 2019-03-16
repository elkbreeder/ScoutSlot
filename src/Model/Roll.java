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
    public synchronized void move(int y)
    {
        int height = Game.cardcount* Card.cardy;

        roll[rollPosition].setY( roll[rollPosition].getY()+y);
        if(roll[rollPosition].getY() + Card.cardy > height)//falls aktuelle karte aus dem bildschirm rausschaut
        {
            int newPosition = roll[rollPosition].getY() - Card.cardy;
            moveRollPosition();
            roll[rollPosition].setY(newPosition);
        }
    }
    public synchronized Card getCard(int index)
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
