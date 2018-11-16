package gameObjects;

import java.awt.Point;
import java.util.Stack;

public class Robot {

	private volatile double robotX = 50;

	public double getRobotX() {
		return robotX;
	}

	public void setRobotX(double newX) {
		robotX = newX;
	}

	private volatile double robotY = 50;

	public double getRobotY() {
		return robotY;
	}

	public void setRobotY(double newY) {
		robotY = newY;
	}

	private volatile double m_robotDirection = 100;

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

	private volatile Point m_targetPoint;

	public Point getM_targetPoint() {
		return m_targetPoint;
	}

	public void setM_targetPoint(Point point) {
		this.m_targetPoint = point;
	}

	private volatile Stack<Point> m_way = new Stack<Point>();
	public void setM_way(Stack<Point> st)
	{
		m_way = st;
	}
	private  Point m_temp;

	public Point getNextPoint() {
		return m_temp;
	}
	public void setNextPoint(Point p)
	{
		m_temp = p;
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

	
	//private Logic logic = new Logic();
	

}
