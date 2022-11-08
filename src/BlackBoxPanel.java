import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BlackBoxPanel extends JPanel implements MouseListener
{
    // variables
    private BlackBoxCell[][] myGrid;
    private char latestLabel;
    private int numShots;
    private boolean revealedMode;
    private SoundPlayer soundPlayer;
    private boolean firstRun;
    private Font scoreFont;

    // constants
    private final int LEFT_MARGIN = 100;
    private final int TOP_MARGIN = 100;

    private final int NUM_BALLS = 5;
    private final int MYSTERY_BOX_GRID_SIZE = 8;

    public static final int DIRECTION_RIGHT = 0;
    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_UP = 3;
    //                               RGT   DWN    LFT    UP
    private final int[][] DELTAS = {{0,1},{1,0},{0,-1},{-1,0}};
    // Note: the size (in pixels) of the cells is a constant in BlackBoxCell, so you can refer to it as
    //        BlackBoxCell.CELL_SIZE.


    public BlackBoxPanel()
    {
        super();
        setBackground(Color.LIGHT_GRAY);
        addMouseListener(this);
        loadSounds();
        firstRun = true;
        scoreFont = new Font("Times",Font.PLAIN, 18);
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

    /**
     * checks whether the given row and column are within the light gray area of the grid (but not the corners)
     * @param p = (row, column)
     * @return - whether the item at p of the grid is an EdgeBox.
     */
    public boolean isEdgeBox(int[] p) { return isEdgeBox(p[0],p[1]);}
    /**
     * Tells all the MysteryBoxes that they should show the ball, if they have one; activates "revealedMode" and plays
     * a sound.
     */
    public void revealAllBalls()
    {
        for (int r = 1; r <= MYSTERY_BOX_GRID_SIZE; r++)
            for (int c = 1; c <= MYSTERY_BOX_GRID_SIZE; c++)
                ((MysteryBox) myGrid[r][c]).setShouldShowBall(true);
        revealedMode = true;
        repaint();
        soundPlayer.playSound("Reveal.wav");
    }

    /**
     * resets the board, hiding content on edgeBoxes, re-randomizing balls and hiding them, resetting the latestLabel to
     * "A," resetting the shot count to zero, clearing the "revealedMode," and playing a sound.
     */
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
        numShots = 0;
        revealedMode = false;
        repaint();
        if (!firstRun)
            soundPlayer.playSound("Reset.wav");
        firstRun = false;
    }

    public void resetWithTestData()
    {
        reset();
        for (int i=1; i<=MYSTERY_BOX_GRID_SIZE; i++)
            for (int j=1; j<=MYSTERY_BOX_GRID_SIZE; j++)
                ((MysteryBox)myGrid[i][j]).setHasBall(false);

        ((MysteryBox)myGrid[1][1]).setHasBall(true);
        ((MysteryBox)myGrid[1][2]).setHasBall(true);
        ((MysteryBox)myGrid[1][6]).setHasBall(true);
        ((MysteryBox)myGrid[3][6]).setHasBall(true);
        ((MysteryBox)myGrid[6][2]).setHasBall(true);
        repaint();

    }

    @Override
    public void paintComponent(Graphics g)
    {

        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.setFont(scoreFont);
        g.drawString("Number of Shots Taken: "+ numShots, LEFT_MARGIN, TOP_MARGIN-20);

        if (revealedMode)
        {
            g.setColor(new Color(200,200,255));
            g.fillRect(LEFT_MARGIN-3, TOP_MARGIN-3, (MYSTERY_BOX_GRID_SIZE+2)*BlackBoxCell.CELL_SIZE+6, (MYSTERY_BOX_GRID_SIZE+2)*BlackBoxCell.CELL_SIZE+6);
        }

        // now draw all the boxes.
        for (int i=0; i<MYSTERY_BOX_GRID_SIZE+2; i++)
            for (int j=0; j<MYSTERY_BOX_GRID_SIZE+2; j++)
                if (myGrid[i][j] != null)
                    myGrid[i][j].drawSelf(g);
    }

    /**
     * toggles the Pencilled/blank state of the MysteryBox at location (r, c) and plays a sound.
     * precondition: the item of myGrid at (r,c) is a MysteryBox.
     * @param r - row
     * @param c - column
     */
    public void togglePencilledStatus(int r, int c)
    {
        if (myGrid[r][c].getStatus() == MysteryBox.STATUS_BLANK)
            myGrid[r][c].setStatus(MysteryBox.STATUS_PENCILLED);
        else
            myGrid[r][c].setStatus(MysteryBox.STATUS_BLANK);
        soundPlayer.playSound("Hmm.wav");
    }

    /**
     * The user has just clicked on an EdgeBox. If it is a blank edgebox, then fire a shot inward from this edgeBox, see
     * where it comes out (if at all) and mark this edgebox (and the edgebox where it exited, if different); play a
     * sound and increment the number of shots.
     * Precondition: the box in myGrid at startPos is an EdgeBox.
     * @param startPos - the (r, c) of the clicked EdgeBox.
     */
    public void processShot(int[] startPos)
    {

        // if this is an edgeCell that already is showing something, then bail out now.
        if (myGrid[startPos[0]][startPos[1]].getStatus() != BlackBoxCell.STATUS_BLANK)
            return;

        // identify the initial direction of movement for this shot, based on which edge of the board this is.
        int direction;
        if (startPos[0]==0) // top edge
            direction = DIRECTION_DOWN;
        else if (startPos[0]==MYSTERY_BOX_GRID_SIZE+1) // bottom edge
            direction = DIRECTION_UP;
        else if (startPos[1]==0) // left edge
            direction = DIRECTION_RIGHT;
        else // right edge
            direction = DIRECTION_LEFT;


        numShots++;
        int[] exitPos = findExitPoint(startPos,direction);

        if (exitPos == null) // if it didn't exit, that means we hit a ball.
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_HIT);
            soundPlayer.playSound("Punch.wav");
        }
        else if (startPos[0] == exitPos[0] && startPos[1] == exitPos[1]) // if it came out the same place it went in...
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_REFLECT);
            soundPlayer.playSound("EnergyBounce.wav");
        }
        else // if we have a distinct exit point from the entry point.
        {
            myGrid[startPos[0]][startPos[1]].setStatus(EdgeBox.STATUS_LABEL);
            ((EdgeBox) myGrid[startPos[0]][startPos[1]]).setMyLabel(String.valueOf(latestLabel));
            myGrid[exitPos[0]][exitPos[1]].setStatus(EdgeBox.STATUS_LABEL);
            ((EdgeBox) myGrid[exitPos[0]][exitPos[1]]).setMyLabel(String.valueOf(latestLabel));
            soundPlayer.playSound("Chirp.wav");
            latestLabel++;
        }
    }

    /**
     * A shot is being fired into the grid of Mystery boxes from the edgeBox at startingPosition, and this method will return
     * the (r,c) of the edgebox where it exits the grid, if any.
     * @param startingPosition - the (r,c) of the edgebox where the shot starts.
     * @param direction - the direction the shot is initially moving, one of DIRECTION_RIGHT, DIRECTION_DOWN, DIRECTION_LEFT,
     *            or DIRECTION_UP.
     * @return - a 2-element array of (r, c) for the EdgeBox where the shot exits, or null, if the shot hit a ball
     * head on.
     */
    public int[] findExitPoint(int[] startingPosition, int direction)
    {
        // p and d are the location of the shot - they change over the course of this method, but the pos and direction don't.
        int[] p = startingPosition;
        int d = direction;

        clear_all_debug_marks();

        // TODO: write this method:

        // do the following until you either hit a ball or exit the grid:
        //     1) Check whether the space in front of you has a ball. If so, return null.
        //     2) Check whether the space in front-right of you has a ball. If so...
        //           a) turn left in place.
        //           b) Skip to step 5.
        //     3) Check whether the space in front-left of you has a ball. If so...
        //           a) turn right in place.
        //           b) Skip to step 5.
        //     4) Move forwards one space.
        //     5) If p holds an edgebox, you've exited the mysterybox area, so return p.
        //     6) DEBUGGING only: ((MysteryBox)myGrid[p[0]][p[1]]).setStatus(MysteryBox.STATUS_DEBUG_SHOW);

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
            }
            else
            {
                int[] leftFrontPoint = getPositionFrontLeftOf(p, d);
                if (isMysteryBox(leftFrontPoint) && ((MysteryBox) myGrid[leftFrontPoint[0]][leftFrontPoint[1]]).hasBall())
                {
                    d = turnRight(d);

                } else
                {
                    p = frontPoint;
                }
            }
            if (! isMysteryBox(p))
                return p;


            (myGrid[p[0]][p[1]]).setStatus(MysteryBox.STATUS_DEBUG_SHOW);
        }
    }

    /**
     * reset all of the mystery boxes that have the green debug marks back to the blank status without disturbing
     * any of the pencilled marks.
     */
    public void clear_all_debug_marks()
    {
        for (int i = 1; i <=MYSTERY_BOX_GRID_SIZE; i++)
            for (int j = 1; j <=MYSTERY_BOX_GRID_SIZE; j++)
                if ((myGrid[i][j]).getStatus() == MysteryBox.STATUS_DEBUG_SHOW)
                    (myGrid[i][j]).setStatus(MysteryBox.STATUS_BLANK);
    }

    @Override
    /**
     * The user just let go of the mouse inside the panel. If it is on a MysteryBox, toggle it's pencil status; otherwise
     * if it is an edgeBox, process the shot from that edge.
     */
    public void mouseReleased(MouseEvent e)
    {
        if (revealedMode) // ignore mouse in this panel if the balls are revealed.
            return;

        // find equivalent cell indexes for this mouse click. So r and c are not pixel locations, but indicators of
        //  which row and column are being clicked.
        int r = (e.getY()- TOP_MARGIN)/BlackBoxCell.CELL_SIZE;
        int c = (e.getX()- LEFT_MARGIN)/BlackBoxCell.CELL_SIZE;

        if (isMysteryBox(r,c))
        {
            togglePencilledStatus(r,c);
        }
        else if (isEdgeBox(r,c))
        {
            int[] startPos = {r,c};
            processShot(startPos);
        }
        repaint();
    }

    // --------------------------------- UNUSED Methods.
    // We are required to have these methods to be a MouseListener, but they don't have to _do_ anything, so we leave
    //  them blank. But being a MouseListener is what allows us to receive events that might get caught by the previous
    //  method, mouseReleased(). So these extra, blank methods are the price we pay for that functionality.
    @Override
    public void mouseClicked(MouseEvent e)
    {
        // intentionally blank. This method gets called if the user lets go of the mouse at the same location as he/she
        //     pressed it.
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        // intentionally blank. This method gets called if the user intially presses the mouse inside the panel.
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        // intentionally blank. The user just moved the mouse into this panel frome elsewhere.
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        // intentionally blank. The user just moved the mouse out of this panel.
    }
}
