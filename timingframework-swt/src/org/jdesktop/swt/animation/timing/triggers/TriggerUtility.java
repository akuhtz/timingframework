package org.jdesktop.swt.animation.timing.triggers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;
import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.Trigger;
import org.jdesktop.core.animation.timing.triggers.AbstractTrigger;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;

import com.surelogic.Immutable;
import com.surelogic.Utility;

/**
 * A utility that creates triggers for SWT applications and Eclipse plug-ins.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
@Utility
public final class TriggerUtility {

  /**
   * Creates a non-auto-reversing timing trigger and adds it as a target to the
   * source animation. For example, one {@link Animator} can be set to start
   * when another ends using this trigger. For example, to have <tt>anim2</tt>
   * start when <tt>anim1</tt> ends, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP);
   * </pre>
   * 
   * @param source
   *          the animation that will be listened to for events to start the
   *          target animation.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link TimingTriggerEvent} on <tt>source</tt> that will cause
   *          <tt>target</tt> to start.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   * 
   * @see TimingTrigger
   */
  public static Trigger addTimingTrigger(Animator source, Animator target, TimingTriggerEvent event) {
    return TimingTrigger.addTrigger(source, target, event);
  }

  /**
   * Creates a timing trigger and adds it as a target to the source animation.
   * For example, one {@link Animator} can be set to start when another ends
   * using this trigger. For example, to have <tt>anim2</tt> start when
   * <tt>anim1</tt> ends and visa versa, have <tt>anim2</tt> stop when
   * <tt>anim1</tt> starts, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP, true);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TimingTrigger.addTrigger(anim1, anim2, TimingTriggerEvent.STOP, true);
   * </pre>
   * 
   * @param source
   *          the animation that will be listened to for events to start the
   *          target animation.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link TimingTriggerEvent} on <tt>source</tt> that will cause
   *          <tt>target</tt> to start.
   * @param autoReverse
   *          {@code true} if the animation should be reversed on opposite
   *          trigger events, {@code false} otherwise.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   * 
   * @see TimingTrigger
   */
  public static Trigger addTimingTrigger(Animator source, Animator target, TimingTriggerEvent event, boolean autoReverse) {
    return TimingTrigger.addTrigger(source, target, event, autoReverse);
  }

  /**
   * Creates an event trigger and adds it as a {@link Listener} to the passed
   * SWT widget. For example, to have {@code anim} start when a button is
   * clicked, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TriggerUtility.addTrigger(button, SWT.Selection, anim);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TriggerUtility.addTrigger(button, SWT.Selection, anim);
   * </pre>
   * 
   * @param widget
   *          an SWT widget that will be used as an event source for this
   *          trigger.
   * @param eventType
   *          the type of SWT event to listen for.
   * @param target
   *          the animation that will start when the SWT event occurs.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   * 
   * @see SWT
   * @see Widget#addListener(int, Listener)
   */
  public static Trigger addEventTrigger(Widget widget, int eventType, Animator target) {
    if (widget == null)
      throw new IllegalArgumentException(I18N.err(1, "widget"));
    if (target == null)
      throw new IllegalArgumentException(I18N.err(1, "target"));
    final EventTriggerHelper trigger = new EventTriggerHelper(widget, eventType, target);
    trigger.init();
    return trigger;
  }

  private static final class EventTriggerHelper extends AbstractTrigger implements Listener {

    private final Widget f_widget;
    private final int f_eventType;

    EventTriggerHelper(Widget widget, int eventType, Animator target) {
      super(target, null, false);
      f_widget = widget;
      f_eventType = eventType;
    }

    public void init() {
      f_widget.addListener(f_eventType, this);
    }

    @Override
    public void disarm() {
      super.disarm();
      f_widget.removeListener(f_eventType, this);
    }

    @Override
    public void handleEvent(Event event) {
      fire(null);
    }
  }

  /**
   * Creates a non-auto-reversing focus trigger and adds it as a
   * {@link FocusListener} to the passed control. For example, to have
   * {@code anim} start when {@code control} receives an IN event, one might
   * write the following:
   * 
   * <pre>
   * Trigger trigger = TriggerUtility.addFocusTrigger(control, anim, FocusTriggerEvent.IN);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TriggerUtility.addFocusTrigger(control, anim, FocusTriggerEvent.IN);
   * </pre>
   * 
   * @param control
   *          the control that will generate focus events for this trigger.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link FocusTriggerEvent} on <tt>control</tt> that will cause
   *          <tt>target</tt> to start.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   */
  public static Trigger addFocusTrigger(Control control, Animator target, FocusTriggerEvent event) {
    return addFocusTrigger(control, target, event, false);
  }

  /**
   * Creates a focus trigger and adds it as a {@link FocusListener} to the
   * passed control. For example, to have {@code anim} start when
   * {@code control} receives an IN event, and reverse {@code anim} when
   * {@code control} receives an OUT event, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TriggerUtility.addFocusTrigger(control, anim, FocusTriggerEvent.IN, true);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TriggerUtility.addFocusTrigger(control, anim, FocusTriggerEvent.IN, true);
   * </pre>
   * 
   * @param control
   *          the control that will generate focus events for this trigger.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link FocusTriggerEvent} on <tt>control</tt> that will cause
   *          <tt>target</tt> to start.
   * @param autoReverse
   *          {@code true} if the animation should be reversed on opposite
   *          trigger events, {@code false} otherwise.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   */
  public static Trigger addFocusTrigger(Control control, Animator target, FocusTriggerEvent event, boolean autoReverse) {
    if (control == null)
      throw new IllegalArgumentException(I18N.err(1, "control"));
    if (target == null)
      throw new IllegalArgumentException(I18N.err(1, "target"));
    if (event == null)
      throw new IllegalArgumentException(I18N.err(1, "event"));
    final FocusTriggerHelper trigger = new FocusTriggerHelper(control, target, event, autoReverse);
    trigger.init();
    return trigger;
  }

  private static final class FocusTriggerHelper extends AbstractTrigger implements FocusListener {

    private final Control f_control;

    FocusTriggerHelper(Control control, Animator target, FocusTriggerEvent event, boolean autoReverse) {
      super(target, event, autoReverse);
      f_control = control;
    }

    public void init() {
      f_control.addFocusListener(this);
    }

    @Override
    public void disarm() {
      super.disarm();
      f_control.removeFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
      fire(FocusTriggerEvent.IN);
    }

    @Override
    public void focusLost(FocusEvent e) {
      fire(FocusTriggerEvent.OUT);
    }
  }

  /**
   * Creates a non-auto-reversing mouse trigger and adds it as a
   * {@link Listener} to the passed control. For example, to have {@code anim}
   * start when {@code control} receives an CLICK event, one might write the
   * following:
   * 
   * <pre>
   * Trigger trigger = TriggerUtility.addMouseTrigger(control, anim, MouseTriggerEvent.CLICK);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TriggerUtility.addMouseTrigger(control, anim, MouseTriggerEvent.CLICK);
   * </pre>
   * 
   * @param control
   *          the control that will generate mouse events for this trigger.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link MouseTriggerEvent} on <tt>control</tt> that will cause
   *          <tt>target</tt> to start.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   */
  public static Trigger addMouseTrigger(Control control, Animator target, MouseTriggerEvent event) {
    return addMouseTrigger(control, target, event, false);
  }

  /**
   * Creates a mouse trigger and adds it as a {@link Listener} to the passed
   * control. For example, to have {@code anim} start when {@code control}
   * receives an ENTER event, and reverse {@code anim} when {@code control}
   * receives an EXIT event, one might write the following:
   * 
   * <pre>
   * Trigger trigger = TriggerUtility.addMouseTrigger(control, anim, MouseTriggerEvent.ENTER, true);
   * </pre>
   * 
   * The returned trigger object can be safely ignored if the code never needs
   * to disarm the trigger.
   * 
   * <pre>
   * TriggerUtility.addMouseTrigger(control, anim, MouseTriggerEvent.ENTER, true);
   * </pre>
   * 
   * @param control
   *          the control that will generate mouse events for this trigger.
   * @param target
   *          the animation that will start when the event occurs.
   * @param event
   *          the {@link MouseTriggerEvent} on <tt>control</tt> that will cause
   *          <tt>target</tt> to start.
   * @param autoReverse
   *          {@code true} if the animation should be reversed on opposite
   *          trigger events, {@code false} otherwise.
   * @return the resulting trigger.
   * 
   * @throws IllegalArgumentException
   *           if any of the parameters is {@code null}.
   */
  public static Trigger addMouseTrigger(Control control, Animator target, MouseTriggerEvent event, boolean autoReverse) {
    if (control == null)
      throw new IllegalArgumentException(I18N.err(1, "control"));
    if (target == null)
      throw new IllegalArgumentException(I18N.err(1, "target"));
    if (event == null)
      throw new IllegalArgumentException(I18N.err(1, "event"));
    final MouseTriggerHelper trigger = new MouseTriggerHelper(control, target, event, autoReverse);
    trigger.init();
    return trigger;
  }

  private static final class MouseTriggerHelper extends AbstractTrigger implements MouseListener, MouseTrackListener {

    private final Control f_control;

    MouseTriggerHelper(Control control, Animator target, MouseTriggerEvent event, boolean autoReverse) {
      super(target, event, autoReverse);
      f_control = control;
    }

    public void init() {
      f_control.addMouseListener(this);
      f_control.addMouseTrackListener(this);
    }

    @Override
    public void disarm() {
      super.disarm();
      f_control.removeMouseListener(this);
      f_control.removeMouseTrackListener(this);
    }

    @Override
    public void mouseEnter(MouseEvent e) {
      fire(MouseTriggerEvent.ENTER);
    }

    @Override
    public void mouseExit(MouseEvent e) {
      fire(MouseTriggerEvent.EXIT);
    }

    @Override
    public void mouseHover(MouseEvent e) {
      // Nothing to do
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {
      // Nothing to do
    }

    @Override
    public void mouseDown(MouseEvent e) {
      fire(MouseTriggerEvent.CLICK);
      fire(MouseTriggerEvent.PRESS);
    }

    @Override
    public void mouseUp(MouseEvent e) {
      fire(MouseTriggerEvent.RELEASE);
    }
  }

  private TriggerUtility() {
    throw new AssertionError();
  }
}
