import java.awt.*;

public class MysteryBox implements BlackBoxCell
{

    public static final int STATUS_REVEALED = 1;
    public static final int STATUS_PENCILLED = 2;

    private int myStatus;
    private int xPos, yPos;

    public MysteryBox(int x, int y)
    {
        setStatus(STATUS_BLANK);
        xPos = x;
        yPos = y;
    }

    @Override
    public void drawSelf(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.WHITE);
        g.drawRect(xPos, yPos, CELL_SIZE, CELL_SIZE);

        if (getStatus()==STATUS_REVEALED)
        {
            g.setColor(Color.GREEN);
            g.fillOval(xPos+3, yPos+3, CELL_SIZE-6, CELL_SIZE-6);
        }
        else if (getStatus()== STATUS_PENCILLED)
        {
            g.setColor(Color.YELLOW);
            g.drawOval(xPos+2, yPos+2, CELL_SIZE-4, CELL_SIZE-4);
        }

    }

    @Override
    public void setStatus(int s)
    {
        myStatus = s;
    }

    @Override
    public int getStatus()
    {
        return myStatus;
    }
}
