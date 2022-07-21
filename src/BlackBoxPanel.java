import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BlackBoxPanel extends JPanel implements MouseListener
{

    private BlackBoxCell[][] myGrid;
    private char latestLabel;
    private int numGuesses;
    private boolean revealedMode;

    private final int LEFT_MARGIN = 100;
    private final int TOP_MARGIN = 100;

    private final int DIRECTION_RIGHT = 0;
    private final int DIRECTION_DOWN = 1;
    private final int DIRECTION_LEFT = 2;
    private final int DIRECTION_UP = 3;

    //                               RGT   DWN    LFT    UP
    private final int[][] DELTAS = {{0,1},{1,0},{0,-1},{-1,0}};


    public BlackBoxPanel()
    {
        super();
        setBackground(Color.LIGHT_GRAY);
        addMouseListener(this);

        myGrid = new BlackBoxCell[10][10];

        for (int i=1; i<=8; i++)
        {
            for (int j = 1; j <= 8; j++)
                myGrid[j][i] = new MysteryBox(LEFT_MARGIN + i * BlackBoxCell.CELL_SIZE, TOP_MARGIN + j * BlackBoxCell.CELL_SIZE);

            for (int k = 0; k <= 9; k += 9)
            {
                myGrid[i][k] = new EdgeBox(LEFT_MARGIN + k * BlackBoxCell.CELL_SIZE, TOP_MARGIN + i * BlackBoxCell.CELL_SIZE);
                myGrid[k][i] = new EdgeBox(LEFT_MARGIN + i * BlackBoxCell.CELL_SIZE, TOP_MARGIN + k * BlackBoxCell.CELL_SIZE);
            }
        }

        reset();


    }


    public int[] getPositionInFrontOf(int[] pos, int direction)
    {
        int[] p = {pos[0],pos[1]};
        p[0] += DELTAS[direction][0];
        p[1] += DELTAS[direction][1];
        return p;
    }

    public int[] getPositionFrontRightOf(int[] pos, int direction)
    {
        return getPositionInFrontOf(getPositionInFrontOf(pos,direction),(direction+1)%4);
    }

    public int[] getPositionFrontLeftOf(int[] pos, int direction)
    {
        return getPositionInFrontOf(getPositionInFrontOf(pos, direction),(direction+3)%4);
    }


    public boolean isMysteryBox(int r, int c)
    {
        return r>0 && r<9 && c>0 && c<9;
    }

    public boolean isMysteryBox(int[] p)
    {
        return isMysteryBox(p[0],p[1]);
    }

    public void revealAllBalls()
    {
        for (int r=1; r<=8; r++)
            for (int c=1; c<=8; c++)
                ((MysteryBox)myGrid[r][c]).setShouldShowBall(true);
        revealedMode = true;
        repaint();
    }

    public void reset()
    {

        latestLabel = 'A';
        for (int r=0; r<=9; r++)
            for (int c=0; c<=9; c++)
            {
                if (myGrid[r][c] == null)
                    continue;
                myGrid[r][c].setStatus(BlackBoxCell.STATUS_BLANK);
                if (isMysteryBox(r,c))
                {
                    ((MysteryBox) myGrid[r][c]).setShouldShowBall(false);
                    ((MysteryBox) myGrid[r][c]).setHasBall(false);
                }
                else
                    ((EdgeBox) myGrid[r][c]).setMyLabel("");
            }
        for (int i=0; i<4; i++)
        {
            int r1 = (int) (8 * Math.random() + 1);
            int c1 = (int) (8 * Math.random() + 1);
            if (((MysteryBox)myGrid[r1][c1]).hasBall())
            {
                i--;
                continue;
            }
            ((MysteryBox) myGrid[r1][c1]).setHasBall(true);
        }
        numGuesses = 0;
        revealedMode = false;
        repaint();

    }

    @Override
    public void paintComponent(Graphics g)
    {

        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.drawString("Number of Shots Taken: "+numGuesses, LEFT_MARGIN, TOP_MARGIN-10);

        if (revealedMode)
        {
            g.setColor(Color.PINK);
            g.fillRect(LEFT_MARGIN-3, TOP_MARGIN-3, 10*BlackBoxCell.CELL_SIZE+6, 10*BlackBoxCell.CELL_SIZE+6);
        }

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
        int r = (e.getY()- TOP_MARGIN)/BlackBoxCell.CELL_SIZE;
        int c = (e.getX()- LEFT_MARGIN)/BlackBoxCell.CELL_SIZE;
        if (r<0 || r>9 || c<0 || c>9)
            return;
        if (isMysteryBox(r,c))
            if (myGrid[r][c].getStatus() == MysteryBox.STATUS_BLANK)
                myGrid[r][c].setStatus(MysteryBox.STATUS_PENCILLED);
            else
                myGrid[r][c].setStatus(MysteryBox.STATUS_BLANK);

        else if (((r>0)&&(r<9))||((c>0))&&(c<9)) // is this an edge box? (eliminating corners)
        {
            int[] startPos = {r,c};
            int direction;

            if (r==0) // top edge
                direction = DIRECTION_DOWN;
            else if (r==9) // bottom edge
                direction = DIRECTION_UP;
            else if (c==0) // left edge
                direction = DIRECTION_RIGHT;
            else // right edge
                direction = DIRECTION_LEFT;


            processShot(startPos, direction);
        }

        repaint();
    }

    public void processShot(int[] startPos, int dir)
    {
        if (myGrid[startPos[0]][startPos[1]].getStatus() != BlackBoxCell.STATUS_BLANK)
            return;
        numGuesses++;
        int[] exitPos = findExitPoint(startPos,dir);

        if (exitPos == null)
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_HIT);
        }
        else if (startPos[0] == exitPos[0] && startPos[1] == exitPos[1])
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_REFLECT);
        }
        else
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_LABEL);
            ((EdgeBox) myGrid[startPos[0]][startPos[1]]).setMyLabel(String.valueOf(latestLabel));
            myGrid[exitPos[0]][exitPos[1]].setStatus(EdgeBox.STATUS_LABEL);
            ((EdgeBox) myGrid[exitPos[0]][exitPos[1]]).setMyLabel(String.valueOf(latestLabel));

            latestLabel++;
        }
    }

    public int[] findExitPoint(int[] startPos, int dir)
    {
        int[] p = startPos;
        int d = dir;
        while(true)
        {
            int[] frontPoint = getPositionInFrontOf(p,d);
            if (myGrid[frontPoint[0]][frontPoint[1]] instanceof EdgeBox)
                return frontPoint;
            if (((MysteryBox)myGrid[frontPoint[0]][frontPoint[1]]).hasBall())
                return null; // it's a hit!

            int[] rightFrontPoint = getPositionFrontRightOf(p,d);
            if (isMysteryBox(rightFrontPoint)&&((MysteryBox)myGrid[rightFrontPoint[0]][rightFrontPoint[1]]).hasBall())
            {
                d = (d+3)%4;
                if (!isMysteryBox(p))
                    return p;
                continue;
            }
            int[] leftFrontPoint = getPositionFrontLeftOf(p,d);
            if (isMysteryBox(leftFrontPoint)&&((MysteryBox)myGrid[leftFrontPoint[0]][leftFrontPoint[1]]).hasBall())
            {
                d = (d+1)%4;
                if (!isMysteryBox(p))
                    return p;
                continue;
            }
            p = frontPoint;
            if (! isMysteryBox(p))
                return p;
            //((MysteryBox)myGrid[p[0]][p[1]]).setStatus(MysteryBox.STATUS_DEBUG_SHOW);
        }
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
