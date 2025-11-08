package command;

import model.core.ShapeStore;
import model.shapes.AbstractShape;

public class RemoveShapeCommand implements Command {

	private final ShapeStore store;
	private final AbstractShape shape;
	private int oldIndex = -1;

	public RemoveShapeCommand(ShapeStore s, AbstractShape sh) {
		this.store = s;
		this.shape = sh;
	}

	@Override
	public void execute() {
		oldIndex = store.indexOf(shape);
		store.remove(shape);
	}

	@Override
	public void undo() {
		if (oldIndex < 0) {
			oldIndex = 0;
		}
		store.insertAt(shape, oldIndex);
	}

	@Override
	public String name() {
		return "RemoveShape";
	}
}
