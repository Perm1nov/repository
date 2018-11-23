package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;

public class MainApplicationFrame extends JFrame {
	private final JDesktopPane desktopPane = new JDesktopPane();

	public MainApplicationFrame() {
		GameWindow gameWindow = new GameWindow();
		gameWindow.setSize(400, 400);
		int inset = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

		setContentPane(desktopPane);

		LogWindow logWindow = gameWindow.createLogWindow();
		gameWindow.addWindow(logWindow, desktopPane);

		gameWindow.addWindow(gameWindow, desktopPane);

		setJMenuBar(gameWindow.generateMenuBar());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

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
