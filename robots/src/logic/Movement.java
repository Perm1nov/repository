package logic;

import java.awt.Point;

import gameObjects.Robot;
import logic.Logic;

public class Movement {

	private double applyLimits(double value, double min, double max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}
	private Logic logic = new Logic();
	public void moveRobot(double velocity, double angularVelocity, double duration, Robot robot) {
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
		double newDirection = logic.angleTo(robot.getRobotX(), robot.getRobotY(), robot.getM_targetPositionX(),
				robot.getM_targetPositionY());
		// asNormalizedRadians(robot.m_robotDirection + angularVelocity * duration);
		robot.setRobotDirection(newDirection);
	}

	public void BFSMoveRobot(double velocity, double angularVelocity, double duration, Robot robot, Point temp) {
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
		double newDirection = logic.angleTo(robot.getRobotX(), robot.getRobotY(), temp.getX(), temp.getY());
		robot.setRobotDirection(newDirection);
	}
}
