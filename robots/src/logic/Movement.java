package logic;

import java.awt.Point;

import gameObjects.Robot;
import logic.Logic;

public class Movement {

	public static void moveRobot(double velocity, double angularVelocity, double duration, Robot robot) {
		velocity = Logic.applyLimits(velocity, 0, robot.getMaxVelocity());
		angularVelocity = Logic.applyLimits(angularVelocity, -robot.getmaxAngularVelocity(), robot.getmaxAngularVelocity());
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
		double newDirection = Logic.angleTo(robot.getRobotX(), robot.getRobotY(), robot.getM_targetPositionX(),
				robot.getM_targetPositionY());
		// asNormalizedRadians(robot.m_robotDirection + angularVelocity * duration);
		robot.setRobotDirection(newDirection);
	}

	public static void BFSMoveRobot(double velocity, double angularVelocity, double duration, Robot robot, Point temp) {
		velocity = Logic.applyLimits(velocity, 0, robot.getMaxVelocity());
		angularVelocity = Logic.applyLimits(angularVelocity, -robot.getmaxAngularVelocity(), robot.getmaxAngularVelocity());
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
		double newDirection = Logic.angleTo(robot.getRobotX(), robot.getRobotY(), temp.getX(), temp.getY());
		robot.setRobotDirection(newDirection);
	}
}
