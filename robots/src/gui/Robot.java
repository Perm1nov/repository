package gui;

import java.awt.Point;
import java.util.Stack;

public class Robot {
	
	public volatile double robotX = 0;
	public volatile double robotY = 0; 
    public volatile Point m_robotPoint;
    public volatile double m_robotDirection = 500; 
    public volatile double maxVelocity = 0.09; 
    public volatile double  maxAngularVelocity = 0.009; 
    
    public volatile Stack<Point> m_way = new Stack<Point>();
    public volatile Point temp;
    public boolean alive = true;
    public String mode ="";
    public Robot(String mode)
    {
    	setMode(mode);
    }
    public Robot()
    {
    }
   public void setMode (String mode)
   {
	   this.mode = mode;
   }
}
