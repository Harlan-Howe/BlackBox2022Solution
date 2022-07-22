import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

public class BlackBoxPanel extends JPanel implements MouseListener
{

    private BlackBoxCell[][] myGrid;
    private char latestLabel;
    private int numGuesses;
    private boolean revealedMode;
    private SoundPlayer soundPlayer;

    private final int LEFT_MARGIN = 100;
    private final int TOP_MARGIN = 100;

    private final int NUM_BALLS = 5;
    private final int MYSTERY_BOX_GRID_SIZE = 8;

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
        loadSounds();
        myGrid = new BlackBoxCell[MYSTERY_BOX_GRID_SIZE+2][MYSTERY_BOX_GRID_SIZE+2];

        for (int i=1; i<=MYSTERY_BOX_GRID_SIZE; i++)
        {
            for (int j = 1; j <= 8; j++)
                myGrid[j][i] = new MysteryBox(LEFT_MARGIN + i * BlackBoxCell.CELL_SIZE, TOP_MARGIN + j * BlackBoxCell.CELL_SIZE);

            for (int k = 0; k <= MYSTERY_BOX_GRID_SIZE+1; k += MYSTERY_BOX_GRID_SIZE+1)
            {
                myGrid[i][k] = new EdgeBox(LEFT_MARGIN + k * BlackBoxCell.CELL_SIZE, TOP_MARGIN + i * BlackBoxCell.CELL_SIZE);
                myGrid[k][i] = new EdgeBox(LEFT_MARGIN + i * BlackBoxCell.CELL_SIZE, TOP_MARGIN + k * BlackBoxCell.CELL_SIZE);
            }
        }

