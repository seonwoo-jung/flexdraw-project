package model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.shapes.AbstractShape;

public class ShapeStore {

	private final List<AbstractShape> shapes = new ArrayList<>();

	public List<AbstractShape> all() {
		return Collections.unmodifiableList(shapes);
	}

	public void add(AbstractShape s) {
		shapes.add(s);
	}

	public void remove(AbstractShape s) {
		shapes.remove(s);
	}

	public int indexOf(AbstractShape s) {
		return shapes.indexOf(s);
	}

	public void insertAt(AbstractShape s, int idx) {
		shapes.add(Math.max(0, Math.min(idx, shapes.size())), s);
	}

	public void bringToFront(AbstractShape s) {
		if (contains(s)) {
			shapes.remove(s);
			shapes.add(s);
		}
	}

	public void sendToBack(AbstractShape s) {
		if (contains(s)) {
			shapes.remove(s);
			shapes.add(0, s);
		}
	}

	public boolean contains(AbstractShape s) {
		return shapes.contains(s);
	}
}