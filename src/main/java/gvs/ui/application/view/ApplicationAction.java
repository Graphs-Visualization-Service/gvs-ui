package gvs.ui.application.view;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * Represents most of the application and menu actions
 * 
 * @author aegli
 *
 */
class ApplicationAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private ApplicationView applicationView;

	/**
	 * Assigns an action to each user interface button
	 * 
	 * @param pText
	 * @param pIcon
	 * @param pAccelerator
	 * @param pApplicationView
	 */
	public ApplicationAction(String pText, Icon pIcon, char pAccelerator, ApplicationView pApplicationView) {

		super(pText, pIcon);
		this.applicationView = pApplicationView;
		if (pAccelerator != ' ') {
			putValue(ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(pAccelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		}
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if ((getValue(NAME)) == "Load") {
				applicationView.loadSession();
			}

			if ((getValue(NAME)) == "Exit") {
				applicationView.exitProgram();
			}

			if ((getValue(NAME)) == "Save") {
				applicationView.saveSesssions(false);
			}

			if ((getValue(NAME)) == "Tracer") {
				applicationView.logApplication();
			}

			if ((getValue(NAME)) == "About") {
				applicationView.loadAboutMenu();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
