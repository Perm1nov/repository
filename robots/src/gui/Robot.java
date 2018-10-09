package gui;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import log.Logger;

public class Robot {

	public volatile double robotX = 0;
	public volatile double robotY = 0;
	 volatile Point m_robotPoint;
	public volatile double m_robotDirection = 500;
	 volatile double maxVelocity = 0.09;
	 volatile double maxAngularVelocity = 0.009;
	volatile int m_targetPositionX;
	volatile int m_targetPositionY;

	volatile Stack<Point> m_way = new Stack<Point>();
	protected volatile Point m_temp;
	public boolean alive = true;
	public String mode = "";

	public Robot(String mode) {
		setMode(mode);
	}

	public Robot() {
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public static List<Point> getMap() {
		Rectangle temp;

		if (GameVisualizer.m_rcts.isEmpty())
			return null;
		List<Point> p = new ArrayList<Point>();
		for (int i = 0; i < GameVisualizer.m_rcts.size(); i++) {
			temp = GameVisualizer.m_rcts.get(i);
			p.add(temp.p0);
			p.add(temp.p1);
			p.add(temp.p2);
			p.add(temp.p3);
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
		Logger.debug(String.valueOf(st));
		m_way = st;
		if(!st.isEmpty())
		m_temp = m_way.pop();
	}
}
