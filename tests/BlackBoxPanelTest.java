import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackBoxPanelTest
{
    private BlackBoxPanel testPanel;
    @BeforeEach
    void setUp()
    {
        testPanel = new BlackBoxPanel();
        testPanel.resetWithTestData();
    }

    @Test
    void testTurnLeft()
    {
        assertEquals(BlackBoxPanel.DIRECTION_UP, testPanel.turnLeft(BlackBoxPanel.DIRECTION_RIGHT));
        assertEquals(BlackBoxPanel.DIRECTION_RIGHT, testPanel.turnLeft(BlackBoxPanel.DIRECTION_DOWN));
        assertEquals(BlackBoxPanel.DIRECTION_DOWN, testPanel.turnLeft(BlackBoxPanel.DIRECTION_LEFT));
        assertEquals(BlackBoxPanel.DIRECTION_LEFT, testPanel.turnLeft(BlackBoxPanel.DIRECTION_UP));
    }

    @Test
    void testTurnRight()
    {
        assertEquals(BlackBoxPanel.DIRECTION_UP, testPanel.turnRight(BlackBoxPanel.DIRECTION_LEFT));
        assertEquals(BlackBoxPanel.DIRECTION_RIGHT, testPanel.turnRight(BlackBoxPanel.DIRECTION_UP));
        assertEquals(BlackBoxPanel.DIRECTION_DOWN, testPanel.turnRight(BlackBoxPanel.DIRECTION_RIGHT));
        assertEquals(BlackBoxPanel.DIRECTION_LEFT, testPanel.turnRight(BlackBoxPanel.DIRECTION_DOWN));
    }

    @Test
    void testGetPositionInFrontOf()
    {
        int[] pointA = {4,5};
        int[] pointB = {4,4};
        int[] pointC = {5,4};
        int[] pointD = {3,4};
        int[] pointE = {4,3};
        int[] pointF = {5,3};
        assertArrayEquals(pointA,testPanel.getPositionInFrontOf(pointB,BlackBoxPanel.DIRECTION_RIGHT));
        assertArrayEquals(pointE,testPanel.getPositionInFrontOf(pointB,BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(pointD,testPanel.getPositionInFrontOf(pointB,BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(pointC,testPanel.getPositionInFrontOf(pointB,BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(pointF,testPanel.getPositionInFrontOf(pointE,BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(pointD,testPanel.getPositionInFrontOf(testPanel.getPositionInFrontOf(pointD,BlackBoxPanel.DIRECTION_LEFT),BlackBoxPanel.DIRECTION_RIGHT));

    }

    @Test
    void testGetPositionFrontRightOf()
    {
        int[] pointA = {4,5};
        int[] pointB = {4,4};
        int[] pointC = {5,4};
        int[] pointD = {3,4};
        int[] pointE = {4,3};
        int[] pointF = {5,3};
        assertArrayEquals(pointC,testPanel.getPositionFrontRightOf(pointE,BlackBoxPanel.DIRECTION_RIGHT));
        assertArrayEquals(pointF,testPanel.getPositionFrontRightOf(pointB,BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(pointD,testPanel.getPositionFrontRightOf(pointA,BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(pointA,testPanel.getPositionFrontRightOf(pointC,BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(pointB,testPanel.getPositionFrontRightOf(pointF,BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(pointD,testPanel.getPositionFrontRightOf(testPanel.getPositionFrontRightOf(pointD,BlackBoxPanel.DIRECTION_LEFT),BlackBoxPanel.DIRECTION_RIGHT));
    }

    @Test
    void testGetPositionFrontLeftOf()
    {
        int[] pointA = {4,5};
        int[] pointB = {4,4};
        int[] pointC = {5,4};
        int[] pointD = {3,4};
        int[] pointE = {4,3};
        int[] pointF = {5,3};
        assertArrayEquals(pointD,testPanel.getPositionFrontLeftOf(pointE,BlackBoxPanel.DIRECTION_RIGHT));
        assertArrayEquals(pointF,testPanel.getPositionFrontLeftOf(pointB,BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(pointC,testPanel.getPositionFrontLeftOf(pointA,BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(pointE,testPanel.getPositionFrontLeftOf(pointC,BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(pointB,testPanel.getPositionFrontLeftOf(pointF,BlackBoxPanel.DIRECTION_RIGHT));
        assertArrayEquals(pointD,testPanel.getPositionFrontLeftOf(testPanel.getPositionFrontLeftOf(pointD,BlackBoxPanel.DIRECTION_LEFT),BlackBoxPanel.DIRECTION_RIGHT));
    }

    @Test
    void testFindExitPoint()
    {
        testPanel.resetWithTestData();
        // PROCEEDING CLOCKWISE FROM TOP LEFT...

        // TOP EDGE, shooting down....
        assertNull(testPanel.findExitPoint(new int[]{0,1},BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(new int[]{0,3},testPanel.findExitPoint(new int[]{0,3},BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(new int[]{9,4},testPanel.findExitPoint(new int[]{0,4},BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(new int[]{9,4},testPanel.findExitPoint(new int[]{0,4},BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(new int[]{0,5},testPanel.findExitPoint(new int[]{0,5},BlackBoxPanel.DIRECTION_DOWN));
        assertNull(testPanel.findExitPoint(new int[]{0,6},BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(new int[]{0,7},testPanel.findExitPoint(new int[]{0,7},BlackBoxPanel.DIRECTION_DOWN));
        assertArrayEquals(new int[]{9,8},testPanel.findExitPoint(new int[]{0,8},BlackBoxPanel.DIRECTION_DOWN));

        // RIGHT EDGE, shooting left...
        assertNull(testPanel.findExitPoint(new int[]{1,9},BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(new int[]{2,9},testPanel.findExitPoint(new int[]{2,9},BlackBoxPanel.DIRECTION_LEFT));
        assertNull(testPanel.findExitPoint(new int[]{3,9},BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(new int[]{9,7},testPanel.findExitPoint(new int[]{4,9},BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(new int[]{5,9},testPanel.findExitPoint(new int[]{5,9},BlackBoxPanel.DIRECTION_LEFT));
        assertNull(testPanel.findExitPoint(new int[]{6,9},BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(new int[]{9,3},testPanel.findExitPoint(new int[]{7,9},BlackBoxPanel.DIRECTION_LEFT));
        assertArrayEquals(new int[]{8,0},testPanel.findExitPoint(new int[]{8,9},BlackBoxPanel.DIRECTION_LEFT));

        // BOTTOM EDGE, shooting up...
        assertArrayEquals(new int[]{0,8},testPanel.findExitPoint(new int[]{9,8},BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(new int[]{4,9},testPanel.findExitPoint(new int[]{9,7},BlackBoxPanel.DIRECTION_UP));
        assertNull(testPanel.findExitPoint(new int[]{9,6},BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(new int[]{4,0},testPanel.findExitPoint(new int[]{9,5},BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(new int[]{0,4},testPanel.findExitPoint(new int[]{9,4},BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(new int[]{7,9},testPanel.findExitPoint(new int[]{9,3},BlackBoxPanel.DIRECTION_UP));
        assertNull(testPanel.findExitPoint(new int[]{9,2},BlackBoxPanel.DIRECTION_UP));
        assertArrayEquals(new int[]{7,0},testPanel.findExitPoint(new int[]{9,1},BlackBoxPanel.DIRECTION_UP));

        // LEFT EDGE, shooting right...
        assertArrayEquals(new int[]{8,9},testPanel.findExitPoint(new int[]{8,0},BlackBoxPanel.DIRECTION_RIGHT));
        assertArrayEquals(new int[]{9,1},testPanel.findExitPoint(new int[]{7,0},BlackBoxPanel.DIRECTION_RIGHT));
        assertNull(testPanel.findExitPoint(new int[]{6,0},BlackBoxPanel.DIRECTION_RIGHT));
        assertNull(testPanel.findExitPoint(new int[]{5,0},BlackBoxPanel.DIRECTION_RIGHT));
        assertArrayEquals(new int[]{9,5},testPanel.findExitPoint(new int[]{4,0},BlackBoxPanel.DIRECTION_RIGHT));
        assertNull(testPanel.findExitPoint(new int[]{3,0},BlackBoxPanel.DIRECTION_RIGHT));
        assertArrayEquals(new int[]{2,0},testPanel.findExitPoint(new int[]{2,0},BlackBoxPanel.DIRECTION_RIGHT));
        assertNull(testPanel.findExitPoint(new int[]{1,0},BlackBoxPanel.DIRECTION_RIGHT));
    }
}