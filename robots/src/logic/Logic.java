package logic;

import java.awt.Point;

import gameObjects.Rectangle;
import gameObjects.Robot;

public class Logic {
	public int round(double value) {
		return (int) (value + 0.5);
	}

	public double applyLimits(double value, double min, double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	public double angleTo(double fromX, double fromY, double toX, double toY) {
		double diffX = toX - fromX;
		double diffY = toY - fromY;

		return asNormalizedRadians(Math.atan2(diffY, diffX));
	}

	public double distance(double x1, double y1, double x2, double y2) {
		double diffX = x1 - x2;
		double diffY = y1 - y2;
		return Math.sqrt(diffX * diffX + diffY * diffY);
	}

	public double asNormalizedRadians(double angle) {
		while (angle < 0) {
			angle += 2 * Math.PI;
		}
		while (angle >= 2 * Math.PI) {
			angle -= 2 * Math.PI;
		}
		return angle;
	}

	public void setTargetPosition(Point p, Robot r) {
		r.setM_targetPositionX(p.x);
		r.setM_targetPositionY(p.y);
	}

	public boolean isInRectangle(Point p, Rectangle rect) {
		if ((p.x >= rect.getX()) && (p.x <= rect.getX() + rect.getWidth()) && (p.y <= rect.getY() + rect.getHeight())
				&& (p.y >= rect.getY()))
			return true;
		return false;
	}

}
