package model.core;

import java.util.ArrayDeque;
import java.util.Deque;

import command.Command;

public class History {
	private final Deque<Command> undoStack = new ArrayDeque<>();
	private final Deque<Command> redoStack = new ArrayDeque<>();
	private final int capacity;

	public History(int capacity) {
		this.capacity = Math.max(10, capacity);
	}

	public void run(Command c) {
		c.execute();
		undoStack.push(c);

		if (undoStack.size() > capacity) {
			undoStack.removeLast();
		}

		redoStack.clear();
	}

	public void undo() {
		if (undoStack.isEmpty()) {
			return;
		}

		Command c = undoStack.pop();
		c.undo();
		redoStack.push(c);
	}

	public void redo() {
		if (redoStack.isEmpty()) {
			return;
		}

		Command c = redoStack.pop();
		c.execute();
		undoStack.push(c);
	}

	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public boolean canRedo() {
		return !redoStack.isEmpty();
	}

}
