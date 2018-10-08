package gui;

import gui.GameVisualizer;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import log.Logger;

/**
 * ��� ��������� �������: 1. ����� �������� ���� ���������� ������������ �
 * ������ ��������. ������� ��������� ��� �� ����� ����� ������� ������� (���
 * ������ �������� ��������� �����).
 *
 */
public class MainApplicationFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JDesktopPane desktopPane = new JDesktopPane();

	public MainApplicationFrame() {
		// Make the big window be indented 50 pixels from each edge
		// of the screen.
		int inset = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

		setContentPane(desktopPane);

		LogWindow logWindow = createLogWindow();
		addWindow(logWindow);

		GameWindow gameWindow = new GameWindow();
		gameWindow.setSize(400, 400);
		addWindow(gameWindow);

		setJMenuBar(generateMenuBar());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	protected LogWindow createLogWindow() {
		LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
		logWindow.setLocation(10, 10);
		logWindow.setSize(300, 800);
		setMinimumSize(logWindow.getSize());
		logWindow.pack();
		Logger.debug("�������� ��������");
		return logWindow;
	}

	protected void addWindow(JInternalFrame frame) {
		desktopPane.add(frame);
		frame.setVisible(true);
	}

	// protected JMenuBar createMenuBar() {
	// JMenuBar menuBar = new JMenuBar();
	//
	// //Set up the lone menu.
	// JMenu menu = new JMenu("Document");
	// menu.setMnemonic(KeyEvent.VK_D);
	// menuBar.add(menu);
	//
	// //Set up the first menu item.
	// JMenuItem menuItem = new JMenuItem("New");
	// menuItem.setMnemonic(KeyEvent.VK_N);
	// menuItem.setAccelerator(KeyStroke.getKeyStroke(
	// KeyEvent.VK_N, ActionEvent.ALT_MASK));
	// menuItem.setActionCommand("new");
	//// menuItem.addActionListener(this);
	// menu.add(menuItem);
	//
	// //Set up the second menu item.
	// menuItem = new JMenuItem("Quit");
	// menuItem.setMnemonic(KeyEvent.VK_Q);
	// menuItem.setAccelerator(KeyStroke.getKeyStroke(
	// KeyEvent.VK_Q, ActionEvent.ALT_MASK));
	// menuItem.setActionCommand("quit");
	//// menuItem.addActionListener(this);
	// menu.add(menuItem);
	//
	// return menuBar;
	// }

	private JMenuBar generateMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		JMenu fme = new JMenu("������� ������");
		fme.setMnemonic(KeyEvent.VK_R);
		fme.getAccessibleContext().setAccessibleDescription("�������� �������");
		{

			JMenuItem RegularRobot = new JMenuItem("Regular", KeyEvent.VK_S);
			RegularRobot.addActionListener((event) -> {
				GameVisualizer.m_rbtsSet(new Robot());
			});
			JMenuItem BFSRobot = new JMenuItem("BFS", KeyEvent.VK_S);
			BFSRobot.addActionListener((event) -> {
				GameVisualizer.m_rbtsSet(new Robot("BFS"));
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
				GameVisualizer.setFlag("rectangle");
			});
			mode.add(addRect);
		}

		{
			JMenuItem removeTarget = new JMenuItem("������� �������������", KeyEvent.VK_S);
			removeTarget.addActionListener((event) -> {
				GameVisualizer.setFlag("remove");
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
				Logger.debug("����� ������");
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
}
