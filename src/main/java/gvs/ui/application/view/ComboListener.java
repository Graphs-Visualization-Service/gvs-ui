package gvs.ui.application.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;

/**
 * Listener for combobox
 * 
 * @author aegli
 *
 */
public class ComboListener implements ActionListener {
  private JComboBox combo;

  private ComboItem currentItem;
  private ApplicationView applicationView;
  private static boolean init = false;

  /**
   * Listener for session combo box in main frame
   * 
   * @param pCombo
   * @param pApplicationView
   */
  public ComboListener(JComboBox pCombo, ApplicationView pApplicationView) {
    this.combo = pCombo;
    this.applicationView = pApplicationView;
  }

  /**
   * Assigns an action to a selected session. Display selected session
   */
  public void actionPerformed(ActionEvent e) {
    currentItem = (ComboItem) combo.getSelectedItem();

    if (!init) {
      init = true;
    } else {
      if ((currentItem != null)) {
        if (!(currentItem.getSessionName().equals("--Choose Session--"))) {
          ((ComboItem) currentItem).getSessionName();
          combo.setSelectedItem(currentItem);
          applicationView
              .comboSet(((ComboItem) currentItem).getSessionController());
        }
      }
    }
  }
}
