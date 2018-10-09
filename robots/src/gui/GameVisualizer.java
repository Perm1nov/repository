package gui;

import java.awt.*;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import gui.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import gui.Robot;

public class GameVisualizer extends JPanel {

	private static final long serialVersionUID = 1L;
	private int count = 0;
	private static String modeFlag = "";

	public static void setFlag(String str) {
		modeFlag = str;
	}

	private final Timer m_timer = initTimer();
	static ArrayList<Rectangle> m_rcts = new ArrayList<Rectangle>();

	private static void m_rctsSet(Rectangle rectangle) {
		m_rcts.add(rectangle);
	}

	private static ArrayList<Robot> m_rbts = new ArrayList<Robot>();

	public static void m_rbtsSet(Robot obj) {
		m_rbts.add(obj);
	}

	{
		m_rbtsSet(new Robot("BFS"));
	}

	private static Timer initTimer() {
		Timer timer = new Timer("events generator", true);
		return timer;
	}

	Robot currentRobot = m_rbts.get(0);

	volatile static Point m_targetPositionPoint;

	public static Point getTargetPoint() {
		return m_targetPositionPoint;
	}

	int x0;
	int y0;
	int x1;
	int y1;

	public GameVisualizer() {
		m_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				onRedrawEvent();
			}
		}, 0, 15);
		m_timer.schedule(new TimerTask() {
			public void run() {
				if (m_rbts.isEmpty())
					return;
				for (Robot r : m_rbts) {
					onModelUpdateEvent(r);
				}
			}
		}, 0, 10);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				for (Robot robot : m_rbts) {
					if (e.getButton() == MouseEvent.BUTTON2 && ((Math.pow(e.getX() - round(robot.robotX), 2) / 30 * 30
							+ Math.pow(e.getY() - round(robot.robotY), 2)) / 10 * 10) <= 300) {
						currentRobot = robot;
						break;
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					setTargetPosition(e.getPoint(), currentRobot);
					if (!m_rcts.isEmpty()) {
						for (Robot r : m_rbts) {
							r.BFS();
						}
					}
				} else if (modeFlag == "remove")
					removeRectangle(e.getPoint(), m_rcts);
				else if (modeFlag == "rectangle")
					if (count == 0) {
						x0 = e.getPoint().x;
						y0 = e.getPoint().y;
						count++;
					} else {
						x1 = e.getPoint().x;
						y1 = e.getPoint().y;
						count = 0;
						m_rctsSet(new Rectangle(x0, y0, x1, y1));
					}

				repaint();
			}
		});
		setDoubleBuffered(true);

	}

	protected void setTargetPosition(Point p, Robot r) {
		r.m_targetPositionX = p.x;
		r.m_targetPositionY = p.y;
		m_targetPositionPoint = p;
	}

	protected void onRedrawEvent() {
		EventQueue.invokeLater(this::repaint);
	}

	static double distance(double x1, double y1, double x2, double y2) {
		double diffX = x1 - x2;
		double diffY = y1 - y2;
		return Math.sqrt(diffX * diffX + diffY * diffY);
	}

	private static double angleTo(double fromX, double fromY, double toX, double toY) {
		double diffX = toX - fromX;
		double diffY = toY - fromY;

		return asNormalizedRadians(Math.atan2(diffY, diffX));
	}

	protected void onModelUpdateEvent(Robot robot) {
		if (!robot.alive)
			return;

		for (Rectangle rect : m_rcts) {
			if ((robot.robotX >= rect.getX()) && (robot.robotY >= rect.getY())
					&& (robot.robotX <= rect.getX() + rect.getWidth())
					&& (robot.robotY <= rect.getY() + rect.getHeight())) {
				robot.alive = false;
				return;
			}
		}
		double distance = distance(robot.m_targetPositionX, robot.m_targetPositionY, robot.robotX, robot.robotY);
		if (distance < 1) {
			return;
		}
		double velocity = robot.maxVelocity;
		double angleToTarget = angleTo(robot.robotX, robot.robotY, robot.m_targetPositionX, robot.m_targetPositionY);
		double angularVelocity = 0;
		if (angleToTarget > robot.m_robotDirection) {
			angularVelocity = robot.maxAngularVelocity;
		}
		if (angleToTarget < robot.m_robotDirection) {
			angularVelocity = -robot.maxAngularVelocity;
		}

		if ((robot.mode == "BFS")
				&& (!isAble(robot.robotX, robot.robotY, robot.m_targetPositionX, robot.m_targetPositionY))) {
			if(robot.m_temp==null)
				robot.m_temp = robot.m_way.pop();
			if ((robot.robotX <= robot.m_temp.x + 0.5 && robot.robotY <= robot.m_temp.y + 0.5)
					&& (robot.robotX >= robot.m_temp.x - 0.5 && robot.robotY >= robot.m_temp.y - 0.5)
					|| (robot.robotX >= robot.m_temp.x + 0.5 && robot.robotY >= robot.m_temp.y + 0.5)
							&& (robot.robotX <= robot.m_temp.x - 0.5 && robot.robotY <= robot.m_temp.y - 0.5)) {
				try {
					robot.m_temp = robot.m_way.pop();
				} catch (Exception e) {
				}

			}
			BFSMoveRobot(velocity, angularVelocity, 10, robot, robot.m_temp);
		} else
			moveRobot(velocity, angularVelocity, 10, robot);
	}

	private static double applyLimits(double value, double min, double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	private void moveRobot(double velocity, double angularVelocity, double duration, Robot robot) {
		velocity = applyLimits(velocity, 0, robot.maxVelocity);
		angularVelocity = applyLimits(angularVelocity, -robot.maxAngularVelocity, robot.maxAngularVelocity);
		double newX = robot.robotX + velocity / angularVelocity
				* (Math.sin(robot.m_robotDirection + angularVelocity * duration) - Math.sin(robot.m_robotDirection));
		if (!Double.isFinite(newX)) {
			newX = robot.robotX + velocity * duration * Math.cos(robot.m_robotDirection);
		}
		double newY = robot.robotY - velocity / angularVelocity
				* (Math.cos(robot.m_robotDirection + angularVelocity * duration) - Math.cos(robot.m_robotDirection));
		if (!Double.isFinite(newY)) {
			newY = robot.robotY + velocity * duration * Math.sin(robot.m_robotDirection);
		}
		robot.robotX = newX;
		robot.robotY = newY;
		double newDirection = angleTo(robot.robotX, robot.robotY, robot.m_targetPositionX, robot.m_targetPositionY);
		// asNormalizedRadians(robot.m_robotDirection + angularVelocity * duration);
		robot.m_robotDirection = newDirection;
	}

	private void BFSMoveRobot(double velocity, double angularVelocity, double duration, Robot robot, Point temp) {
		velocity = applyLimits(velocity, 0, robot.maxVelocity);
		angularVelocity = applyLimits(angularVelocity, -robot.maxAngularVelocity, robot.maxAngularVelocity);
		double newX = robot.robotX + velocity / angularVelocity
				* (Math.sin(robot.m_robotDirection + angularVelocity * duration) - Math.sin(robot.m_robotDirection));
		if (!Double.isFinite(newX)) {
			newX = robot.robotX + velocity * duration * Math.cos(robot.m_robotDirection);
		}
		double newY = robot.robotY - velocity / angularVelocity
				* (Math.cos(robot.m_robotDirection + angularVelocity * duration) - Math.cos(robot.m_robotDirection));
		if (!Double.isFinite(newY)) {
			newY = robot.robotY + velocity * duration * Math.sin(robot.m_robotDirection);
		}
		robot.robotX = newX;
		robot.robotY = newY;
		double newDirection = angleTo(robot.robotX, robot.robotY, temp.getX(), temp.getY());
		robot.m_robotDirection = newDirection;
	}

	private static double asNormalizedRadians(double angle) {
		while (angle < 0) {
			angle += 2 * Math.PI;
		}
		while (angle >= 2 * Math.PI) {
			angle -= 2 * Math.PI;
		}
		return angle;
	}

	private static int round(double value) {
		return (int) (value + 0.5);
	}

	public void paint(Graphics g) {

		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		for (Robot r : m_rbts) {
			drawRobot(g2d, r);
			drawTarget(g2d, r.m_targetPositionX, r.m_targetPositionY);
		}

		for (Rectangle rect : m_rcts) {
			g.setColor(Color.BLACK);
			g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
		}
	}

	private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
		g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
	}

	private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
		g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
	}

	private void drawRobot(Graphics2D g, Robot robot) {
		int robotCenterX = round(robot.robotX);
		int robotCenterY = round(robot.robotY);
		AffineTransform t = AffineTransform.getRotateInstance(robot.m_robotDirection, robotCenterX, robotCenterY);
		g.setTransform(t);
		g.setColor(Color.MAGENTA);
		fillOval(g, robotCenterX, robotCenterY, 30, 10);
		g.setColor(Color.BLACK);
		drawOval(g, robotCenterX, robotCenterY, 30, 10);
		g.setColor(Color.WHITE);
		fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
		g.setColor(Color.BLACK);
		drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
	}

	private void drawTarget(Graphics2D g, int x, int y) {
		AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
		g.setTransform(t);
		g.setColor(Color.GREEN);
		fillOval(g, x, y, 5, 5);
		g.setColor(Color.BLACK);
		drawOval(g, x, y, 5, 5);
	}

	private void removeRectangle(Point p, ArrayList<Rectangle> rects) {
		if (!rects.isEmpty())
			for (int i = 0; i < rects.size(); i++) {
				if (isInRectangle(p, rects.get(i)))
					rects.remove(rects.get(i));
			}
		else
			return;
	}

	private boolean isInRectangle(Point p, Rectangle rect) {
		if ((p.x >= rect.getX()) && (p.x <= rect.getX() + rect.getWidth()) && (p.y <= rect.getY() + rect.getHeight())
				&& (p.y >= rect.getY()))
			return true;
		return false;

	}

	static boolean isAble(double x, double y, double x1, double y1) {
		Line2D line = new Line2D.Double(x, y, x1, y1);
		for (Rectangle rect : m_rcts)
			if (line.intersects(rect.xn, rect.yn, rect.w, rect.h))
				return false;
		return true;
	}

}
/********************************************************************************
 * приоритет отрисовки????
 *********************************************************************************/