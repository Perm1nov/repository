package logic;

import java.awt.Point;

import gameObjects.Rectangle;
import gameObjects.Robot;
import gui.GameVisualizer;

public class Logic {
	public static int round(double value) {
		return (int) (value + 0.5);
	}

	 static double applyLimits(double value, double min, double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	 public static double angleTo(double fromX, double fromY, double toX, double toY) {
		double diffX = toX - fromX;
		double diffY = toY - fromY;

		return asNormalizedRadians(Math.atan2(diffY, diffX));
	}

	 public static double distance(double x1, double y1, double x2, double y2) {
		double diffX = x1 - x2;
		double diffY = y1 - y2;
		return Math.sqrt(diffX * diffX + diffY * diffY);
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

	public static void setTargetPosition(Point p, Robot r) {
		r.setM_targetPositionX(p.x);
		r.setM_targetPositionY(p.y);
	}

	

	
	public static void removeRectangle(Point p, Rectangle rect) {
		if (isInRectangle(p, rect))
			GameVisualizer.m_rctsRemove(rect);
	}
	public static boolean isInRectangle(Point p, Rectangle rect) {
		if ((p.x >= rect.getX()) && (p.x <= rect.getX() + rect.getWidth()) && (p.y <= rect.getY() + rect.getHeight())
				&& (p.y >= rect.getY()))
			return true;
		return false;
	}

}
