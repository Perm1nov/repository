package gui;

import java.awt.Color;
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
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

import gui.Robot;
import log.Logger;

public class GameVisualizer extends JPanel {

	private static final long serialVersionUID = 1L;
	private int count = 0;
	private static String modeFlag = "rectangle";

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

	private static Timer initTimer() {
		Timer timer = new Timer("events generator", true);
		return timer;
	}

	private volatile int m_targetPositionX;
	private volatile int m_targetPositionY;
	private volatile Point m_targetPositionPoint;

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
				if (e.getButton() == MouseEvent.BUTTON3) {
					setTargetPosition(e.getPoint());
					if (!m_rcts.isEmpty()) {
						for (Robot r : m_rbts) {
							r.m_way = BFS(r);
							try {
								r.temp = r.m_way.pop();
							} catch (Exception ex) {
								throw new EmptyStackException();
							}
						}
					}
				} else if (modeFlag == "remove")
					removeRectangle(e.getPoint(), m_rcts);
				else if (count == 0) {
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

	protected void setTargetPosition(Point p) {
		m_targetPositionX = p.x;
		m_targetPositionY = p.y;
		m_targetPositionPoint = p;
	}

	protected void onRedrawEvent() {
		EventQueue.invokeLater(this::repaint);
	}

	private static double distance(double x1, double y1, double x2, double y2) {
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
		double distance = distance(m_targetPositionX, m_targetPositionY, robot.robotX, robot.robotY);
		if (distance < 1) {
			return;
		}
		double velocity = robot.maxVelocity;
		double angleToTarget = angleTo(robot.robotX, robot.robotY, m_targetPositionX, m_targetPositionY);
		double angularVelocity = 0;
		if (angleToTarget > robot.m_robotDirection) {
			angularVelocity = robot.maxAngularVelocity;
		}
		if (angleToTarget < robot.m_robotDirection) {
			angularVelocity = -robot.maxAngularVelocity;
		}

		if ((robot.mode == "BFS") && (!isAble(robot.robotX, robot.robotY, m_targetPositionX, m_targetPositionY))) {
			if ((robot.robotX <= robot.temp.x + 0.5 && robot.robotY <= robot.temp.y + 0.5)
					&& (robot.robotX >= robot.temp.x - 0.5 && robot.robotY >= robot.temp.y - 0.5)
					|| (robot.robotX >= robot.temp.x + 0.5 && robot.robotY >= robot.temp.y + 0.5)
							&& (robot.robotX <= robot.temp.x - 0.5 && robot.robotY <= robot.temp.y - 0.5)) {
				try {
					robot.temp = robot.m_way.pop();
				} catch (Exception e) {
				}

			}
			BFSMoveRobot(velocity, angularVelocity, 10, robot, robot.temp);
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
		double newDirection = angleTo(robot.robotX, robot.robotY, m_targetPositionX, m_targetPositionY);
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

		}
		drawTarget(g2d, m_targetPositionX, m_targetPositionY);
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

	private boolean isAble(double x, double y, double x1, double y1) {
		Line2D line = new Line2D.Double(x, y, x1, y1);
		for (Rectangle rect : m_rcts)
			if (line.intersects(rect.xn, rect.yn, rect.w, rect.h))
				return false;
		return true;
	}

	// просчитать и осорт
	public List<Point> getMap() {
		Rectangle temp;

		if (m_rcts.isEmpty())
			return null;
		List<Point> p = new ArrayList<Point>();
		for (int i = 0; i < m_rcts.size(); i++) {
			temp = m_rcts.get(i);
			p.add(temp.p0);
			p.add(temp.p1);
			p.add(temp.p2);
			p.add(temp.p3);
		}
		p.add(m_targetPositionPoint);
		return p;
	}

	public Stack<Point> BFS(Robot r) {
		int temp = 0;
		double max = Integer.MAX_VALUE;
		List<Point> map = getMap();
		for (int i = 0; i < map.size(); i++) {
			double c = distance(map.get(i).x, map.get(i).y, r.robotX, r.robotY);
			if (c < max) {
				max = c;
				temp = i;
			}

		}
		Logger.debug(String.valueOf(temp));
		int[] num = new int[map.size()];
		int[] father = new int[map.size()];
		num[temp] = 1;
		father[temp] = -1;
		Queue<Integer> q = new LinkedList<Integer>();
		Stack<Point> st = new Stack<Point>();
		int a = temp;
		q.add(a);
		while (!q.isEmpty()) {
			int pn = q.remove();
			for (int i = 0; i < num.length; i++) {
				if (isAble(map.get(pn).x, map.get(pn).y, map.get(i).x, map.get(i).y) && (num[i] != 1)) {
					num[i] = 1;
					father[i] = pn;
					q.add(i);

				}
			}
		}
		int y = map.size() - 1;
		while ((y != a) && (father[y] != -1)) {
			y = father[y];
			st.push(map.get(y));
		}
		Logger.debug(String.valueOf(st));
		return st;
	}
}

/********************************************************************************
 * Вопросы 1- приоритет отрисовки(что сверху) | 2- робот и край экрана(свёрнуто)
 * | 3-
 *********************************************************************************/