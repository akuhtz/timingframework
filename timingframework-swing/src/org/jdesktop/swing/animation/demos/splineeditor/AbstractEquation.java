package org.jdesktop.swing.animation.demos.splineeditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractEquation implements Equation {

  protected final List<PropertyChangeListener> listeners;

  protected AbstractEquation() {
    this.listeners = new LinkedList<PropertyChangeListener>();
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    if (listener != null && !listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    if (listener != null) {
      listeners.remove(listener);
    }
  }

  protected void firePropertyChange(String propertyName, double oldValue, double newValue) {
    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
    for (PropertyChangeListener listener : listeners) {
      listener.propertyChange(changeEvent);
    }
  }
}
