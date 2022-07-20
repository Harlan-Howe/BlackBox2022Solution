import java.awt.*;

public interface  BlackBoxCell
{

    final int STATUS_BLANK = 0;

    final int CELL_SIZE = 60;


    public void drawSelf(Graphics g);

    public void setStatus(int s);

    public int getStatus();
}
