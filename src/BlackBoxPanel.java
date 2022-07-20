import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BlackBoxPanel extends JPanel implements MouseListener
{

    private BlackBoxCell[][] myGrid;

    private final int leftMargin = 20;
    private final int topMargin = 20;


    public BlackBoxPanel()
    {
        super();
        setBackground(Color.LIGHT_GRAY);
        addMouseListener(this);
        myGrid = new BlackBoxCell[10][10];

        for (int i=1; i<=8; i++)
        {
            for (int j = 1; j <= 8; j++)
                myGrid[j][i] = new MysteryBox(leftMargin + i * BlackBoxCell.CELL_SIZE, topMargin + j * BlackBoxCell.CELL_SIZE);

            for (int k = 0; k <= 9; k += 9)
            {
                myGrid[i][k] = new EdgeBox(leftMargin + k * BlackBoxCell.CELL_SIZE, topMargin + i * BlackBoxCell.CELL_SIZE);
                myGrid[k][i] = new EdgeBox(leftMargin + i * BlackBoxCell.CELL_SIZE, topMargin + k * BlackBoxCell.CELL_SIZE);
            }
        }

        myGrid[0][1].setStatus(EdgeBox.STATUS_HIT);
        myGrid[0][2].setStatus(EdgeBox.STATUS_REFLECT);
        myGrid[0][3].setStatus(EdgeBox.STATUS_LABEL);
        ((EdgeBox)myGrid[0][3]).setMyLabel("");
        myGrid[0][4].setStatus(EdgeBox.STATUS_LABEL);
        ((EdgeBox)myGrid[0][4]).setMyLabel("a");
        myGrid[0][5].setStatus(EdgeBox.STATUS_LABEL);
        ((EdgeBox)myGrid[0][5]).setMyLabel("bcd");

        myGrid[1][1].setStatus(MysteryBox.STATUS_PENCILLED);
        myGrid[2][2].setStatus(MysteryBox.STATUS_REVEALED);




    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        for (int i=0; i<10; i++)
            for (int j=0; j<10; j++)
                if (myGrid[i][j] != null)
                    myGrid[i][j].drawSelf(g);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {

    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        int r = (e.getY()-topMargin)/BlackBoxCell.CELL_SIZE;
        int c = (e.getX()-leftMargin)/BlackBoxCell.CELL_SIZE;
        if (r<0 || r>9 || c<0 || c>9)
            return;
        if (((r>0)&&(r<9))||((c>0))&&(c<9))
        {
            if (r==0) // top edge
                ;
            else if (r==9) // bottom edge
                ;
            else if (c==0) // left edge
                ;
            else if (c==9) // right edge
                ;
            else // mystery boxes
            {
                if (myGrid[r][c].getStatus() == MysteryBox.STATUS_BLANK)
                    myGrid[r][c].setStatus(MysteryBox.STATUS_PENCILLED);
                else
                    myGrid[r][c].setStatus(MysteryBox.STATUS_BLANK);
            }
        }

        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }
}
