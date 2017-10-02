package gvs.ui.application.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

/**
 * Used for radio buttons
 * 
 * @author aegli
 *
 */
class RadioButtonUtils {

  // private constructor so it can't create instances
  private RadioButtonUtils() {
  }

  /**
   * Creates radio button group for each level
   * 
   * @param elements
   * @param title
   * @param actionListener
   * @param selectedRadio
   * @return
   */
  public static Container createRadioButtonGrouping(String[] elements,
      String title, ActionListener actionListener, int selectedRadio) {
    JPanel panel = new JPanel(new GridLayout(0, 5));
    panel.setBackground(Color.WHITE);
    // If title set, create titled border
    if (title != null) {
      Border border = BorderFactory.createTitledBorder(title);
      panel.setBorder(border);
    }
    // Create group
    ButtonGroup group = new ButtonGroup();
    JRadioButton aRadioButton;

    // For each String passed in:
    // Create button, add to panel, and add to group
    for (int i = 0, n = elements.length; i < n; i++) {
      aRadioButton = new JRadioButton(elements[i]);
      aRadioButton.setBackground(Color.WHITE);
      if (i == selectedRadio) {
        aRadioButton.setSelected(true);
      }
      panel.add(aRadioButton);
      group.add(aRadioButton);
      if (actionListener != null) {
        aRadioButton.addActionListener(actionListener);
      }

    }
    return panel;
  }
}
