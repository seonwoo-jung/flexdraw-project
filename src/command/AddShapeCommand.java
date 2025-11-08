package command;

import model.ShapeStore;
import model.shapes.AbstractShape;

public class AddShapeCommand implements Command {
	private final ShapeStore store;
	private final AbstractShape shape;

	public AddShapeCommand(ShapeStore s, AbstractShape sh) {
		this.store = s;
		this.shape = sh;
	}

	@Override
	public void execute() {
		store.add(shape);
	}

	@Override
	public void undo() {
		store.remove(shape);
	}

	@Override
	public String name() {
		return "AddShape";
	}
}
