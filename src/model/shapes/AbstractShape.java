package model.shapes;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractShape {

	protected final Map<String, Object> props = new LinkedHashMap<>(); // preserve insertion order

	public AbstractShape() {
		props.putIfAbsent("x", 0.0);
		props.putIfAbsent("y", 0.0);
		props.putIfAbsent("width", 0.0);
		props.putIfAbsent("height", 0.0);
		props.putIfAbsent("fill-color", Color.LIGHT_GRAY);
		props.putIfAbsent("stroke-color", Color.DARK_GRAY);
		props.putIfAbsent("stroke-width", 2.0);
		props.putIfAbsent("rotation-angle", 0.0);
		props.putIfAbsent("name", getDefaultName());
	}

	protected abstract String getDefaultName();

	public abstract void draw(Graphics2D g2, boolean selected);

	public abstract boolean contains(Point2D p);

	public abstract Shape getGeometry();

	public Map<String, Object> getProps() {
		return props;
	}

	public double getDouble(String key) {
		Object v = props.get(key);
		if (v instanceof Number)
			return ((Number)v).doubleValue();
		if (v instanceof String s) {
			try {
				return Double.parseDouble(s.trim());
			} catch (Exception ignore) {

			}
		}
		return 0.0;
	}

	public int getInt(String key) {
		Object v = props.get(key);
		if (v instanceof Number)
			return ((Number)v).intValue();
		if (v instanceof String s) {
			try {
				return Integer.parseInt(s.trim());
			} catch (Exception ignore) {

			}
		}
		return 0;
	}

	public Color getColor(String key) {
		Object v = props.get(key);
		if (v instanceof Color c)
			return c;
		if (v instanceof String s) {
			try {
				return Color.decode(s.trim());
			} catch (Exception ignore) {

			}
		}
		return Color.GRAY;
	}

	public void setProp(String key, Object value) {
		props.put(key, value);
	}

	protected AffineTransform buildTransform() {
		double x = getDouble("x"), y = getDouble("y");
		double w = Math.max(0, getDouble("width"));
		double h = Math.max(0, getDouble("height"));
		double angle = Math.toRadians(getDouble("rotation-angle"));
		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		at.rotate(angle, w / 2.0, h / 2.0);
		return at;
	}

	protected Stroke buildStroke() {
		double sw = Math.max(1.0, getDouble("stroke-width"));
		return new BasicStroke((float)sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	}
}
