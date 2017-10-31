package gvs.ui.application.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gvs.business.logic.ApplicationController;
import gvs.business.logic.cluster.ClusterSplitterGVS;
import gvs.business.model.ApplicationModel;
import gvs.interfaces.ISessionController;

/**
 * The application view builds the application frame and most of its components.
 * After each update() from a session controller the view will be rebuilt.
 * 
 * @author aegli
 *
 */
public class ApplicationView extends JFrame implements Observer {

  private static final int DEFAULT_FONT_SIZE = 25;
  private static final int WINDOW_HEIGHT = 800;
  private static final int WINDOW_WIDTH = 1200;
  private static final long serialVersionUID = 1L;
  private Logger appViewLogger = null;
  private BorderLayout gbl = null;
  private JPanel headerPanel = new JPanel();
  private JLabel titelOfSession = new JLabel();
  private JPanel myVisualPanel = null;
  private int initialization = 0;
  private JComboBox<ComboItem> combo = null;
  private SaveDialog saveDialog = null;
  // TODO do we need this? -> class not ported form GVS 1.0
  // private LoggerDialog logDialog = null;
  private Dimension screenSize = null;
  private Font font = null;
  private static JButton deleteButton = null;
  private Border border = null;
  private ApplicationController ac = null;
  private ApplicationModel am = null;

