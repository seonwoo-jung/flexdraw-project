class ReorderCommand implements Command {
	private final ShapeStore store;
	private final AbstractShape shape;
	private final boolean toFront;
	private int oldIndex = -1;

	public ReorderCommand(ShapeStore store, AbstractShape shape, boolean toFront) {
		this.store = store;
		this.shape = shape;
		this.toFront = toFront;
	}

	@Override
	public void execute() {
		oldIndex = store.indexOf(shape);
		if (toFront)
			store.bringToFront(shape);
		else
			store.sendToBack(shape);
	}

	@Override
	public void undo() {
		store.remove(shape);
		store.insertAt(shape, Math.max(0, oldIndex));
	}

	@Override
	public String name() {
		return toFront ? "BringToFront" : "SendToBack";
	}
}
