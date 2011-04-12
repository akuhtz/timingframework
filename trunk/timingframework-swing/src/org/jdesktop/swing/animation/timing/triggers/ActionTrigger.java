package org.jdesktop.swing.animation.timing.triggers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Trigger;

import com.surelogic.ThreadSafe;

/**
 * ActionTrigger handles action events and starts the animator when actions
 * occur. For example, to have {@code anim} start when a button is clicked, one
 * might write the following:
 * 
 * <pre>
 * ActionTrigger trigger = ActionTrigger.addTrigger(button, anim);
 * </pre>
 * 
 * @author Chet Haase
 */
@ThreadSafe
public class ActionTrigger extends Trigger implements ActionListener {

  /**
   * Creates an {@link ActionTrigger} and adds it as a listener to the passed
   * object.
   * 
   * @param object
   *          an object that will be used as an event source for this trigger.
   *          This object must have an {@code addActionListener()} method.
   * @param animator
   *          the animation to start when the event occurs.
   * @return the resulting trigger.
   * @throws IllegalArgumentException
   *           if the passed object has no {@code addActionListener()}.
   */
  public static ActionTrigger addTrigger(Object object, Animator animator) {
    final ActionTrigger trigger = new ActionTrigger(animator);
    try {
      Method addListenerMethod = object.getClass().getMethod("addActionListener", ActionListener.class);
      addListenerMethod.invoke(object, trigger);
    } catch (Exception e) {
      throw new IllegalArgumentException("Problem adding listener" + " to object: " + e);
    }
    return trigger;
  }

  /**
   * Creates an {@link ActionTrigger} that will start the animator upon
   * receiving any ActionEvents. It should be added to any suitable object with
   * an {@code addActionListener()} method.
   * 
   * @param animator
   *          the Animator that start when the event occurs
   */
  public ActionTrigger(Animator animator) {
    super(animator, null, false);
  }

  public void actionPerformed(ActionEvent ae) {
    fire(null);
  }
}