        reset();
    }

    /**
     * preload sound files for more responsive sound playback
     */
    public void loadSounds()
    {
        soundPlayer = new SoundPlayer();
        soundPlayer.loadSound("EnergyBounce.wav"); // Energy Bounce by "magnuswalker" at https://freesound.org/s/523088/ shared via Creative Commons
        soundPlayer.loadSound("Punch.wav"); // "Martial arts fast punch" at https://mixkit.co/free-sound-effects/
        soundPlayer.loadSound("Chirp.wav"); // "Retro game notification" at https://mixkit.co/free-sound-effects/
        soundPlayer.loadSound("Hmm.wav"); // Hmm sound by "DAN2008" at https://freesound.org/s/165011/ shared via Creative Commons
        soundPlayer.loadSound("Reveal.wav"); // Reveal sound by "GameAudio" at https://freesound.org/s/220171/ shared via Creative Commons
        soundPlayer.loadSound("Reset.wav"); // Reset sound by "Wdomino" at https://freesound.org/s/508575/ shared via Creative Commons
    }

    /**
     * returns a direction that corresponds to a 90° ccw rotation from the given direction.
     * I.e. DIRECTION_RIGHT --> DIRECTION_UP; DIRECTION_DOWN --> DIRECTION_RIGHT; DIRECTION_LEFT --> DIRECTION_DOWN;
     *      DIRECTION_UP --> DIRECTION_LEFT
     * @param dir starting direction number (unchanged)
     * @return the direction number corresponding to a 90° rotation to the left.
     */
    public int turnLeft(int dir)
    {
        return (dir+3)%4;
    }

    /**
     * returns a direction that corresponds to a 90° cw rotation from the given direction.
     * I.e. DIRECTION_RIGHT --> DIRECTION_DOWN; DIRECTION_DOWN --> DIRECTION_LEFT; DIRECTION_LEFT --> DIRECTION_UP;
     *      DIRECTION_UP --> DIRECTION_RIGHT
     * @param dir starting direction number (unchanged)
     * @return the direction number corresponding to a 90° rotation to the left.
     */public int turnRight(int dir)
    {
        return (dir+1)%4;
    }

    /**
     * finds the row and column directly in front of the given position, assuming one is facing in the given direction.
     * Example 1: if pos is (3,4) and direction is DIRECTION_RIGHT, then return (3,5).
     * Example 2: if pos is (3,4) and direction is DIRECTION_UP, then return (2,4).
     * @param pos - starting (row, column)
     * @param direction - the direction one is facing
     * @return a new (row, column) 2-element array for the position in front of this one.
     */
    public int[] getPositionInFrontOf(int[] pos, int direction)
    {
        int[] p = {pos[0],pos[1]};
        p[0] += DELTAS[direction][0];
        p[1] += DELTAS[direction][1];
        return p;
    }

    /**
     * finds the row and column directly in front AND one to the right of the given position, assuming one is facing in the given direction.
     * Example 1: if pos is (3,4) and direction is DIRECTION_RIGHT, then return (4,5).
     * Example 2: if pos is (3,4) and direction is DIRECTION_UP, then return (2,5).
     * @param pos - starting (row, column)
     * @param direction - the direction one is facing
     * @return a new (row, column) 2-element array for the position diagonally to the front-right of this one.
     */
    public int[] getPositionFrontRightOf(int[] pos, int direction)
    {
        return getPositionInFrontOf(getPositionInFrontOf(pos,direction),turnRight(direction));
    }

    /**
     * finds the row and column directly in front AND one to the left of the given position, assuming one is facing in the given direction.
     * Example 1: if pos is (3,4) and direction is DIRECTION_RIGHT, then return (2,5).
     * Example 2: if pos is (3,4) and direction is DIRECTION_UP, then return (2,3).
     * @param pos - starting (row, column)
     * @param direction - the direction one is facing
     * @return a new (row, column) 2-element array for the position diagonally to the front-left of this one.
     */
    public int[] getPositionFrontLeftOf(int[] pos, int direction)
    {
        return getPositionInFrontOf(getPositionInFrontOf(pos, direction),turnLeft(direction));
    }

    /**
     * checks whether the given row, column are within the black area of the grid.
     * @param r - row
     * @param c - column
     * @return whether the item at (r, c) of the grid is a MysteryBox
     */
    public boolean isMysteryBox(int r, int c)
    {
        return r>0 && r<MYSTERY_BOX_GRID_SIZE+1 && c>0 && c<MYSTERY_BOX_GRID_SIZE+1;
    }

    /**
     * checks whether the given (row, column) are within the black area of the grid
     * @param p - (row, column)
     * @return whether the item at p of the grid is a MysteryBox.
     */
    public boolean isMysteryBox(int[] p)
    {
        return isMysteryBox(p[0],p[1]);
    }

    /**
     * checks whether the given row and column are within the light gray area of the grid (but not the corners)
     * @param r - row
     * @param c - colu,n
     * @return - whether the item at (r, c) of the grid is an EdgeBox.
     */
    public boolean isEdgeBox(int r, int c)
    {
        return ((r==0 || r==MYSTERY_BOX_GRID_SIZE+1)&&(c>0 && c<=MYSTERY_BOX_GRID_SIZE)) ||
                ((c==0 || c==MYSTERY_BOX_GRID_SIZE+1)&&(r>0 && r<=MYSTERY_BOX_GRID_SIZE));
    }


    public void revealAllBalls()
    {
        for (int r = 1; r <= MYSTERY_BOX_GRID_SIZE; r++)
            for (int c = 1; c <= MYSTERY_BOX_GRID_SIZE; c++)
                ((MysteryBox) myGrid[r][c]).setShouldShowBall(true);
        revealedMode = true;
        repaint();
        soundPlayer.playSound("Reveal.wav");
    }

    public void reset()
    {

        latestLabel = 'A';
        for (int r=0; r<=MYSTERY_BOX_GRID_SIZE+1; r++)
            for (int c=0; c<=MYSTERY_BOX_GRID_SIZE+1; c++)
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
        for (int i=0; i<NUM_BALLS; i++)
        {
            int r1 = (int) (MYSTERY_BOX_GRID_SIZE * Math.random() + 1);
            int c1 = (int) (MYSTERY_BOX_GRID_SIZE * Math.random() + 1);
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
        soundPlayer.playSound("Reset.wav");
    }

    @Override
    public void paintComponent(Graphics g)
    {

        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.drawString("Number of Shots Taken: "+numGuesses, LEFT_MARGIN, TOP_MARGIN-10);

        if (revealedMode)
        {
            g.setColor(new Color(200,200,255));
            g.fillRect(LEFT_MARGIN-3, TOP_MARGIN-3, (MYSTERY_BOX_GRID_SIZE+2)*BlackBoxCell.CELL_SIZE+6, (MYSTERY_BOX_GRID_SIZE+2)*BlackBoxCell.CELL_SIZE+6);
        }

        for (int i=0; i<MYSTERY_BOX_GRID_SIZE+2; i++)
            for (int j=0; j<MYSTERY_BOX_GRID_SIZE+2; j++)
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
        if (revealedMode)
            return;
        int r = (e.getY()- TOP_MARGIN)/BlackBoxCell.CELL_SIZE;
        int c = (e.getX()- LEFT_MARGIN)/BlackBoxCell.CELL_SIZE;
        if (r<0 || r>MYSTERY_BOX_GRID_SIZE+1 || c<0 || c>MYSTERY_BOX_GRID_SIZE+1)
            return;
        if (isMysteryBox(r,c))
        {
            if (myGrid[r][c].getStatus() == MysteryBox.STATUS_BLANK)
                myGrid[r][c].setStatus(MysteryBox.STATUS_PENCILLED);
            else
                myGrid[r][c].setStatus(MysteryBox.STATUS_BLANK);
            soundPlayer.playSound("Hmm.wav");
        }
        else if (((r>0)&&(r<MYSTERY_BOX_GRID_SIZE+1))||((c>0))&&(c<MYSTERY_BOX_GRID_SIZE+1)) // is this an edge box? (eliminating corners)
        {
            int[] startPos = {r,c};
            int direction;

            if (r==0) // top edge
                direction = DIRECTION_DOWN;
            else if (r==MYSTERY_BOX_GRID_SIZE+1) // bottom edge
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
            soundPlayer.playSound("Punch.wav");
        }
        else if (startPos[0] == exitPos[0] && startPos[1] == exitPos[1])
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_REFLECT);
            soundPlayer.playSound("EnergyBounce.wav");
        }
        else
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_LABEL);
            ((EdgeBox) myGrid[startPos[0]][startPos[1]]).setMyLabel(String.valueOf(latestLabel));
            myGrid[exitPos[0]][exitPos[1]].setStatus(EdgeBox.STATUS_LABEL);
            ((EdgeBox) myGrid[exitPos[0]][exitPos[1]]).setMyLabel(String.valueOf(latestLabel));
            soundPlayer.playSound("Chirp.wav");
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
                d = turnLeft(d);
                if (!isMysteryBox(p))
                    return p;
                continue;
            }

            int[] leftFrontPoint = getPositionFrontLeftOf(p,d);
            if (isMysteryBox(leftFrontPoint)&&((MysteryBox)myGrid[leftFrontPoint[0]][leftFrontPoint[1]]).hasBall())
            {
                d = turnRight(d);
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
