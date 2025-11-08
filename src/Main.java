import javax.swing.*;

import app.GraphicsEditor;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new GraphicsEditor().setVisible(true));
	}
}
