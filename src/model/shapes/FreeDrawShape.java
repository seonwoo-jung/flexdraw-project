
package model.shapes;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class FreeDrawShape extends AbstractShape {

	private final List<Point2D.Double> points = new ArrayList<>();

	public FreeDrawShape() {
		props.remove("width");
		props.remove("height");
		props.remove("fill-color");
		props.remove("rotation-angle");

		props.put("name", "FreeDrawShape");
	}

	@Override
	protected String getDefaultName() {
		return "FreeDrawShape";
	}

	public void addPoint(Point2D p) {
		addPoint(p.getX(), p.getY());
	}

	public void addPoint(double x, double y) {
		if (!points.isEmpty()) {
			Point2D.Double last = points.get(points.size() - 1);
			if (last.distance(x, y) < 0.5) {
				return;
			}
		}

		points.add(new Point2D.Double(x, y));
		updateBounds();
	}

	public int getPointCount() {
		return points.size();
	}

	private void updateBounds() {
		if (points.isEmpty()) {
			setProp("x", 0.0);
			setProp("y", 0.0);
			return;
		}

		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;

		for (Point2D.Double pt : points) {
			minX = Math.min(minX, pt.x);
			minY = Math.min(minY, pt.y);
			maxX = Math.max(maxX, pt.x);
			maxY = Math.max(maxY, pt.y);
		}

		setProp("x", minX);
		setProp("y", minY);
	}

	private Path2D buildPath() {
		Path2D.Double path = new Path2D.Double();
		if (points.isEmpty()) {
			return path;
		}

		Point2D.Double first = points.get(0);
		path.moveTo(first.x, first.y);
		for (int i = 1; i < points.size(); i++) {
			Point2D.Double pt = points.get(i);
			path.lineTo(pt.x, pt.y);
		}
		return path;
	}

	@Override
	public void draw(Graphics2D g2, boolean selected) {
		if (points.size() < 2) {
			return;
		}

		g2 = (Graphics2D)g2.create();
		g2.setStroke(buildStroke());
		g2.setColor(getColor("stroke-color"));
		Path2D path = buildPath();
		g2.draw(path);
		g2.dispose();
	}

	@Override
	public boolean contains(Point2D p) {
		if (points.size() < 2) {
			return false;
		}
		Path2D path = buildPath();
		double hitWidth = Math.max(6.0, getDouble("stroke-width") + 2.0);
		Stroke hitStroke = new BasicStroke((float)hitWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Shape hitArea = hitStroke.createStrokedShape(path);
		return hitArea.contains(p);
	}

	@Override
	public Shape getGeometry() {
		return buildPath();
	}
}