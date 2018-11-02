package gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Robot {

	private volatile double robotX = 0;

	public double getRobotX() {
		return robotX;
	}

	public void setRobotX(double newX) {
		robotX = newX;
	}

	private volatile double robotY = 0;

	public double getRobotY() {
		return robotY;
	}

	public void setRobotY(double newY) {
		robotY = newY;
	}

	private volatile double m_robotDirection = 500;

	public double getRobotDirection() {
		return m_robotDirection;
	}

	public void setRobotDirection(double value) {
		m_robotDirection = value;
	}

	private volatile double maxVelocity = 0.09;

	public double getMaxVelocity() {
		return maxVelocity;
	}

	private volatile double maxAngularVelocity = 0.009;

	public double getmaxAngularVelocity() {
		return maxAngularVelocity;
	}

	private volatile int m_targetPositionX;

	public int getM_targetPositionX() {
		return m_targetPositionX;
	}

	public void setM_targetPositionX(int m_targetPositionX) {
		this.m_targetPositionX = m_targetPositionX;
	}

	private volatile int m_targetPositionY;

	public int getM_targetPositionY() {
		return m_targetPositionY;
	}

	public void setM_targetPositionY(int m_targetPositionY) {
		this.m_targetPositionY = m_targetPositionY;
	}

	private volatile Stack<Point> m_way = new Stack<Point>();
	private Point m_temp;
	public Point getNextPoint() {
		return m_temp;
	}

	public void setNextPoint() {
		m_temp = m_way.pop();
	}

	private boolean status = true;

	public void setAlive(boolean status) {
		if (status == false)
			this.status = status;
	}

	public boolean getAlive() {
		return status;
	}

	private String mode = "";

	public String get_mode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Robot(String mode) {
		setMode(mode);
	}

	public Robot() {
	}

	public static List<Point> getMap() {
		Rectangle temp;
		ArrayList<Rectangle> tempArray = GameVisualizer.getArrayRcts();
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
		p.add(GameVisualizer.getTargetPoint());
		return p;
	}

	public void BFS() {
		int temp = 0;
		double max = Integer.MAX_VALUE;
		List<Point> map = getMap();
		for (int i = 0; i < map.size(); i++) {
			double c = GameVisualizer.distance(map.get(i).x, map.get(i).y, robotX, robotY);
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
				if (GameVisualizer.isAble(map.get(pn).x, map.get(pn).y, map.get(i).x, map.get(i).y) && (num[i] != 1)) {
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
		m_way = st;
		if (!st.isEmpty())
			m_temp = st.pop();
	}

}
