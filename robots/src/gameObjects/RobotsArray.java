package gameObjects;

import java.util.ArrayList;
import java.util.Iterator;

public class RobotsArray implements Iterable<Robot>{
	private  ArrayList<Robot> m_rbts;

	public  void m_rbtsSet(Robot obj) {
		m_rbts.add(obj);
	}
	public Robot get(int number) {
		return m_rbts.get(number);
	}
	public RobotsArray()
	{
		m_rbts = new ArrayList<Robot>();
	}
	public boolean isEmpty() {
	 return m_rbts.isEmpty();
	}
	@Override
	public Iterator<Robot> iterator() {
		return m_rbts.iterator();
	}
	public void add(Robot r) {
		m_rbts.add(r);
	}
}