  /**
   * Creates main user interface.
   *
   */
  public ApplicationView() {
    super("Graphs-Visualization-Service GVS");
    this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    // TODO check logger replacement
    // this.appViewLogger =
    // gvs.common.Logger.getInstance().getApplicationViewLogger();
    this.appViewLogger = LoggerFactory.getLogger(ApplicationView.class);
    appViewLogger.info("Build User-Interface, set default view");

    gbl = new BorderLayout();
    font = new Font("Times", Font.ITALIC + Font.BOLD, DEFAULT_FONT_SIZE);

    am = new ApplicationModel();
    // TODO: observer is now deactivated for gvs 1.0
    // am.addObserver(this);
    // ac = ApplicationController.getInstance(am);

    initCombo();
    generateHeaderPanel();

    titelOfSession.setFont(font);
    titelOfSession.setForeground(Color.BLUE);
    titelOfSession.setText("No View available");

    getContentPane().setLayout(gbl);
    getContentPane().add(BorderLayout.NORTH, headerPanel);

    screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();
    setLocation((screenSize.width - frameSize.width) / 2,
        (screenSize.height - frameSize.height) / 2);

    this.setJMenuBar(initMenubar());

    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent arg0) {
        super.windowClosing(arg0);
        appViewLogger.info("Closing application");
        saveSesssions(true);
      }
    });

    deleteButton.setEnabled(false);
  }

  /**
   * Create menubar, items and item actions.
   * 
   * @return JMenuBar
   */
  private JMenuBar initMenubar() {
    JMenuBar bar = new JMenuBar();

    appViewLogger.debug("Create menu bar");
    ApplicationAction saveAction = new ApplicationAction("Save", new ImageIcon(
        // "GVS_ServerIcons\\GVSIcon_save.JPG"), 'S',this);
        "GVS_ServerIcons/GVSIcon_save.JPG"), 'S', this);
    ApplicationAction loadAction = new ApplicationAction("Load",
        new ImageIcon("GVS_ServerIcons\\GVSIcon_load.JPG"), 'L', this);

    ApplicationAction closeAction = new ApplicationAction("Exit",
        new ImageIcon("GVS_ServerIcons\\GVSIcon_exit.JPG"), 'E', this);

    ApplicationAction layoutAction = new ApplicationAction("Layout",
        new ImageIcon("GVS_ServerIcons\\GVSIcon_layout.JPG"), ' ', this);

    ApplicationAction graphLayoutAction = new ApplicationAction("Graph", null,
        ' ', this);

    ApplicationAction treeLayoutAction = new ApplicationAction("Tree", null,
        ' ', this);

    ApplicationAction hardLayoutAction = new ApplicationAction("Random Layout",
        new ImageIcon("GVS_ServerIcons\\GVSIcon_hardlayout.JPG"), ' ', this);

    ApplicationAction softLayoutAction = new ApplicationAction("Stable Layout",
        new ImageIcon("GVS_ServerIcons\\GVSIcon_softlayout.JPG"), ' ', this);

    ApplicationAction loggerAction = new ApplicationAction("Tracer",
        new ImageIcon("GVS_ServerIcons\\GVSIcon_logging.JPG"), 'T', this);

    ApplicationAction versionAction = new ApplicationAction("About",
        new ImageIcon("GVS_ServerIcons\\GVSIcon_about.JPG"), ' ', this);

    JMenu formatMenu = new JMenu("File");
    formatMenu.add(saveAction);
    formatMenu.add(loadAction);
    formatMenu.addSeparator();
    formatMenu.add(closeAction);
    bar.add(formatMenu);

    JMenu optionMenu = new JMenu("Options");
    JMenu layoutOptions = new JMenu(layoutAction);
    optionMenu.add(layoutOptions);
    JMenu graphLayoutOptions = new JMenu(graphLayoutAction);
    layoutOptions.add(graphLayoutOptions);
    JMenu treeLayoutOptions = new JMenu(treeLayoutAction);
    layoutOptions.add(treeLayoutOptions);
    optionMenu.add(loggerAction);

    ButtonGroup group = new ButtonGroup();
    JRadioButtonMenuItem radioHardLayout = new JRadioButtonMenuItem(
        hardLayoutAction);
    radioHardLayout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutOption(false);
      }
    });
    JRadioButtonMenuItem radioSoftLayout = new JRadioButtonMenuItem(
        softLayoutAction);
    radioSoftLayout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        layoutOption(true);
      }
    });

    group.add(radioHardLayout);
    group.add(radioSoftLayout);

    graphLayoutOptions.add(radioHardLayout);
    graphLayoutOptions.add(radioSoftLayout);
    radioHardLayout.setSelected(true);

    JRadioButtonMenuItem clutterSplitterLayout = new JRadioButtonMenuItem(
        treeLayoutAction);
    clutterSplitterLayout.setText("Cluster-Splitting");
    clutterSplitterLayout.setSelected(ClusterSplitterGVS.isEnabled());

    clutterSplitterLayout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ClusterSplitterGVS.setEnabled(!ClusterSplitterGVS.isEnabled());
      }
    });
    treeLayoutOptions.add(clutterSplitterLayout);

    JMenu about = new JMenu("?");
    about.add(versionAction);

    bar.add(optionMenu);
    bar.add(about);
    return bar;
  }

  /**
   * Updates view after choosing another session or after newly arrived updates
   * from layout engine.
   * 
   * @param s
   *          session controller
   */
  private void setApplicationView(ISessionController s) {
    appViewLogger.info("New view update arrived");
    if (initialization == 0) {
      myVisualPanel = s.getControlPanel();
      initialization++;
    } else {
      remove(myVisualPanel);
      myVisualPanel = s.getControlPanel();
    }

    appViewLogger.debug("Update header panel and combobox");
    titelOfSession.setFont(font);
    titelOfSession.setForeground(Color.BLUE);
    titelOfSession.setText(s.getSessionName());

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        getContentPane().add(BorderLayout.CENTER, myVisualPanel);
        getContentPane().add(BorderLayout.NORTH, headerPanel);
        validate();
        repaint();
      }
    });

  }

  /**
   * Initialze combobox.
   */
  private void initCombo() {
    appViewLogger.debug("Create combobox");
    combo = new JComboBox<>();
    combo.setRenderer(new ComboRenderer());
    combo.addActionListener(new ComboListener(combo, this));
  }

  /**
   * Update combobox in order of new session.
   * 
   * @param s
   *          session controller
   */
  private void updateCombo(ISessionController s) {
    appViewLogger.debug("Update combobox");
    ISessionController[] comboTitel = getSessiontoSave();
    combo.removeAllItems();

    for (int i = 0; i < comboTitel.length; i++) {
      combo.addItem(new ComboItem((comboTitel[i])));
      if (comboTitel[i].getSessionId() == s.getSessionId()) {
        combo.setSelectedIndex(i);
      }
    }

    if (comboTitel.length > 0) {
      deleteButton.setEnabled(true);
    } else {
      combo.addItem(new ComboItem());
      deleteButton.setEnabled(false);
    }

    for (int i = 0; i < comboTitel.length; i++) {
      if (comboTitel[i].getSessionId() == s.getSessionId()) {
        combo.setSelectedIndex(i);
      }
    }

  }

  /**
   * Generates information panel on top of user interface.
   */
  private void generateHeaderPanel() {
    appViewLogger.debug("Create header panel");
    headerPanel.setLayout(new GridLayout(1, 2));
    border = BorderFactory.createTitledBorder("Actual View");
    titelOfSession.setBorder(border);

    JPanel leftPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    deleteButton = new JButton(
        new ImageIcon("GVS_ServerIcons\\GVSIcon_delete.JPG"));

    deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteSession();
      }
    });

    centerPanel.add(deleteButton);
    border = BorderFactory.createTitledBorder("Delete");
    centerPanel.setBorder(border);

    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(titelOfSession, BorderLayout.CENTER);
    leftPanel.add(centerPanel, BorderLayout.EAST);
    headerPanel.add(leftPanel);

    border = BorderFactory.createTitledBorder("Available Views");
    combo.setBorder(border);
    headerPanel.add(combo);
  }

  /**
   * Update view while choosing another session or of arriving updates from
   * layout engine.
   * 
   * @param o
   *          observable
   * @param arg
   *          arguments
   */
  public void update(Observable o, Object arg) {
    ISessionController s = ((ISessionController) am.getSession());
    updateCombo(s);
    setApplicationView(s);
  }

  /**
   * Returns references of available sessions. Used by combobox and saveDialog
   * 
   * @return ISessionController sessioncontroller
   */
  public ISessionController[] getSessiontoSave() {
    int counter = 0;

    // not used anymore (swing class) @mwieland
    // Vector<ISessionController> v = ac.getSessionContollers();

    // ISessionController[] sessionNames = new ISessionController[v.size()];
    // Iterator<ISessionController> it = v.iterator();
    // while (it.hasNext()) {
    // sessionNames[counter] = ((ISessionController) it.next());
    // counter++;
    // }
    return null;
  }

  /**
   * Deletes actual displayed session.
   *
   */
  public void deleteSession() {
    appViewLogger.info("Delete actual Session");
    ac.deleteSession(am.getSession());
  }

  /**
   * Sets a choosen seesion from combobox to main panel.
   * 
   * @param cs
   *          session controller
   */
  public void comboSet(ISessionController cs) {
    am.setSession(cs);
  }

  /**
   * Show Trace dialog in oder to change trace level.
   *
   */
  // TODO do we need this? -> class not ported from GVS 1.0
  public void logApplication() {
    appViewLogger.info("Showing Tracedialog");
    // logDialog = new LoggerDialog(this);
    // Dimension window = logDialog.getSize();

    // logDialog.setLocation((screenSize.width - window.width) / 2,
    // (screenSize.height - window.height) / 2);
    // logDialog.setVisible(true);
  }

  /**
   * Show session dialog, check if user wants to exit program.
   * 
   * @param isExitRequested
   *          exist requested
   */
  public void saveSesssions(boolean isExitRequested) {
    appViewLogger.info("Showing Savedialog");
    saveDialog = new SaveDialog(this, null, isExitRequested);
    saveDialog.setAvailableSession(getSessiontoSave());
    Dimension window = saveDialog.getSize();

    saveDialog.setLocation((screenSize.width - window.width) / 2,
        (screenSize.height - window.height) / 2);
    saveDialog.setVisible(true);
  }

  /**
   * Shows About Menu.
   *
   */
  public void loadAboutMenu() {
    JOptionPane.showMessageDialog(this,
        "<html>Graphs-Visualization-Service<p>Version 1.5 <p>"
            + "Produced by Andreas Egli and Michael Koller <p>"
            + "January 2006</html>",
        "About GVS", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Load a session and display.
   *
   */
  public void loadSession() {
    appViewLogger.info("Load-Dialog requested");
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Load GVS Session");
    chooser.setCurrentDirectory(new File(".\\DataStorage"));
    chooser.setFileFilter(new FileFilter() {
      public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".gvs") || f.isDirectory();
      }

      public String getDescription() {
        return "GVS Files";
      }
    });

    int r = chooser.showOpenDialog(this);
    if (r == JFileChooser.APPROVE_OPTION) {
      String gvsname = chooser.getSelectedFile().getPath();
      ac.setRequestedFile(gvsname);
    }
  }

  /**
   * Before exit program, show save dialog.
   *
   */
  public void exitProgram() {
    appViewLogger.info("Exit program, show save dialog");
    saveSesssions(true);
  }

  /**
   * Set new layout option.
   * 
   * @param layoutOption
   *          layout option
   */
  public void layoutOption(boolean layoutOption) {
    appViewLogger.info("Set new layout option");
    ac.setIsSoftLayoutOption(layoutOption);
  }

  /**
   * While layouting, disable delete button.
   * 
   * @param pIsDeleteButtonEnable
   *          enable delete button
   */
  public static void setButton(boolean pIsDeleteButtonEnable) {
    deleteButton.setEnabled(pIsDeleteButtonEnable);
  }

}
