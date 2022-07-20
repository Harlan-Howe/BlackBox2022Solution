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
                myGrid[i][j] = new MysteryBox(leftMargin + i * BlackBoxCell.CELL_SIZE, topMargin + j * BlackBoxCell.CELL_SIZE);

            for (int k = 0; k <= 9; k += 9)
            {
                myGrid[k][i] = new EdgeBox(leftMargin + k * BlackBoxCell.CELL_SIZE, topMargin + i * BlackBoxCell.CELL_SIZE);
                myGrid[i][k] = new EdgeBox(leftMargin + i * BlackBoxCell.CELL_SIZE, topMargin + k * BlackBoxCell.CELL_SIZE);
            }
        }
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
