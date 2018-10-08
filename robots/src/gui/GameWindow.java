package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GameVisualizer m_visualizer;
    public GameWindow() 
    {
        super("Игровое поле", true, false, true, true);
        this.setLocation(500,100);
        m_visualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    } 
}
