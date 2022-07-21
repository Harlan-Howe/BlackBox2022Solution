import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackBoxFrame extends JFrame implements ActionListener
{
    private BlackBoxPanel myPanel;
    private JButton revealButton, resetButton;

    public BlackBoxFrame()
    {
        super("Black Box");
        setSize(800, 800);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        myPanel = new BlackBoxPanel();
        getContentPane().add(myPanel, BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    public JPanel createButtonPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        revealButton = new JButton("Reveal");
        revealButton.addActionListener(this);
        panel.add(revealButton);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == revealButton)
        {
            myPanel.revealAllBalls();
        }
    }
}
