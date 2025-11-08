import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

class EllipseShape extends AbstractShape {
    @Override protected String getDefaultName() { return "Ellipse"; }

    @Override
    public void draw(Graphics2D g2, boolean selected) {
        Shape s = getGeometry();
        g2 = (Graphics2D) g2.create();
        g2.setStroke(buildStroke());
        g2.setColor(getColor("fill-color"));
        g2.fill(s);
        g2.setColor(getColor("stroke-color"));
        g2.draw(s);
        if (selected) drawSelection(g2, s);
        g2.dispose();
    }

    private void drawSelection(Graphics2D g2, Shape s) {
        Stroke old = g2.getStroke();
        float[] dash = {4f, 4f};
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
        g2.setColor(new Color(0, 120, 215));
        g2.draw(s.getBounds2D());
        g2.setStroke(old);
    }

    @Override
    public boolean contains(Point2D p) {
        return getGeometry().contains(p);
    }

    @Override
    public Shape getGeometry() {
        double w = Math.max(0, getDouble("width"));
        double h = Math.max(0, getDouble("height"));
        Ellipse2D ellipse = new Ellipse2D.Double(0, 0, w, h);
        AffineTransform at = buildTransform();
        return at.createTransformedShape(ellipse);
    }
}
