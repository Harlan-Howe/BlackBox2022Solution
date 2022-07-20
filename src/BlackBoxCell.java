import java.awt.*;

public interface  BlackBoxCell
{

    public final int STATUS_BLANK = 0;


    public void drawSelf(Graphics g);

    public void setStatus(int s);

    public int getStatus();
}
