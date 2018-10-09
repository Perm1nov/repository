package gui;

import java.awt.Point;

public class Rectangle {
	private float x0, y0; // координаты левого верхнего угла
	private float x1, y1; // координаты второго угла (координаты положения мыши)
	public int w;
	public int h;
	public int xn;
	public int yn;
	public Point p0;
	public Point p1;
	public Point p2;
	public Point p3;

	public Rectangle(int x0, int y0, int x1, int y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
		w = (int) Math.abs(this.x1 - this.x0);
		h = (int) Math.abs(this.y1 - this.y0);
		xn = (int) Math.min(this.x1, this.x0);
		yn = (int) Math.min(this.y1, this.y0);
		p0 = new Point(xn - 6, yn - 6);
		p1 = new Point(xn + w + 6, yn - 6);
		p2 = new Point(xn - 6, yn + h + 6);
		p3 = new Point(xn + w + 6, yn + h + 6);
	}

	public double getHeight() {
		// TODO Auto-generated method stub
		return h;
	}

	public double getWidth() {
		// TODO Auto-generated method stub
		return w;
	}

	public double getX() {
		// TODO Auto-generated method stub
		return xn;
	}

	public double getY() {
		// TODO Auto-generated method stub
		return yn;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toString(Rectangle this) {
		return this.p0 + " " + this.p1 + " " + this.p2 + " " + this.p3;
	}

}
