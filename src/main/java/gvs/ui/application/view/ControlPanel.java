package gvs.ui.application.view;

import gvs.interfaces.ISessionController;
import gvs.interfaces.ITreeSessionController;
import gvs.interfaces.IVisualizationPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Gives a user several interaction possibilities with current session
 * 
 * @author aegli
 *
 */
public class ControlPanel extends JPanel implements ImageObserver {

  private static final long serialVersionUID = 1L;
  private JTextField sliderTextField = new JTextField(17);
  private ISessionController session = null;
  private GridBagLayout gb = null;
  private JButton previous = null;
  private JButton first = null;
  private JButton next = null;
  private JButton last = null;
  private JButton replay = null;
  private JButton layout = null;
  private JPanel controlPanel = null;
  private JPanel replayPanel = null;
  private JPanel informationPanel = null;
  private JPanel layoutPanel = null;
  private JProgressBar progressBar = null;
  private JSlider slider = null;
  private Border border = null;
  private JLabel treeLabel = new JLabel();
  private JPanel treeState = new JPanel();

  /**
   * Used to provide user input functions to a displayed session. For example
   * show next/previous model, replay or autolayout session
   * 
   * @param session
   */
  public ControlPanel(ISessionController session) {
    this.session = session;
    gb = new GridBagLayout();
    this.setLayout(gb);

    first = new JButton("First");
    previous = new JButton("Previous");
    next = new JButton("Next");
    last = new JButton("Last");
    replay = new JButton("Replay");
    layout = new JButton("Layout");

    addItem(this, gb, getNavigationPanel(), 0, 1, 1, 1, 0.6, 0.0, 5, 5, 5, 5);
    addItem(this, gb, getReplayPanel(), 1, 1, 1, 1, 0.2, 0.0, 5, 5, 5, 5);
    addItem(this, gb, getLayoutPanel(), 2, 1, 1, 1, 0.2, 0.0, 5, 5, 5, 5);
    addItem(this, gb, getProgressPanel(), 0, 2, 3, 1, 1.0, 0.0, 5, 5, 5, 5);

    treeState.add(treeLabel);

  }

