package org.jdesktop.swing.animation.demos.splineeditor;

import javax.swing.JComponent;

public class AbstractSimulator extends JComponent {

  protected double f_time;

  public AbstractSimulator() {
    f_time = 0.0f;
  }

  public void setTime(double time) {
    f_time = time;
    repaint();
  }

  private static final long serialVersionUID = 1341220883137936222L;
}
