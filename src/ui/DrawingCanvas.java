package ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

import app.AppState;
import app.Tool;
import command.AddShapeCommand;
import command.RemoveShapeCommand;
import command.ReorderCommand;
import model.core.History;
import model.core.ShapeStore;
import model.shapes.AbstractShape;
import model.shapes.EllipseShape;
import model.shapes.LineShape;
import model.shapes.RectangleShape;

public class DrawingCanvas extends JPanel {

	private final ShapeStore store;
	private final AppState state;
	private final History history;
	private Point2D startPt;
	private AbstractShape drafting;

	public DrawingCanvas(ShapeStore store, AppState state, History history) {
		this.store = store;
		this.state = state;
		this.history = history;
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(960, 640));

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocusInWindow();
				if (state.getTool() == Tool.SELECT) {
					handleSelect(e.getPoint());
				} else {
					startPt = e.getPoint();
					drafting = createShapeForTool(state.getTool());
					if (drafting instanceof LineShape ln) {
						ln.setProp("x", startPt.getX());
						ln.setProp("y", startPt.getY());
						ln.setProp("x2", startPt.getX());
						ln.setProp("y2", startPt.getY());
					} else {
						drafting.setProp("x", startPt.getX());
						drafting.setProp("y", startPt.getY());
						drafting.setProp("width", 0.0);
						drafting.setProp("height", 0.0);
					}
				}
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (drafting != null) {
					updateDrafting(e.getPoint());
					repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (drafting != null) {
					finalizeDrafting(e.getPoint());
					drafting = null;
					startPt = null;
				}
				repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e) && state.getSelection() != null) {
					showContextMenu(e);
				}
			}
		};
		addMouseListener(ma);
		addMouseMotionListener(ma);

		setFocusable(true);
		getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		getActionMap().put("delete", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AbstractShape sel = state.getSelection();
				if (sel != null) {
					history.run(new RemoveShapeCommand(store, sel));
					state.setSelection(null);
					repaint();
				}
			}
		});
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK),
			"undo");
		getActionMap().put("undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				history.undo();
				repaint();
			}
		});
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK),
			"redo");
		getActionMap().put("redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				history.redo();
				repaint();
			}
		});
	}

	private void showContextMenu(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem bringFront = new JMenuItem("Bring to Front");
		JMenuItem sendBack = new JMenuItem("Send to Back");
		bringFront.addActionListener(ae -> {
			if (state.getSelection() != null) {
				history.run(new ReorderCommand(store, state.getSelection(), true));
				repaint();
			}
		});
		sendBack.addActionListener(ae -> {
			if (state.getSelection() != null) {
				history.run(new ReorderCommand(store, state.getSelection(), false));
				repaint();
			}
		});
		menu.add(bringFront);
		menu.add(sendBack);
		menu.show(this, e.getX(), e.getY());
	}

	private void handleSelect(Point p) {
		List<AbstractShape> list = new ArrayList<>(store.all());
		Collections.reverse(list);
		AbstractShape hit = null;

		for (AbstractShape s : list) {
			if (s.contains(p)) {
				hit = s;
				break;
			}
		}

		state.setSelection(hit);
	}

	private AbstractShape createShapeForTool(Tool t) {
		return switch (t) {
			case RECT -> new RectangleShape();
			case ELLIPSE -> new EllipseShape();
			case LINE -> new LineShape();
			default -> null;
		};
	}

	private void updateDrafting(Point current) {
		if (drafting instanceof LineShape ln) {
			ln.setProp("x2", current.getX());
			ln.setProp("y2", current.getY());
		} else {
			double x = Math.min(startPt.getX(), current.getX());
			double y = Math.min(startPt.getY(), current.getY());
			double w = Math.abs(current.getX() - startPt.getX());
			double h = Math.abs(current.getY() - startPt.getY());
			drafting.setProp("x", x);
			drafting.setProp("y", y);
			drafting.setProp("width", w);
			drafting.setProp("height", h);
		}
	}

	private void finalizeDrafting(Point end) {
		if (drafting == null) {
			return;
		}

		Shape geom = drafting.getGeometry();
		if (geom.getBounds2D().getWidth() < 1 && geom.getBounds2D().getHeight() < 1) {
			return;
		}

		history.run(new AddShapeCommand(store, drafting));
		state.setSelection(drafting);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		var g2 = (Graphics2D)g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (AbstractShape s : store.all()) {
			boolean sel = (s == state.getSelection());
			s.draw(g2, sel);
		}

		if (drafting != null) {
			drafting.draw(g2, true);
		}

		g2.dispose();
	}
}
