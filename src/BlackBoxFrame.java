import javax.swing.*;
import java.awt.*;

public class BlackBoxFrame extends JFrame
{
    private BlackBoxPanel myPanel;

    public BlackBoxFrame()
    {
        super("Black Box");
        setSize(800,800);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        myPanel = new BlackBoxPanel();
        getContentPane().add(myPanel,BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }


}
