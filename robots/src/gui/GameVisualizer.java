package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import gameObjects.Rectangle;
import gameObjects.RectsArray;
import gameObjects.Robot;
import gameObjects.RobotsArray;
import logic.Logic;
import logic.Movement;

public class GameVisualizer extends JPanel {

	private static final long serialVersionUID = 1L;
	private int count = 0;

	private int x0;
	private int y0;
	private int x1;
	private int y1;

	private final Timer m_timer = initTimer();

	private static RobotsArray rbts = new RobotsArray();
	private static RectsArray rcts = new RectsArray();
	public RobotsArray getRbts()
	{
		return rbts;
	}
	public void addRbt(Robot r)
	{
		rbts.add(r);
	}
	private Movement movement = new Movement();
	private Logic logic = new Logic();
	{
		rbts.m_rbtsSet(new Robot("BFS"));
	}

	private Timer initTimer() {
		Timer timer = new Timer("events generator", true);
		return timer;
	}

	private Robot currentRobot = rbts.get(0);

	private volatile static Point m_targetPositionPoint;

	public Point getTargetPoint() {
		return m_targetPositionPoint;
	}

	public void setTargetPoint(Point p) {
		m_targetPositionPoint = p;
	}
	public GameVisualizer(String s)
	{
		
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
				if (rbts.isEmpty())
					return;
				for (Robot r : rbts) {
					onModelUpdateEvent(r);
				}
			}
		}, 0, 10);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				for (Robot robot : rbts) {
					if (e.getButton() == MouseEvent.BUTTON2
							&& ((Math.pow(e.getX() - logic.round(robot.getRobotX()), 2) / 30 * 30
									+ Math.pow(e.getY() - logic.round(robot.getRobotY()), 2)) / 10 * 10) <= 300) {
						currentRobot = robot;
						break;
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					setTargetPoint(e.getPoint());
					logic.setTargetPosition(e.getPoint(),currentRobot);
					if (!rcts.isEmpty()) {
						BFS(currentRobot);
					}
				} else if (logic.getFlag() == "remove")
					for (int i = 0; i < rcts.size(); i++) {
						rcts.removeRectangle(e.getPoint(), rcts.get(i));
					}
				else if ((logic.getFlag() == "create") && (e.getButton() != MouseEvent.BUTTON2))
					if (count == 0) {
						x0 = e.getPoint().x;
						y0 = e.getPoint().y;
						count++;
					} else {
						x1 = e.getPoint().x;
						y1 = e.getPoint().y;
						count = 0;
						rcts.m_rctsSet(new Rectangle(x0, y0, x1, y1));
					}
				repaint();
			}
		});
		setDoubleBuffered(true);

	}

	protected void onRedrawEvent() {
		EventQueue.invokeLater(this::repaint);
	}

	protected void onModelUpdateEvent(Robot robot) {
		if (robot.getAlive() == false)
			return;

		for (Rectangle rect : rcts) {
			if ((robot.getRobotX() >= rect.getX()) && (robot.getRobotY() >= rect.getY())
					&& (robot.getRobotX() <= rect.getX() + rect.getWidth())
					&& (robot.getRobotY() <= rect.getY() + rect.getHeight())) {
				robot.setAlive(false);
				return;
			}
		}
		double distance = logic.distance(robot.getM_targetPositionX(), robot.getM_targetPositionY(), robot.getRobotX(),
				robot.getRobotY());
		if (distance < 1) {
			return;
		}
		double velocity = robot.getMaxVelocity();
		double angleToTarget = logic.angleTo(robot.getRobotX(), robot.getRobotY(), robot.getM_targetPositionX(),
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
			movement.BFSMoveRobot(velocity, angularVelocity, 10, robot, robot.getNextPoint());
		} else
			movement.moveRobot(velocity, angularVelocity, 10, robot);
	}

	public void paint(Graphics g) {

		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		for (Robot r : rbts) {
			drawRobot(g2d, r);
			drawTarget(g2d, r.getM_targetPositionX(), r.getM_targetPositionY());
		}

		for (Rectangle rect : rcts) {
			g.setColor(Color.BLACK);
			g.fillRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
		}
	}

	private void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
		g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
	}

	private void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
		g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
	}

	private void drawRobot(Graphics2D g, Robot robot) {
		int robotCenterX = logic.round(robot.getRobotX());
		int robotCenterY = logic.round(robot.getRobotY());
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

	public List<Point> getMap() {
		Rectangle temp;
		ArrayList<Rectangle> tempArray = rcts.getArrayRcts();
		if (tempArray.isEmpty())
			return null;
		List<Point> p = new ArrayList<Point>();
		for (int i = 0; i < tempArray.size(); i++) {
			temp = tempArray.get(i);
			p.add(temp.getP0());
			p.add(temp.getP1());
			p.add(temp.getP2());
			p.add(temp.getP3());
		}
		p.add(getTargetPoint());
		return p;
	}

	public void BFS(Robot r) {
		int temp = 0;
		double max = Integer.MAX_VALUE;
		List<Point> map = getMap();
		for (int i = 0; i < map.size(); i++) {
			double c = logic.distance(map.get(i).x, map.get(i).y, r.getRobotX(), r.getRobotY());
			if (c < max) {
				max = c;
				temp = i;
			}

		}
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
		r.setM_way(st);
		if (!st.isEmpty())
			r.setNextPoint(st.pop());
	}

	public boolean isAble(double x, double y, double x1, double y1) {
		Line2D line = new Line2D.Double(x, y, x1, y1);
		for (Rectangle rect : rcts)
			if (line.intersects(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()))
				return false;
		return true;
	}

}