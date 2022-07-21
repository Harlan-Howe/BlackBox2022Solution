import java.awt.*;

public class MysteryBox implements BlackBoxCell
{

    public static final int STATUS_DEBUG_SHOW = 1;
    public static final int STATUS_PENCILLED = 2;

    private int myStatus;
    private int xPos, yPos;
    private boolean hasBall;
    private boolean shouldShowBall;

    public MysteryBox(int x, int y)
    {
        setStatus(STATUS_BLANK);
        xPos = x;
        yPos = y;
        shouldShowBall = false;
    }

    @Override
    public void drawSelf(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(xPos, yPos, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.WHITE);
        g.drawRect(xPos, yPos, CELL_SIZE, CELL_SIZE);


        if (getStatus()== STATUS_PENCILLED)
        {
            g.setColor(Color.YELLOW);
            g.drawOval(xPos+2, yPos+2, CELL_SIZE-4, CELL_SIZE-4);
        }

        if (shouldShowBall && hasBall)
        {
            g.setColor(Color.RED);
            g.fillOval(xPos+5, yPos+5, CELL_SIZE-10, CELL_SIZE-10);
        }

        if (getStatus()== STATUS_DEBUG_SHOW)
        {
            g.setColor(Color.GREEN);
            g.fillOval(xPos+10, yPos+10, CELL_SIZE-20, CELL_SIZE-20);
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

    public void setHasBall(boolean hb)
    {
        hasBall = hb;
    }

    public boolean hasBall()
    {
        return hasBall;
    }

    public void setShouldShowBall(boolean ssb)
    {
        shouldShowBall = ssb;
    }
}
