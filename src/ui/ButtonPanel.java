package ui;

import java.awt.*;
import java.util.Map;

import javax.swing.*;

import app.AppState;
import app.Tool;
import command.RemoveShapeCommand;
import command.ReorderCommand;
import model.History;
import model.ShapeStore;
import model.shapes.AbstractShape;

public class ButtonPanel extends JPanel {
	public ButtonPanel(AppState state, History history, DrawingCanvas canvas, ShapeStore store, Runnable refreshProps) {
		setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));

		// app.Tool buttons stored in a HashMap
		Map<String, JButton> btns = state.buttons();

		JButton selectBtn = new JButton("Select");
		JButton rectBtn = new JButton("Rect");
		JButton ellBtn = new JButton("Ellipse");
		JButton lineBtn = new JButton("Line");
		JButton undoBtn = new JButton("Undo");
		JButton redoBtn = new JButton("Redo");
		JButton delBtn = new JButton("Delete");
		JButton frontBtn = new JButton("Front");
		JButton backBtn = new JButton("Back");

		btns.put("tool-select", selectBtn);
		btns.put("tool-rect", rectBtn);
		btns.put("tool-ellipse", ellBtn);
		btns.put("tool-line", lineBtn);
		btns.put("cmd-undo", undoBtn);
		btns.put("cmd-redo", redoBtn);
		btns.put("cmd-delete", delBtn);
		btns.put("cmd-front", frontBtn);
		btns.put("cmd-back", backBtn);

		selectBtn.addActionListener(e -> state.setTool(Tool.SELECT));
		rectBtn.addActionListener(e -> state.setTool(Tool.RECT));
		ellBtn.addActionListener(e -> state.setTool(Tool.ELLIPSE));
		lineBtn.addActionListener(e -> state.setTool(Tool.LINE));
		undoBtn.addActionListener(e -> {
			history.undo();
			canvas.repaint();
			refreshProps.run();
		});
		redoBtn.addActionListener(e -> {
			history.redo();
			canvas.repaint();
			refreshProps.run();
		});
		delBtn.addActionListener(e -> {
			AbstractShape sel = state.getSelection();
			if (sel != null) {
				history.run(new RemoveShapeCommand(store, sel));
				state.setSelection(null);
				canvas.repaint();
				refreshProps.run();
			}
		});
		frontBtn.addActionListener(e -> {
			if (state.getSelection() != null) {
				history.run(new ReorderCommand(store, state.getSelection(), true));
				canvas.repaint();
			}
		});
		backBtn.addActionListener(e -> {
			if (state.getSelection() != null) {
				history.run(new ReorderCommand(store, state.getSelection(), false));
				canvas.repaint();
			}
		});

		add(selectBtn);
		add(rectBtn);
		add(ellBtn);
		add(lineBtn);
		add(undoBtn);
		add(redoBtn);
		add(delBtn);
		add(frontBtn);
		add(backBtn);
	}
}
