package command;

import model.shapes.AbstractShape;

public class UpdatePropertyCommand implements Command {

	private final AbstractShape shape;
	private final String key;
	private final Object newValue;
	private Object oldValue;

	public UpdatePropertyCommand(AbstractShape s, String key, Object newValue) {
		this.shape = s;
		this.key = key;
		this.newValue = newValue;
	}

	@Override
	public void execute() {
		oldValue = shape.getProps().get(key);
		shape.setProp(key, newValue);
	}

	@Override
	public void undo() {
		shape.setProp(key, oldValue);
	}

	@Override
	public String name() {
		return "UpdateProperty(" + key + ")";
	}
}
