import java.awt.*;

public class EdgeBox implements BlackBoxCell
{

    public static final int STATUS_HIT = 1;
    public static final int STATUS_REFLECT = 2;
    public static final int STATUS_LABEL = 3;

    private int myStatus;
    private String myLabel;
    private int xPos, yPos;

    private static Font edgeFont;


    public EdgeBox(int x, int y)
    {
        if (edgeFont == null)
        {
            edgeFont = new Font("Arial",Font.BOLD, CELL_SIZE-5);
        }
        xPos = x;
        yPos = y;
    }

    @Override
    public void drawSelf(Graphics g)
    {
        g.setColor(Color.GRAY);
        g.fillRect(xPos, yPos, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.WHITE);
        g.drawRect(xPos, yPos, CELL_SIZE, CELL_SIZE);

        if (getStatus()==STATUS_HIT)
        {
            g.setColor(Color.RED);
            g.fillRect(xPos+3, yPos+3, CELL_SIZE-6, CELL_SIZE-6);
        }
        else if (getStatus()== STATUS_REFLECT)
        {
            g.setColor(Color.YELLOW);
            g.fillOval(xPos+2, yPos+2, CELL_SIZE-4, CELL_SIZE-4);
        }
        else if (getStatus() == STATUS_LABEL)
        {

            g.setFont(edgeFont);
            int width = g.getFontMetrics().stringWidth(myLabel);
            g.setColor(Color.WHITE);
            g.drawString(myLabel,xPos+CELL_SIZE/2-width/2+1, yPos+CELL_SIZE-4);
            g.setColor(Color.BLACK);
            g.drawString(myLabel,xPos+CELL_SIZE/2-width/2, yPos+CELL_SIZE-5);
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

    public void setMyLabel(String s)
    {
        if (s.length()==0)
            myLabel = "?";
        else if (s.length()==1)
            myLabel = s.toUpperCase();
        else
            myLabel = s.substring(0,1).toUpperCase();
    }

}
