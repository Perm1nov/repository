package logic;

import java.awt.Point;

import gameObjects.Robot;

public class Logic {
	public static int round(double value) {
		return (int) (value + 0.5);
	}

	private static double applyLimits(double value, double min, double max) {
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

	public static void moveRobot(double velocity, double angularVelocity, double duration, Robot robot) {
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

	public static void BFSMoveRobot(double velocity, double angularVelocity, double duration, Robot robot, Point temp) {
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
}
