package app;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

import javax.swing.*;

import command.UpdatePropertyCommand;
import model.core.History;
import model.core.ShapeStore;
import model.shapes.AbstractShape;
import ui.ButtonPanel;
import ui.DrawingCanvas;
import ui.PropertyEditorPanel;

public class GraphicsEditor extends JFrame {

	private final AppState state = new AppState();
	private final ShapeStore store = new ShapeStore();
	private final History history = new History(200);

	private PropertyEditorPanel propertyPanel;
	private DrawingCanvas canvas;

	public GraphicsEditor() {
		super("Modern Graphics Editor (HashMap-based)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		canvas = new DrawingCanvas(store, state, history);
		propertyPanel = new PropertyEditorPanel(this::applyPropertiesFromPanel);

		var buttonPanel = new ButtonPanel(state, history, canvas, store, this::refreshPropertyPanel);

		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(new JScrollPane(canvas), BorderLayout.CENTER);
		add(propertyPanel, BorderLayout.EAST);

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				refreshPropertyPanel();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				refreshPropertyPanel();
			}
		});

		setSize(1200, 720);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Exiting application...");
				Runtime.getRuntime().halt(0);
			}
		});
	}

	private void applyPropertiesFromPanel(Map<String, Object> newValues) {
		AbstractShape sel = state.getSelection();
		if (sel == null)
			return;

		newValues.forEach((k, v) -> {
			history.run(new UpdatePropertyCommand(sel, k, v));
		});

		canvas.repaint();
		refreshPropertyPanel();
	}

	private void refreshPropertyPanel() {
		propertyPanel.bindShape(state.getSelection());
	}
}
