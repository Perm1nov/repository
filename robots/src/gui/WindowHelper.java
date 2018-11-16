package gui;

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

import gameObjects.Robot;
import log.Logger;
import logic.Logic;

public class WindowHelper extends JFrame {

	private static final long serialVersionUID = 1L;

	protected LogWindow createLogWindow() {
		LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
		logWindow.setLocation(10, 10);
		logWindow.setSize(300, 800);
		setMinimumSize(logWindow.getSize());
		logWindow.pack();
		Logger.debug("�������� ��������");
		return logWindow;
	}

	private Logic logic = new Logic();

	protected void addWindow(JInternalFrame frame, JDesktopPane desktopPane) {
		desktopPane.add(frame);
		frame.setVisible(true);
	}

	private GameVisualizer gv = new GameVisualizer("asd");

	JMenuBar generateMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		JMenu fme = new JMenu("������� ������");
		fme.setMnemonic(KeyEvent.VK_R);
		fme.getAccessibleContext().setAccessibleDescription("�������� �������");
		{

			JMenuItem RegularRobot = new JMenuItem("Regular", KeyEvent.VK_S);
			RegularRobot.addActionListener((event) -> {

				gv.addRbt(new Robot());
			});
			JMenuItem BFSRobot = new JMenuItem("BFS", KeyEvent.VK_S);
			BFSRobot.addActionListener((event) -> {
				gv.addRbt(new Robot("BFS"));
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
				logic.setFlag("create");
			});
			mode.add(addRect);
		}

		{
			JMenuItem removeTarget = new JMenuItem("������� �������������", KeyEvent.VK_S);
			removeTarget.addActionListener((event) -> {
				logic.setFlag("remove");
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
