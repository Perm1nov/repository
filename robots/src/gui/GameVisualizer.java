package gui;

import java.awt.*;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

import gameObjects.Rectangle;
import gameObjects.Robot;

public class GameVisualizer extends JPanel {

	private static final long serialVersionUID = 1L;
	private int count = 0;
	private static String modeFlag = "create";
	private int x0;
	private int y0;
	private int x1;
	private int y1;

	public static void setFlag(String str) {
		modeFlag = str;
	}

	private final Timer m_timer = initTimer();
	private static ArrayList<Rectangle> m_rcts = new ArrayList<Rectangle>();

	public static void m_rctsSet(Rectangle rectangle) {
		m_rcts.add(rectangle);
	}

	public static ArrayList<Rectangle> getArrayRcts() {
		ArrayList<Rectangle> rects = m_rcts;
		return rects;
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

	private Robot currentRobot = m_rbts.get(0);

	private volatile static Point m_targetPositionPoint;

	public static Point getTargetPoint() {
		return m_targetPositionPoint;
	}

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
					if (e.getButton() == MouseEvent.BUTTON2
							&& ((Math.pow(e.getX() - round(robot.getRobotX()), 2) / 30 * 30
									+ Math.pow(e.getY() - round(robot.getRobotY()), 2)) / 10 * 10) <= 300) {
						currentRobot = robot;
						break;
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					setTargetPosition(e.getPoint(), currentRobot);
					if (!m_rcts.isEmpty()) {
						currentRobot.BFS();
					}
				} else if (modeFlag == "remove")
					removeRectangle(e.getPoint(), m_rcts);
				else if ((modeFlag == "create") && (e.getButton() != MouseEvent.BUTTON2))
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
		r.setM_targetPositionX(p.x);
		r.setM_targetPositionY(p.y);
		m_targetPositionPoint = p;
	}

	protected void onRedrawEvent() {
		EventQueue.invokeLater(this::repaint);
	}

	public static double distance(double x1, double y1, double x2, double y2) {
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
		if (robot.getAlive() == false)
			return;

		for (Rectangle rect : m_rcts) {
			if ((robot.getRobotX() >= rect.getX()) && (robot.getRobotY() >= rect.getY())
					&& (robot.getRobotX() <= rect.getX() + rect.getWidth())
					&& (robot.getRobotY() <= rect.getY() + rect.getHeight())) {
				robot.setAlive(false);
				return;
			}
		}
		double distance = distance(robot.getM_targetPositionX(), robot.getM_targetPositionY(), robot.getRobotX(),
				robot.getRobotY());
		if (distance < 1) {
			return;
		}
		double velocity = robot.getMaxVelocity();
		double angleToTarget = angleTo(robot.getRobotX(), robot.getRobotY(), robot.getM_targetPositionX(),
				robot.getM_targetPositionY());
		double angularVelocity = 0;
		if (angleToTarget > robot.getRobotDirection()) {
			angularVelocity = robot.getmaxAngularVelocity();
		}
		if (angleToTarget < robot.getRobotDirection()) {
			angularVelocity = -robot.getmaxAngularVelocity();
		}

		if ((robot.get_mode() == "BFS") && (!isAble(robot.getRobotX(), robot.getRobotY(), robot.getM_targetPositionX(),
				robot.getM_targetPositionY())) && (robot.getNextPoint() != null)) {

			if ((robot.getRobotX() <= robot.getNextPoint().x + 0.5 && robot.getRobotY() <= robot.getNextPoint().y + 0.5)
					&& (robot.getRobotX() >= robot.getNextPoint().x - 0.5
							&& robot.getRobotY() >= robot.getNextPoint().y - 0.5)
					|| (robot.getRobotX() >= robot.getNextPoint().x + 0.5
							&& robot.getRobotY() >= robot.getNextPoint().y + 0.5)
							&& (robot.getRobotX() <= robot.getNextPoint().x - 0.5
									&& robot.getRobotY() <= robot.getNextPoint().y - 0.5)) {
				robot.setNextPoint();
			}
			BFSMoveRobot(velocity, angularVelocity, 10, robot, robot.getNextPoint());
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
		velocity = applyLimits(velocity, 0, robot.getMaxVelocity());
		angularVelocity = applyLimits(angularVelocity, -robot.getmaxAngularVelocity(), robot.getmaxAngularVelocity());
		double newX = robot.getRobotX()
				+ velocity / angularVelocity * (Math.sin(robot.getRobotDirection() + angularVelocity * duration)
						- Math.sin(robot.getRobotDirection()));
		if (!Double.isFinite(newX)) {
			newX = robot.getRobotX() + velocity * duration * Math.cos(robot.getRobotDirection());
		}
		double newY = robot.getRobotY()
				- velocity / angularVelocity * (Math.cos(robot.getRobotDirection() + angularVelocity * duration)
						- Math.cos(robot.getRobotDirection()));
		if (!Double.isFinite(newY)) {
			newY = robot.getRobotY() + velocity * duration * Math.sin(robot.getRobotDirection());
		}
		robot.setRobotX(newX);
		robot.setRobotY(newY);
		double newDirection = angleTo(robot.getRobotX(), robot.getRobotY(), robot.getM_targetPositionX(),
				robot.getM_targetPositionY());
		// asNormalizedRadians(robot.m_robotDirection + angularVelocity * duration);
		robot.setRobotDirection(newDirection);
	}

	private void BFSMoveRobot(double velocity, double angularVelocity, double duration, Robot robot, Point temp) {
		velocity = applyLimits(velocity, 0, robot.getMaxVelocity());
		angularVelocity = applyLimits(angularVelocity, -robot.getmaxAngularVelocity(), robot.getmaxAngularVelocity());
		double newX = robot.getRobotX()
				+ velocity / angularVelocity * (Math.sin(robot.getRobotDirection() + angularVelocity * duration)
						- Math.sin(robot.getRobotDirection()));
		if (!Double.isFinite(newX)) {
			newX = robot.getRobotX() + velocity * duration * Math.cos(robot.getRobotDirection());
		}
		double newY = robot.getRobotY()
				- velocity / angularVelocity * (Math.cos(robot.getRobotDirection() + angularVelocity * duration)
						- Math.cos(robot.getRobotDirection()));
		if (!Double.isFinite(newY)) {
			newY = robot.getRobotY() + velocity * duration * Math.sin(robot.getRobotDirection());
		}
		robot.setRobotX(newX);
		robot.setRobotY(newY);
		double newDirection = angleTo(robot.getRobotX(), robot.getRobotY(), temp.getX(), temp.getY());
		robot.setRobotDirection(newDirection);
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
			drawTarget(g2d, r.getM_targetPositionX(), r.getM_targetPositionY());
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
		int robotCenterX = round(robot.getRobotX());
		int robotCenterY = round(robot.getRobotY());
		AffineTransform t = AffineTransform.getRotateInstance(robot.getRobotDirection(), robotCenterX, robotCenterY);
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

	public static boolean isAble(double x, double y, double x1, double y1) {
		Line2D line = new Line2D.Double(x, y, x1, y1);
		for (Rectangle rect : m_rcts)
			if (line.intersects(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()))
				return false;
		return true;
	}

}