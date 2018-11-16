package logic;

import java.awt.Point;
import gameObjects.Robot;

public class Logic {
	public int round(double value) {
		return (int) (value + 0.5);
	}

	private static String modeFlag = "create";
	public void setFlag(String str) {
		modeFlag = str;
	}
	public String getFlag()
	{
		return modeFlag;
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

	private double asNormalizedRadians(double angle) {
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
		r.setM_targetPoint(p);
	}
	

}
