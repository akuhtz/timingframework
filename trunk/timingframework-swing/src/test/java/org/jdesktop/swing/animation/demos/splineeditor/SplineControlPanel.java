package org.jdesktop.swing.animation.demos.splineeditor;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Interpolator;
import org.jdesktop.core.animation.timing.KeyFrames;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;

class SplineControlPanel extends JPanel {

  SplineDisplay display;
  DropSimulator dropSimulator = new DropSimulator();
  BouncerSimulator bounceSimulator = new BouncerSimulator();

  int linesCount = 0;

  JLabel labelControl1;
  JLabel labelControl2;
  Animator controller;

  SplineControlPanel() {
    super(new BorderLayout());

    add(buildEquationDisplay(), BorderLayout.CENTER);
    add(buildDebugControls(), BorderLayout.EAST);
  }

  private Component buildDebugControls() {
    JButton button;
    JPanel debugPanel = new JPanel(new GridBagLayout());

    debugPanel.add(Box.createHorizontalStrut(150), new GridBagConstraints(0, linesCount++, 2, 1, 1.0, 0.0,
        GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    addSeparator(debugPanel, "Control Points");
    labelControl1 = addDebugLabel(debugPanel, "Point 1:", formatPoint(display.getControl1()));
    labelControl2 = addDebugLabel(debugPanel, "Point 2:", formatPoint(display.getControl2()));
    button = addButton(debugPanel, "Copy Code");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        NumberFormat formatter = getNumberFormatter();
        Point2D c1 = display.getControl1();
        Point2D c2 = display.getControl2();

        StringBuilder code = new StringBuilder();
        code.append("SplineInterpolator spline = new SplineInterpolator(");
        code.append(formatter.format(c1.getX())).append(", ");
        code.append(formatter.format(c1.getY())).append(", ");
        code.append(formatter.format(c2.getX())).append(", ");
        code.append(formatter.format(c2.getY())).append(");");

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(code.toString()), null);
      }
    });

    addEmptySpace(debugPanel, 6);
    addSeparator(debugPanel, "Animation");

    button = addButton(debugPanel, "Play Sample");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        startSampleAnimation();
      }
    });

    addEmptySpace(debugPanel, 6);
    addSeparator(debugPanel, "Templates");
    debugPanel.add(createTemplates(), new GridBagConstraints(0, linesCount++, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    addEmptySpace(debugPanel, 6);

    debugPanel.add(Box.createVerticalGlue(), new GridBagConstraints(0, linesCount++, 2, 1, 1.0, 1.0, GridBagConstraints.LINE_START,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.add(new JSeparator(JSeparator.VERTICAL), BorderLayout.WEST);
    wrapper.add(debugPanel);
    wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));

    return wrapper;
  }

  @SuppressWarnings("all")
  private Component createTemplates() {
    DefaultListModel model = new DefaultListModel();
    model.addElement(createTemplate(0.0, 0.0, 1.0, 1.0));
    model.addElement(createTemplate(0.0, 1.0, 0.0, 1.0));
    model.addElement(createTemplate(0.0, 1.0, 1.0, 1.0));
    model.addElement(createTemplate(0.0, 1.0, 1.0, 0.0));
    model.addElement(createTemplate(1.0, 0.0, 0.0, 1.0));
    model.addElement(createTemplate(1.0, 0.0, 1.0, 1.0));
    model.addElement(createTemplate(1.0, 0.0, 1.0, 0.0));

    JList list = new JList(model);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.setCellRenderer(new TemplateCellRenderer());
    list.addListSelectionListener(new TemplateSelectionHandler());

    JScrollPane pane = new JScrollPane(list);
    pane.getViewport().setPreferredSize(new Dimension(98, 97 * 3));
    return pane;
  }

  private JButton addButton(JPanel debugPanel, String label) {
    JButton button;
    debugPanel.add(button = new JButton(label), new GridBagConstraints(0, linesCount++, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.NONE, new Insets(3, 0, 0, 0), 0, 0));
    return button;
  }

  String formatPoint(Point2D p) {
    NumberFormat formatter = getNumberFormatter();
    return "" + formatter.format(p.getX()) + ", " + formatter.format(p.getY());
  }

  private Component buildEquationDisplay() {
    JPanel panel = new JPanel(new BorderLayout());

    display = new SplineDisplay();
    display.addPropertyChangeListener("control1", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        labelControl1.setText(formatPoint(display.getControl1()));
      }
    });
    display.addPropertyChangeListener("control2", new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent evt) {
        labelControl2.setText(formatPoint(display.getControl2()));
      }
    });

    panel.add(display, BorderLayout.NORTH);

    JPanel wrapper = new JPanel(new GridBagLayout());
    wrapper.add(new JSeparator(), new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    wrapper.add(bounceSimulator, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    wrapper.add(dropSimulator, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
    panel.add(wrapper, BorderLayout.CENTER);

    return panel;
  }

  private JLabel addDebugLabel(JPanel panel, String label, String value) {
    JLabel labelComponent = new JLabel(label);
    panel.add(labelComponent, new GridBagConstraints(0, linesCount, 1, 1, 0.5, 0.0, GridBagConstraints.LINE_END,
        GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
    labelComponent = new JLabel(value);
    panel.add(labelComponent, new GridBagConstraints(1, linesCount++, 1, 1, 0.5, 0.0, GridBagConstraints.LINE_START,
        GridBagConstraints.NONE, new Insets(0, 6, 0, 0), 0, 0));
    return labelComponent;
  }

  private void addEmptySpace(JPanel panel, int size) {
    panel.add(Box.createVerticalStrut(size), new GridBagConstraints(0, linesCount++, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
        GridBagConstraints.VERTICAL, new Insets(6, 0, 0, 0), 0, 0));
  }

  private void addSeparator(JPanel panel, String label) {
    JPanel innerPanel = new JPanel(new GridBagLayout());
    innerPanel.add(new JLabel(label), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    innerPanel.add(new JSeparator(), new GridBagConstraints(1, 0, 1, 1, 0.9, 0.0, GridBagConstraints.LINE_START,
        GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 6), 0, 0));
    panel.add(innerPanel, new GridBagConstraints(0, linesCount++, 2, 1, 1.0, 0.0, GridBagConstraints.LINE_START,
        GridBagConstraints.HORIZONTAL, new Insets(6, 6, 6, 0), 0, 0));
  }

  void startSampleAnimation() {
    if (controller != null && controller.isRunning()) {
      controller.stop();
    }

    Point2D control1 = display.getControl1();
    Point2D control2 = display.getControl2();
    Interpolator splines = new SplineInterpolator(control1.getX(), control1.getY(), control2.getX(), control2.getY());
    KeyFrames<Double> frames = new KeyFrames.Builder<Double>(0.0).addFrame(1.0).setInterpolator(splines).build();

    TimingTarget dropModifier = PropertySetter.getTarget(dropSimulator, "time", frames);
    TimingTarget bounceModifier = PropertySetter.getTarget(bounceSimulator, "time", frames);

    controller = new Animator.Builder().setDuration(2, SECONDS).setRepeatBehavior(Animator.RepeatBehavior.REVERSE)
        .addTarget(dropModifier).addTarget(bounceModifier).build();
    controller.start();
  }

  class TemplateSelectionHandler implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      if (e.getValueIsAdjusting()) {
        return;
      }

      JList list = (JList) e.getSource();
      Template template = (Template) list.getSelectedValue();
      if (template != null) {
        if (controller != null && controller.isRunning()) {
          controller.stop();
        }

        controller = new Animator.Builder().setDuration(400, MILLISECONDS).build();
        controller.addTarget(PropertySetter.getTarget(display, "control1", display.getControl1(), template.getControl1()));
        controller.addTarget(PropertySetter.getTarget(display, "control2", display.getControl2(), template.getControl2()));
        controller.start();
      }
    }
  }

  static NumberFormat getNumberFormatter() {
    NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);
    formatter.setMinimumFractionDigits(2);
    formatter.setMaximumFractionDigits(2);
    return formatter;
  }

  private static Template createTemplate(double x1, double y1, double x2, double y2) {
    return new Template(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
  }

  static class TemplateCellRenderer extends DefaultListCellRenderer {
    private boolean isSelected;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Template template = (Template) value;
      this.setBackground(Color.WHITE);
      this.setIcon(new ImageIcon(template.getImage()));
      this.isSelected = isSelected;
      return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (isSelected) {
        g.setColor(new Color(0.0f, 0.0f, 0.7f, 0.1f));
        g.fillRect(0, 0, getWidth(), getHeight());
      }
    }

    private static final long serialVersionUID = -6480082464537000217L;
  }

  private static class Template {
    private Point2D control1;
    private Point2D control2;
    private Image image;

    public Template(Point2D control1, Point2D control2) {
      this.control1 = control1;
      this.control2 = control2;
    }

    public Point2D getControl1() {
      return control1;
    }

    public Point2D getControl2() {
      return control2;
    }

    public Image getImage() {
      if (image == null) {
        NumberFormat formatter = getNumberFormatter();

        String name = "";
        name += formatter.format(control1.getX()) + '-' + formatter.format(control1.getY());
        name += '-';
        name += formatter.format(control2.getX()) + '-' + formatter.format(control2.getY());

        try {
          image = ImageIO.read(Thread.currentThread().getContextClassLoader()
              .getResourceAsStream(SplineEditor.PREFIX + name + ".png"));
        } catch (IOException e) {
        }
      }

      return image;
    }
  }

  private static final long serialVersionUID = 8501072000818916950L;
}
