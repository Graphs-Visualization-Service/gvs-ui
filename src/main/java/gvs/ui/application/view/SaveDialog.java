package gvs.ui.application.view;

import gvs.common.Persistor;
import gvs.interfaces.ISessionController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shows save dialog with the available options
 * 
 * @author aegli
 *
 */
public class SaveDialog extends JDialog {
	private Logger appViewLogger = null;
	private static final long serialVersionUID = 1L;
	private GridBagLayout gbl = null;
	private GridBagLayout selectGbl = null;
	private JList list = null;
	private JDialog dialog = null;

	private Persistor persistor = null;
	private Font font = null;
	private Border loweredBorder = null;
	private boolean isExit = false;

	/**
	 * Creates an instance of saving dialog
	 * 
	 * @param pApplicationView
	 * @param persistor
	 * @param pIsExit
	 */
	public SaveDialog(ApplicationView pApplicationView, Persistor persistor, boolean pIsExit) {
		//TODO: check Logger replacement
		//this.appViewLogger = gvs.common.Logger.getInstance().getApplicationViewLogger();
		this.appViewLogger = LoggerFactory.getLogger(SaveDialog.class);
		appViewLogger.debug("Build Save dialog");
		this.gbl = new GridBagLayout();
		this.setLayout(gbl);
		this.dialog = this;
		this.setTitle("Save GVS Sessions");
		this.setModal(true);
		this.persistor = persistor;
		this.isExit = pIsExit;

	}

	// Create list entries
	private CheckableItem[] createData(ISessionController[] sessionControllers) {
		int countSessions = sessionControllers.length;
		CheckableItem[] items = new CheckableItem[countSessions];
		for (int i = 0; i < countSessions; i++) {
			items[i] = new CheckableItem(sessionControllers[i]);
		}
		return items;
	}

	// Return list
	private void getList() {
		appViewLogger.debug("Prepare list of available session");
		list.setCellRenderer(new CheckListRenderer());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBorder(new EmptyBorder(0, 4, 0, 0));
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = list.locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem) list.getModel().getElementAt(index);
				item.setSelected(!item.isSelected());
				Rectangle rect = list.getCellBounds(index, index);
				list.repaint(rect);
			}
		});
	}

	// Add components to dialog
	private void addItem(Container pContainer, GridBagLayout pGridBagLayout, Component pComponent, int pGridX,
			int pGridY, int pGridWidth, int pGridHeight, double pWeightX, double pWeightY, int pInsUp, int pInsLeft,
			int pInsDown, int pInsRight) {

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(pInsUp, pInsLeft, pInsDown, pInsRight);

		gbc.gridx = pGridX;
		gbc.gridy = pGridY;
		gbc.gridheight = pGridHeight;
		gbc.gridwidth = pGridWidth;
		gbc.weightx = pWeightX;
		gbc.weighty = pWeightY;
		gbc.anchor = GridBagConstraints.CENTER;
		pGridBagLayout.setConstraints(pComponent, gbc);
		pContainer.add(pComponent);
	}

	/**
	 * Adds available session names to be displayed in saving dialog
	 * 
	 * @param sessiontitel
	 */
	public void setAvailableSession(ISessionController[] sessiontitel) {
		appViewLogger.debug("Available save session arrived");
		this.font = new Font("Arial", Font.BOLD, 18);
		this.list = new JList(createData(sessiontitel));

		getList();

		JPanel dialogheader = new JPanel();
		dialogheader.setLayout(new BorderLayout());
		JLabel titledLabel = new JLabel();

		titledLabel.setFont(font);
		titledLabel.setForeground(Color.BLUE);
		titledLabel.setText("     Save Menu");

		JLabel icon = new JLabel(new ImageIcon("GVS_ServerIcons\\GVSIcon_attention.JPG"));
		dialogheader.add(icon, BorderLayout.WEST);
		dialogheader.add(titledLabel, BorderLayout.CENTER);

		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());
		JLabel listLabel = new JLabel();
		font = new Font("Arial", Font.BOLD, 10);
		listLabel.setFont(font);
		listLabel.setForeground(Color.GRAY);
		listLabel.setText("           Type              Name");

		JScrollPane listScroller = new JScrollPane(list);
		loweredBorder = BorderFactory.createLoweredBevelBorder();
		listScroller.setBorder(loweredBorder);

		listPanel.add(listLabel, BorderLayout.NORTH);
		listPanel.add(listScroller, BorderLayout.CENTER);
		listPanel.setBorder(new TitledBorder("Available Choices"));

		JButton selectAllButton = new JButton("Select All");
		selectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListModel model = list.getModel();
				int n = model.getSize();
				for (int i = 0; i < n; i++) {
					CheckableItem item = (CheckableItem) model.getElementAt(i);
					item.setSelected(true);
					list.repaint();
				}
			}
		});

		JButton deselectButton = new JButton("Deselect");
		deselectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ListModel model = list.getModel();
				int n = model.getSize();
				for (int i = 0; i < n; i++) {
					CheckableItem item = (CheckableItem) model.getElementAt(i);
					item.setSelected(false);
					list.repaint();
				}
			}
		});

		selectGbl = new GridBagLayout();
		JPanel selection = new JPanel();
		selection.setLayout(selectGbl);

		addItem(selection, selectGbl, selectAllButton, 0, 0, 1, 1, 0.0, 0.0, 5, 5, 5, 5);
		addItem(selection, selectGbl, deselectButton, 0, 1, 1, 1, 0.0, 0.0, 5, 5, 5, 5);
		addItem(selection, selectGbl, new JLabel(), 0, 2, 1, 1, 0.0, 1.0, 5, 5, 5, 5);

		JButton saveButton = new JButton();
		if (isExit) {
			saveButton.setText("Save and Exit");
		} else {
			saveButton.setText("Save");
		}

		saveButton.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				Vector saveToDisk = new Vector();
				ListModel model = list.getModel();
				int n = model.getSize();

				for (int i = 0; i < n; i++) {
					CheckableItem item = (CheckableItem) model.getElementAt(i);
					if (item.isSelected()) {
						appViewLogger.info("Save selected sessions");
						saveToDisk.add(item.getSessionController());
					}
				}

				if (isExit) {
					appViewLogger.info("Save and exit program");
					persistor.saveToDisk(saveToDisk);
					System.exit(0);
				} else {
					appViewLogger.info("Save and close dialog");
					persistor.saveToDisk(saveToDisk);
					dialog.setVisible(false);
					dialog.dispose();
				}
			}
		});

		JButton cancelButton = new JButton();
		if (isExit) {
			cancelButton.setText("Exit");
		} else {
			cancelButton.setText("Cancel");
		}

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isExit) {
					appViewLogger.info("Exit program without any savings");
					System.exit(0);
				} else {
					appViewLogger.info("Exit dialog without savings");
					dialog.setVisible(false);
					dialog.dispose();
				}
			}
		});

		JPanel south = new JPanel();
		south.setLayout(new GridLayout(1, 2));

		south.add(saveButton);
		south.add(cancelButton);

		listPanel.add(selection, BorderLayout.EAST);

		addItem(this, gbl, dialogheader, 0, 0, 1, 1, 0.0, 0.0, 5, 5, 5, 5);
		addItem(this, gbl, listPanel, 0, 1, 1, 1, 1.0, 1.0, 5, 5, 5, 5);
		addItem(this, gbl, south, 0, 2, 1, 1, 0.0, 0.0, 5, 5, 5, 5);

		this.pack();
	}
}
