package gui;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gameObjects.Robot;
import log.Logger;

public class GameWindow extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private final GameVisualizer m_visualizer;
	private final Logger m_logger;

	public GameWindow() {
		super("������� ����", true, false, true, true);
		this.setLocation(500, 100);
		m_visualizer = new GameVisualizer();
		m_logger = new Logger();
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(m_visualizer, BorderLayout.CENTER);
		getContentPane().add(panel);
		pack();

	}
    
	JMenuBar generateMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		JMenu fme = new JMenu("������� ������");
		fme.setMnemonic(KeyEvent.VK_R);
		fme.getAccessibleContext().setAccessibleDescription("�������� �������");
		{

			JMenuItem RegularRobot = new JMenuItem("Regular", KeyEvent.VK_S);
			RegularRobot.addActionListener((event) -> {
				m_visualizer.m_rbtsSet(new Robot());
			});
			JMenuItem BFSRobot = new JMenuItem("BFS", KeyEvent.VK_S);
			BFSRobot.addActionListener((event) -> {
				m_visualizer.m_rbtsSet(new Robot("BFS"));
			});
			JMenu mods = new JMenu("����� �����");
			fme.add(mods);
			mods.add(RegularRobot);
			mods.add(BFSRobot);
		}
		JMenu mode = new JMenu("�����");
		mode.setMnemonic(KeyEvent.VK_R);
		mode.getAccessibleContext().setAccessibleDescription("������");
		{
			JMenuItem addRect = new JMenuItem("���������� �������������", KeyEvent.VK_S);
			addRect.addActionListener((event) -> {
				m_visualizer.setFlag("create");
			});
			mode.add(addRect);
		}

		{
			JMenuItem removeTarget = new JMenuItem("������� �������������", KeyEvent.VK_S);
			removeTarget.addActionListener((event) -> {
				m_visualizer.setFlag("remove");
			});
			mode.add(removeTarget);
		}

		JMenu lookAndFeelMenu = new JMenu("����� �����������");
		lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
		lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("���������� ������� ����������� ����������");

		{
			JMenuItem systemLookAndFeel = new JMenuItem("��������� �����", KeyEvent.VK_S);
			systemLookAndFeel.addActionListener((event) -> {
				setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				this.invalidate();
			});
			lookAndFeelMenu.add(systemLookAndFeel);
		}

		{
			JMenuItem crossplatformLookAndFeel = new JMenuItem("������������� �����", KeyEvent.VK_S);
			crossplatformLookAndFeel.addActionListener((event) -> {
				setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				this.invalidate();
			});
			lookAndFeelMenu.add(crossplatformLookAndFeel);
		}

		JMenu testMenu = new JMenu("�����");
		testMenu.setMnemonic(KeyEvent.VK_T);
		testMenu.getAccessibleContext().setAccessibleDescription("�������� �������");

		{
			JMenuItem addLogMessageItem = new JMenuItem("��������� � ���", KeyEvent.VK_S);
			addLogMessageItem.addActionListener((event) -> {
				m_logger.debug("����� ������");
			});
			testMenu.add(addLogMessageItem);
		}

		menuBar.add(lookAndFeelMenu);
		menuBar.add(testMenu);
		menuBar.add(fme);
		menuBar.add(mode);
		return menuBar;
	}

	private void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// just ignore
		}
	}

	protected LogWindow createLogWindow() {
		LogWindow logWindow = new LogWindow(m_logger.getDefaultLogSource());
		logWindow.setLocation(10, 10);
		logWindow.setSize(300, 800);
		setMinimumSize(logWindow.getSize());
		logWindow.pack();
		m_logger.debug("�������� ��������");
		return logWindow;
	}

	protected void addWindow(JInternalFrame frame, JDesktopPane desktopPane) {
		desktopPane.add(frame);
		frame.setVisible(true);
	}

}
