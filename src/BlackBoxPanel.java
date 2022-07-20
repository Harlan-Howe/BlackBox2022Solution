import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BlackBoxPanel extends JPanel implements MouseListener
{

    private BlackBoxCell[][] myGrid;


    public BlackBoxPanel()
    {
        super();
        addMouseListener(this);
        myGrid = new BlackBoxCell[10][10];

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
