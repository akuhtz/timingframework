package org.jdesktop.core.animation.timing;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;

import com.surelogic.Immutable;
import com.surelogic.Utility;

/**
 * A utility to construct {@link TimingTarget} instances that enables automating
 * the animation of object properties. The returned {@link TimingTarget}
 * instances from the <tt>getTarget</tt> or <tt>getTargetTo</tt> static factory
 * methods should be added as a timing target of an animation. The timing events
 * from the animation will change the specified property over time.
 * <p>
 * For example, here is an animation of the "background" property of some object
 * {@code obj} from blue to red over a period of one second:
 * 
 * <pre>
 * TimingTarget ps = PropertySetter.getTarget(obj, &quot;background&quot;, Color.BLUE, Color.RED);
 * Animator animator = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * More complex animations can be created by passing in multiple values for the
 * property to take on, for example:
 * 
 * <pre>
 * TimingTarget ps = PropertySetter.getTarget(obj, &quot;background&quot;, Color.BLUE, Color.RED, Color.GREEN);
 * Animator animator = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * It is also possible to define more involved and tightly-controlled steps in
 * the animation, including the times between the values and how the values are
 * interpolated by using the constructor that takes a {@link KeyFrames} object.
 * {@link KeyFrames} defines the fractional times at which an object takes on
 * specific values, the values to assume at those times, and the method of
 * interpolation between those values. For example, here is the same animation
 * as above, specified through KeyFrames, where the RED color will be set 10% of
 * the way through the animation (note that we are not setting an Interpolator,
 * so the timing intervals will use the default LinearInterpolator):
 * 
 * <pre>
 * KeyFramesBuilder&lt;Color&gt; builder = new KeyFramesBuilder&lt;Color&gt;(Color.BLUE);
 * builder.setFrame(Color.RED, 0.1);
 * builder.setFrame(Color.GREEN, 1);
 * KeyFrames&lt;Color&gt; frames = builder.build();
 * TimingTarget ps = PropertySetter.getTarget(obj, &quot;background&quot;, frames);
 * Animator animator = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * It is also possible to setup a {@link PropertySetter} to use the current
 * value of the property as the starting value in an animation. This is called a
 * "to" animation. For example, here is an animation of the "foreground"
 * property of some object {@code obj} from it's current value to white and then
 * to blue over a period of 5 seconds:
 * 
 * <pre>
 * TimingTarget ps = PropertySetter.getTargetTo(obj, &quot;foreground&quot;, Color.WHITE, Color.BLUE);
 * Animator animator = new AnimatorBuilder().setDuration(5, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * Note the use of <tt><b>getTargetTo</b></tt>, rather than <tt>getTarget</tt>,
 * to construct an instance for a "to" animation.
 * <p>
 * As with <tt>getTarget</tt>, it is also possible with <tt>getTargetTo</tt> to
 * define more involved and tightly-controlled steps in the animation, including
 * the times between the values and how the values are interpolated by using the
 * constructor that takes a {@link KeyFrames} object. In the case of a "to"
 * animation, the first key value of the {@link KeyFrames} object is
 * ignored&mdash;it is replaced with the current value of the property. For
 * example, here is the same animation as above, specified through a list of
 * {@link KeyFrames.Frame} objects, where the WHITE color will be set 40% of the
 * way through the animation. The final transition to BLUE uses a
 * {@link SplineInterpolator} rather than the default {@link LinearInterpolator}
 * . The RED color that is specified as the starting value is ignored and
 * replaced with the current value of the foreground property.
 * 
 * <pre>
 * KeyFramesBuilder&lt;Color&gt; builder = new KeyFramesBuilder&lt;Color&gt;(Color.RED);
 * builder.setFrame(Color.WHITE, 0.4);
 * builder.setFrame(Color.BLUE, 1, new SplineInterpolator(0.00, 1.00, 1.00, 1.00));
 * KeyFrames&lt;Color&gt; frames = builder.build();
 * TimingTarget ps = PropertySetter.getTargetTo(obj, &quot;foreground&quot;, frames);
 * Animator animator = new AnimatorBuilder().setDuration(5, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * <p>
 * All the methods in this utility return a {@link TimingTargetAdapter} so that
 * a "debug" name can be explicitly set on a returned timing target. In the
 * example code below calling {@link TimingTargetAdapter#getDebugName()
 * ps.getDebugName()} will result in <tt>"BlueToRed"</tt> and calling
 * {@link TimingTargetAdapter#toString() ps.toString()} will result in
 * <tt>"PropertySetterTimingTarget@BlueToRed"</tt>.
 * 
 * <pre>
 * TimingTargetAdapter ps = PropertySetter.getTarget(obj, &quot;background&quot;, Color.BLUE, Color.RED);
 * ps.setDebugName(&quot;BlueToRed&quot;);
 * </pre>
 * 
 * <p>
 * The "debug" name is automatically set to the value passed for the property
 * name. In the example code below the {@link TimingTargetAdapter} type doesn't
 * need to be used but calling {@link TimingTargetAdapter#toString()
 * ps.toString()} will result in
 * <tt>"PropertySetterTimingTarget@background"</tt>.
 * 
 * <pre>
 * TimingTarget ps = PropertySetter.getTarget(obj, &quot;background&quot;, Color.BLUE, Color.RED);
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@Immutable
@Utility
public final class PropertySetter {

  /**
   * Constructs a timing target that changes an object's property over time.
   * 
   * @param <T>
   *          the type of the object's property.
   * @param object
   *          an object.
   * @param propertyName
   *          the name of the the property to manipulate on <tt>object</tt>.
   * @param keyFrames
   *          a key frames instance that define how the property's value changes
   *          over time.
   * @return a timing target.
   */
  public static <T> TimingTargetAdapter getTarget(Object object, String propertyName, KeyFrames<T> keyFrames) {
    return getTargetHelper(object, propertyName, keyFrames, false);
  }

  /**
   * Constructs a timing target that changes an object's property over time.
   * 
   * @param <T>
   *          the type of the object's property.
   * @param object
   *          an object.
   * @param propertyName
   *          the name of the the property to manipulate on <tt>object</tt>.
   * @param values
   *          an ordered list of values that the property should be animated
   *          between. The values will be spaced equally in time and a
   *          {@link LinearInterpolator} will be used.
   * @return a timing target.
   */
  public static <T> TimingTargetAdapter getTarget(Object object, String propertyName, T... values) {
    final KeyFrames<T> keyFrames = new KeyFrames.Builder<T>().addFrames(values).build();
    return getTarget(object, propertyName, keyFrames);
  }

  /**
   * Constructs a timing target that changes an object's property over time.
   * 
   * @param <T>
   *          the type of the object's property.
   * @param object
   *          an object.
   * @param propertyName
   *          the name of the the property to manipulate on <tt>object</tt>.
   * @param interpolator
   *          the interpolator that should be used between the values.
   * @param values
   *          an ordered list of values that the property should be animated
   *          between. The values will be spaced equally in time and the passed
   *          interpolator will be used.
   * @return a timing target.
   */
  public static <T> TimingTargetAdapter getTarget(Object object, String propertyName, Interpolator interpolator, T... values) {
    final KeyFrames<T> keyFrames = new KeyFrames.Builder<T>().setInterpolator(interpolator).addFrames(values).build();
    return getTarget(object, propertyName, keyFrames);
  }

  /**
   * Constructs a timing target that changes an object's property from its
   * current value over time. This is referred to as a "to" animation.
   * 
   * @param <T>
   *          the type of the object's property.
   * @param object
   *          an object.
   * @param propertyName
   *          the name of the the property to manipulate on <tt>object</tt>.
   * @param keyFrames
   *          a key frames instance that define how the property's value changes
   *          over time. The initial value is ignored and replaced with the
   *          current value of the object's property.
   * @return a timing target.
   */
  public static <T> TimingTargetAdapter getTargetTo(Object object, String propertyName, KeyFrames<T> keyFrames) {
    return getTargetHelper(object, propertyName, keyFrames, true);
  }

  /**
   * Constructs a timing target that changes an object's property from its
   * current value over time. This is referred to as a "to" animation.
   * 
   * @param <T>
   *          the type of the object's property.
   * @param object
   *          an object.
   * @param propertyName
   *          the name of the the property to manipulate on <tt>object</tt>.
   * @param values
   *          an ordered list of values that the property should be animated
   *          between. The current value of the object's property is added to
   *          the start of this ordered list. The values will be spaced equally
   *          in time and a {@link LinearInterpolator} will be used.
   * @return a timing target.
   */
  public static <T> TimingTargetAdapter getTargetTo(Object object, String propertyName, T... values) {
    final KeyFrames<T> keyFrames = new KeyFrames.Builder<T>(values[0]).addFrames(values).build();
    return getTargetTo(object, propertyName, keyFrames);
  }

  /**
   * Constructs a timing target that changes an object's property from its
   * current value over time. This is referred to as a "to" animation.
   * 
   * @param <T>
   *          the type of the object's property.
   * @param object
   *          an object.
   * @param propertyName
   *          the name of the the property to manipulate on <tt>object</tt>.
   * @param interpolator
   *          the interpolator that should be used between the values.
   * @param values
   *          an ordered list of values that the property should be animated
   *          between. The current value of the object's property is added to
   *          the start of this ordered list. The values will be spaced equally
   *          in time and the passed interpolator will be used.
   * @return a timing target.
   */
  public static <T> TimingTargetAdapter getTargetTo(Object object, String propertyName, Interpolator interpolator, T... values) {
    final KeyFrames<T> keyFrames = new KeyFrames.Builder<T>(values[0]).setInterpolator(interpolator).addFrames(values).build();
    return getTargetTo(object, propertyName, keyFrames);
  }

  private PropertySetter() {
    throw new AssertionError();
  }

  private static TimingTargetAdapter getTargetHelper(final Object object, final String propertyName, final KeyFrames<?> keyFrames,
      final boolean isToAnimation) {
    if (object == null)
      throw new IllegalArgumentException(I18N.err(1, "object"));
    if (propertyName == null)
      throw new IllegalArgumentException(I18N.err(1, "propertyName"));
    if (keyFrames == null)
      throw new IllegalArgumentException(I18N.err(1, "keyFrames"));
    @SuppressWarnings("unchecked")
    final KeyFrames<Object> objectKeyFrames = (KeyFrames<Object>) keyFrames;
    /*
     * Find the setter method for the property.
     */
    final String firstChar = propertyName.substring(0, 1);
    final String remainder = propertyName.substring(1);
    final String propertySetterName = "set" + firstChar.toUpperCase(Locale.ENGLISH) + remainder;
    Method propertySetter = null;
    try {
      for (Method m : object.getClass().getMethods()) {
        if (m.getName().equals(propertySetterName)) {
          if (m.getParameterTypes().length == 1) {
            propertySetter = m;
            break;
          }
        }
      }
      if (propertySetter == null) {
        throw new IllegalArgumentException(I18N.err(30, propertySetterName, propertyName, object.toString()));
      }
    } catch (SecurityException e) {
      throw new IllegalArgumentException(I18N.err(30, propertySetterName, propertyName, object.toString()), e);
    }
    /*
     * Find the getter method for the property if this is a "to" animations
     */
    if (isToAnimation) {
      final String propertyGetterName = "get" + firstChar.toUpperCase(Locale.ENGLISH) + remainder;
      Method propertyGetter = null;
      try {
        for (Method m : object.getClass().getMethods()) {
          if (m.getName().equals(propertyGetterName)) {
            if (m.getParameterTypes().length == 0) {
              propertyGetter = m;
              break;
            }
          }
        }
        if (propertyGetter == null) {
          throw new IllegalArgumentException(I18N.err(30, propertyGetterName, propertyName, object.toString()));
        }
      } catch (SecurityException e) {
        throw new IllegalArgumentException(I18N.err(30, propertyGetterName, propertyName, object.toString()), e);
      }
      /*
       * Setup "to" animation.
       */
      return new PropertySetterToTimingTarget(objectKeyFrames, object, propertyGetter, propertySetter, propertyName);
    } else {
      /*
       * Setup animation.
       */
      return new PropertySetterTimingTarget(objectKeyFrames, object, propertySetter, propertyName);
    }
  }

  private static class PropertySetterTimingTarget extends TimingTargetAdapter {

    protected final AtomicReference<KeyFrames<Object>> f_keyFrames = new AtomicReference<KeyFrames<Object>>();
    protected final Object f_object;
    protected final Method f_propertySetter;

    public PropertySetterTimingTarget(KeyFrames<Object> keyFrames, Object object, Method propertySetter, String propertyName) {
      f_keyFrames.set(keyFrames);
      f_object = object;
      f_propertySetter = propertySetter;
      setDebugName(propertyName);
    }

    @Override
    public void timingEvent(Animator source, double fraction) {
      try {
        f_propertySetter.invoke(f_object, f_keyFrames.get().getInterpolatedValueAt(fraction));
      } catch (Exception e) {
        throw new IllegalStateException(I18N.err(31, f_propertySetter.getName(), f_object.toString()), e);
      }
    }

    @Override
    public void begin(Animator source) {
      timingEvent(source, 0.0);
    }
  }

  private static final class PropertySetterToTimingTarget extends PropertySetterTimingTarget {

    private final Method f_propertyGetter;

    public PropertySetterToTimingTarget(KeyFrames<Object> keyFrames, Object object, Method propertyGetter, Method propertySetter,
        String propertyName) {
      super(keyFrames, object, propertySetter, propertyName);
      f_propertyGetter = propertyGetter;
    }

    @Override
    public void begin(Animator source) {
      try {
        final Object startValue = f_propertyGetter.invoke(f_object);
        final KeyFrames.Builder<Object> builder = new KeyFrames.Builder<Object>(startValue);
        boolean first = true;
        for (KeyFrames.Frame<Object> frame : f_keyFrames.get()) {
          if (first)
            first = false;
          else
            builder.addFrame(frame);
        }
        f_keyFrames.set(builder.build());
      } catch (Exception e) {
        throw new IllegalStateException(I18N.err(31, f_propertyGetter.getName(), f_object.toString()), e);
      }
      super.begin(source); // set the initial value
    }
  }
}
