package gameObjects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public class RectsArray implements Iterable<Rectangle> {
	private static ArrayList<Rectangle> m_rcts = new ArrayList<Rectangle>();;

	public void m_rctsSet(Rectangle rectangle) {
		m_rcts.add(rectangle);
	}

	public void m_rctsRemove(Rectangle r) {
		m_rcts.remove(r);
	}
	public  void removeRectangle(Point p, Rectangle rect) {
		if (isInRectangle(p, rect))
			m_rctsRemove(rect);
	}
	public  boolean isInRectangle(Point p, Rectangle rect) {
		if ((p.x >= rect.getX()) && (p.x <= rect.getX() + rect.getWidth()) && (p.y <= rect.getY() + rect.getHeight())
				&& (p.y >= rect.getY()))
			return true;
		return false;
	}
	public ArrayList<Rectangle> getArrayRcts() {
		ArrayList<Rectangle> rects = m_rcts;
		return rects;
	}

	public RectsArray() {

	}

	public int size() {
		return m_rcts.size();
	}

	public Rectangle get(int i) {
		return m_rcts.get(i);
	}

	public boolean isEmpty() {
		return m_rcts.isEmpty();
	}

	@Override
	public Iterator<Rectangle> iterator() {
		return m_rcts.iterator();
	}
}
