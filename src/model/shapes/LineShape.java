package model.shapes;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LineShape extends AbstractShape {
    public LineShape() {
        props.put("x2", 0.0);
        props.put("y2", 0.0);
        // width/height unused; keep for uniformity
        props.put("name", "Line");
    }

    @Override protected String getDefaultName() { return "Line"; }

    @Override
    public void draw(Graphics2D g2, boolean selected) {
        g2 = (Graphics2D) g2.create();
        g2.setStroke(buildStroke());
        g2.setColor(getColor("stroke-color"));
        Line2D ln = getLine();
        g2.draw(ln);
        if (selected) {
            g2.setColor(new Color(0,120,215));
            g2.fill(new Rectangle2D.Double(ln.getX1()-3, ln.getY1()-3, 6, 6));
            g2.fill(new Rectangle2D.Double(ln.getX2()-3, ln.getY2()-3, 6, 6));
        }
        g2.dispose();
    }

    private Line2D getLine() {
        double x = getDouble("x"), y = getDouble("y");
        double x2 = getDouble("x2"), y2 = getDouble("y2");
        return new Line2D.Double(x, y, x2, y2);
    }

    @Override
    public boolean contains(Point2D p) {
        return getLine().ptSegDist(p) <= Math.max(3.0, getDouble("stroke-width"));
    }

    @Override
    public Shape getGeometry() { return getLine(); }
}
