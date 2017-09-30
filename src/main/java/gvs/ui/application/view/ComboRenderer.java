package gvs.ui.application.view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/**
 * Used for rendering the combo items
 * 
 * @author aegli
 *
 */
class ComboRenderer extends JLabel implements ListCellRenderer {
	private static final long serialVersionUID = 1L;

	public ComboRenderer() {
		setOpaque(true);
		setBorder(new EmptyBorder(1, 1, 1, 1));
	}

	/**
	 * Returns behaviour of selected combo items
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {

		if (isSelected) {
			setForeground(Color.BLUE);
		} else {

			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		if (value != null) {
			if (!((ComboItem) value).isShown) {
				setBackground(list.getBackground());
			}
			setText(((ComboItem) value).getSessionName());
		}
		return this;
	}
}
