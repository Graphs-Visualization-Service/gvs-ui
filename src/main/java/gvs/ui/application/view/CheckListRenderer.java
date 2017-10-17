package gvs.ui.application.view;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * Used for rendering the checkbox items
 * 
 * @author aegli
 *
 */
public class CheckListRenderer extends JCheckBox
    implements ListCellRenderer<Object> {
  private static final long serialVersionUID = 1L;

  /**
   * Set default back- and foregroundcolor of savedialog items
   *
   */
  public CheckListRenderer() {
    setBackground(UIManager.getColor("List.textBackground"));
    setForeground(UIManager.getColor("List.textForeground"));
  }

  /**
   * Returns save components
   */
  public Component getListCellRendererComponent(JList<?> list, Object value,
      int index, boolean isSelected, boolean hasFocus) {
    setEnabled(list.isEnabled());
    setSelected(((CheckableItem) value).isSelected());
    setFont(list.getFont());
    setText(((CheckableItem) value).getSessionTypeName()
        + ((CheckableItem) value).getSessionName());

    return this;
  }
}
