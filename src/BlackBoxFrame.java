import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackBoxFrame extends JFrame implements ActionListener
{
    private BlackBoxPanel myPanel;
    private JButton revealResetButton;

    public BlackBoxFrame()
    {
        super("Black Box");
        setSize(800, 800);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        myPanel = new BlackBoxPanel();
        getContentPane().add(myPanel, BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.NORTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    public JPanel createButtonPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        revealResetButton = new JButton("Reveal");
        revealResetButton.addActionListener(this);
        panel.add(revealResetButton);


        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == revealResetButton)
        {
            if (revealResetButton.getText().equals("Reveal"))
            {
                myPanel.revealAllBalls();
                revealResetButton.setText("Reset");
            }
            else
            {
                myPanel.reset();
                revealResetButton.setText("Reveal");
            }
        }

    }
}
