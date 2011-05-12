package org.jdesktop.swing.animation.demos.splineeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

public class SplineEditor extends JFrame {

  public static final String PREFIX = "org/jdesktop/swing/animation/demos/splineeditor/";

  public SplineEditor() throws HeadlessException {
    super("Spline Editor");
    add(buildHeader(), BorderLayout.NORTH);
    add(buildControlPanel(), BorderLayout.CENTER);

    pack();
    setLocationRelativeTo(null);
    setResizable(false);

    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  private Component buildHeader() {
    ImageIcon icon = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(PREFIX + "simulator.png"));
    setIconImage(icon.getImage());
    HeaderPanel header = new HeaderPanel(icon, "Timing Framework Spline Editor",
        "Drag control points in the display to change the shape of the spline.",
        "Click the Copy Code button to generate the corresponding Java code.");
    return header;
  }

  private Component buildControlPanel() {
    return new SplineControlPanel();
  }

  public static void main(String[] args) {
    System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

    TimingSource ts = new SwingTimerTimingSource();
    Animator.setDefaultTimingSource(ts);
    ts.init();

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new SplineEditor().setVisible(true);
      }
    });
  }

  private static final long serialVersionUID = -6414936342796708815L;
}