  /**
   * Returns navigation panel, used for navigation through session
   */
  private JPanel getNavigationPanel() {
    Border border = BorderFactory.createTitledBorder("Steering");

    controlPanel = new JPanel();
    controlPanel.setLayout(new GridLayout(1, 4));
    controlPanel.add(first);
    controlPanel.add(previous);
    controlPanel.add(next);
    controlPanel.add(last);

    first.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        session.getFirstModel();

      }
    });

    previous.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        session.getPreviousModel();

      }
    });

    next.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        session.getNextModel();

      }
    });

    last.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        session.getLastModel();
      }
    });

    controlPanel.setBorder(border);
    return controlPanel;
  }

  // Progress panel, displays actual view in session queue
  private JPanel getProgressPanel() {
    border = BorderFactory.createTitledBorder("Navigation");

    informationPanel = new JPanel(new BorderLayout());
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);

    progressBar.setBorder(border);
    informationPanel.add(progressBar, BorderLayout.CENTER);
    informationPanel.add(progressBar);

    return informationPanel;
  }

  // Returns replay panel, which includes replay button and splitter
  private JPanel getReplayPanel() {
    border = BorderFactory.createTitledBorder("Replay Function");
    int sliderDefaultSpeed = 500;

    replayPanel = new JPanel(new FlowLayout());
    replayPanel.add(replay);

    slider = new JSlider(JSlider.HORIZONTAL, 0, 2000, sliderDefaultSpeed);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.setMajorTickSpacing(500);
    slider.setMinorTickSpacing(50);
    slider.setMaximum(2000);
    replayPanel.add(slider);
    sliderTextField.setBackground(Color.WHITE);
    sliderTextField
        .setText("Replay Speed: " + sliderDefaultSpeed + " ms p/Pic");
    Font font = new Font("Arial", Font.BOLD, 16);
    sliderTextField.setFont(font);
    sliderTextField.setFocusable(false);
    replayPanel.add(sliderTextField);

    slider.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent event) {
        JSlider source = (JSlider) event.getSource();
        sliderTextField
            .setText("Replay Speed: " + source.getValue() + " ms p/Pic");
        session.speed(source.getValue() + 1);
      }
    });

    replay.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        session.replay();
      }
    });

    layout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        session.autoLayout();
      }
    });

    replayPanel.setBorder(border);

    return replayPanel;
  }

  // Returns layouting panel. Includes autolayouting button
  private JPanel getLayoutPanel() {
    border = BorderFactory.createTitledBorder("Autolayout");

    layoutPanel = new JPanel(new BorderLayout());
    layout = new JButton("Inactive");
    layout.setBackground(Color.GREEN);

    layout.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        session.autoLayout();
      }
    });

    layout.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10,
        new ImageIcon("GVS_ServerIcons\\GVSIcon_hazard.gif")));

    layoutPanel.setBorder(border);
    layoutPanel.add(layout);

    return layoutPanel;
  }

  // Adds components to gridbag layout
  private void addItem(Container pContainer, GridBagLayout pGridBagLayout,
      Component pComponent, int pGridX, int pGridY, int pGridWidth,
      int pGridHeight, double pWeightX, double pWeightY, int pInsUp,
      int pInsLeft, int pInsDown, int pInsRight) {

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
   * Adds visualization panel
   * 
   * @param visualPanel
   */
  public void addVisualizationPanel(IVisualizationPanel visualPanel) {
    addItem(this, gb, (JComponent) visualPanel, 0, 0, 3, 1, 1.0, 1.0, 5, 5, 5,
        5);
  }

  /**
   * Sets button to disabled when first model of session is reached
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setFirst(boolean isEnabled) {
    first.setEnabled(isEnabled);
  }

  /**
   * Sets button to disabled when only one model of session exists
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setPrevious(boolean isEnabled) {
    previous.setEnabled(isEnabled);
  }

  /**
   * Sets button to disabled when only one model of session exists
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setNext(boolean isEnabled) {
    next.setEnabled(isEnabled);
  }

  /**
   * Sets button to disabled when last model of session is reached
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setLast(boolean isEnabled) {
    last.setEnabled(isEnabled);
  }

  /**
   * Disables all buttons on control panel when replay is activated
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setReplay(boolean isEnabled) {
    ApplicationView.setButton(isEnabled);
    replay.setEnabled(isEnabled);
  }

  /**
   * Disables all buttons on control panel when layouting is activated
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setLayout(boolean isEnabled) {
    ApplicationView.setButton(isEnabled);
    layout.setEnabled(isEnabled);
  }

  /**
   * Disables slider on control panel when replay or layouting is activated
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setSlider(boolean isEnabled) {
    slider.setEnabled(isEnabled);
  }

  /**
   * Disables layout button when replay,layouting or tree session is activated
   * 
   * @param isEnabled
   */
  // TODO Refactor -> bad naming
  public void setLayoutState(boolean isLayouting) {
    if (isLayouting == true) {
      layout.setBackground(Color.RED);
      layout.setText("   Active  ");
      treeLabel.setText("Active");
      treeLabel.setBackground(Color.RED);
      treeState.setBackground(Color.RED);
    } else {
      Class[] interfaces = session.getClass().getInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
        if (interfaces[i] == ITreeSessionController.class) {
          layout.setBackground(Color.ORANGE);
          layout.setText("Unavailable");
          treeLabel.setText("Unavailable");
          treeLabel.setBackground(Color.RED);
          treeState.setBackground(Color.RED);
        } else {
          layout.setBackground(Color.GREEN);
          layout.setText(" Inactive ");
          treeLabel.setText("Inactive");
          treeLabel.setBackground(Color.GREEN);
          treeState.setBackground(Color.GREEN);
        }
      }
    }
  }

  /**
   * Sets current progress of displayed models
   * 
   * @param id
   * @param models
   */
  // TODO Refactor -> bad naming
  public void setText(int id, int models) {
    progressBar.setMaximum(models);
    progressBar.setValue(id);
    progressBar.setString("Graph Model " + id + " out of " + models + ".");
  }

  public void setReplayText(String text) {
    replay.setText(text);
  }
}
