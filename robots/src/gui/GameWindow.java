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
		super("Игровое поле", true, false, true, true);
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
		JMenu fme = new JMenu("Создать робота");
		fme.setMnemonic(KeyEvent.VK_R);
		fme.getAccessibleContext().setAccessibleDescription("Создание роботов");
		{

			JMenuItem RegularRobot = new JMenuItem("Regular", KeyEvent.VK_S);
			RegularRobot.addActionListener((event) -> {
				m_visualizer.m_rbtsSet(new Robot());
			});
			JMenuItem BFSRobot = new JMenuItem("BFS", KeyEvent.VK_S);
			BFSRobot.addActionListener((event) -> {
				m_visualizer.m_rbtsSet(new Robot("BFS"));
			});
			JMenu mods = new JMenu("Новый робот");
			fme.add(mods);
			mods.add(RegularRobot);
			mods.add(BFSRobot);
		}
		JMenu mode = new JMenu("Режим");
		mode.setMnemonic(KeyEvent.VK_R);
		mode.getAccessibleContext().setAccessibleDescription("Режимы");
		{
			JMenuItem addRect = new JMenuItem("Нарисовать прямоугольник", KeyEvent.VK_S);
			addRect.addActionListener((event) -> {
				m_visualizer.setFlag("create");
			});
			mode.add(addRect);
		}

		{
			JMenuItem removeTarget = new JMenuItem("Удалить прямоугольник", KeyEvent.VK_S);
			removeTarget.addActionListener((event) -> {
				m_visualizer.setFlag("remove");
			});
			mode.add(removeTarget);
		}

		JMenu lookAndFeelMenu = new JMenu("Режим отображения");
		lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
		lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

		{
			JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
			systemLookAndFeel.addActionListener((event) -> {
				setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				this.invalidate();
			});
			lookAndFeelMenu.add(systemLookAndFeel);
		}

		{
			JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
			crossplatformLookAndFeel.addActionListener((event) -> {
				setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				this.invalidate();
			});
			lookAndFeelMenu.add(crossplatformLookAndFeel);
		}

		JMenu testMenu = new JMenu("Тесты");
		testMenu.setMnemonic(KeyEvent.VK_T);
		testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

		{
			JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
			addLogMessageItem.addActionListener((event) -> {
				m_logger.debug("Новая строка");
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
		m_logger.debug("Протокол работает");
		return logWindow;
	}

	protected void addWindow(JInternalFrame frame, JDesktopPane desktopPane) {
		desktopPane.add(frame);
		frame.setVisible(true);
	}

}
