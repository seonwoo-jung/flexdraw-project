import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;

public class AppState {
	private Tool currentTool = Tool.SELECT;
	private AbstractShape selection;
	private final Map<String, JButton> buttonMap = new LinkedHashMap<>();

	public Tool getTool() {
		return currentTool;
	}

	public void setTool(Tool t) {
		this.currentTool = t;
	}

	public AbstractShape getSelection() {
		return selection;
	}

	public void setSelection(AbstractShape s) {
		this.selection = s;
	}

	public Map<String, JButton> buttons() {
		return buttonMap;
	}

}
