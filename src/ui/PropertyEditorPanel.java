package ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.shapes.AbstractShape;

public class PropertyEditorPanel extends JPanel {

	private AbstractShape boundShape;
	private final List<FieldRow> rows = new ArrayList<>();
	private final JButton updateBtn = new JButton("Update");

	private record FieldRow(String key, JComponent input, Class<?> type) {
	}

	private final Consumer<Map<String, Object>> onApply;

	public PropertyEditorPanel(Consumer<Map<String, Object>> onApply) {
		super(new BorderLayout(8, 8));
		this.onApply = onApply;
		setBorder(new EmptyBorder(8, 8, 8, 8));
		add(new JLabel("Properties"), BorderLayout.NORTH);
		add(new JScrollPane(new JPanel()), BorderLayout.CENTER);
		updateBtn.addActionListener(e -> applyChanges());
		add(updateBtn, BorderLayout.SOUTH);
	}

	public void bindShape(AbstractShape s) {
		this.boundShape = s;
		rebuild();
	}

	private void rebuild() {
		rows.clear();
		removeAll();
		JPanel inner = new JPanel(new GridBagLayout());
		inner.setBorder(new EmptyBorder(8, 8, 8, 8));
		GridBagConstraints gc = new GridBagConstraints();
		gc.insets = new Insets(2, 2, 2, 2);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.WEST;

		if (boundShape == null) {
			inner.add(new JLabel("No selection"), gc);
		} else {
			for (Map.Entry<String, Object> e : boundShape.getProps().entrySet()) {
				String k = e.getKey();
				Object v = e.getValue();
				Class<?> type = (v == null) ? String.class : v.getClass();

				gc.gridx = 0;
				inner.add(new JLabel(k), gc);

				JComponent input;
				if (v instanceof Color c) {
					JButton colorBtn = new JButton(colorToHex(c));
					colorBtn.addActionListener(ae -> {
						Color chosen = JColorChooser.showDialog(this, "Choose Color", c);
						if (chosen != null)
							colorBtn.setText(colorToHex(chosen));
					});
					input = colorBtn;
				} else {
					JTextField tf = new JTextField(String.valueOf(v), 12);
					input = tf;
				}

				gc.gridx = 1;
				inner.add(input, gc);
				rows.add(new FieldRow(k, input, type));
				gc.gridy++;
			}
		}

		add(new JLabel("Properties"), BorderLayout.NORTH);
		add(new JScrollPane(inner), BorderLayout.CENTER);
		add(updateBtn, BorderLayout.SOUTH);
		revalidate();
		repaint();
	}

	private static String colorToHex(Color c) {
		return String.format("#%06X", (c.getRGB() & 0xFFFFFF));
	}

	private static Color parseColor(String s) {
		try {
			if (s.startsWith("#") || s.startsWith("0x") || s.startsWith("0X")) {
				return Color.decode(s);
			}

			if (s.contains(",")) {
				String[] p = s.split(",");
				return new Color(
					Integer.parseInt(p[0].trim()),
					Integer.parseInt(p[1].trim()),
					Integer.parseInt(p[2].trim())
				);
			}

			return Color.decode(s);
		} catch (Exception ex) {
			System.err.println("[WARN] Invalid color string: '" + s + "'. Using default gray.");
			return Color.GRAY;
		}
	}

	private void applyChanges() {
		if (boundShape == null) {
			return;
		}

		Map<String, Object> newValues = new LinkedHashMap<>();

		for (FieldRow r : rows) {
			Object parsed;
			if (r.input instanceof JButton b && r.type == Color.class) {
				parsed = parseColor(b.getText().trim());
			} else if (r.type == Integer.class) {
				try {
					parsed = Integer.parseInt(((JTextField)r.input).getText().trim());
				} catch (Exception ex) {
					parsed = 0;
				}
			} else if (r.type == Double.class || r.type == Float.class || r.type == Long.class) {
				try {
					parsed = Double.parseDouble(((JTextField)r.input).getText().trim());
				} catch (Exception ex) {
					parsed = 0.0;
				}
			} else if (Number.class.isAssignableFrom(r.type)) {
				try {
					parsed = Double.parseDouble(((JTextField)r.input).getText().trim());
				} catch (Exception ex) {
					parsed = 0.0;
				}
			} else if (r.type == Color.class) {
				parsed = parseColor(((JTextField)r.input).getText().trim());
			} else {
				parsed = ((JTextField)r.input).getText();
			}
			newValues.put(r.key, parsed);
		}
		onApply.accept(newValues);
	}
}
