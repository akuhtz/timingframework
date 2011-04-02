package org.jdesktop.core.animation.timing;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.Animator.Direction;
import org.jdesktop.core.animation.timing.interpolators.LinearInterpolator;
import org.jdesktop.core.animation.timing.interpolators.SplineInterpolator;

import com.surelogic.ThreadSafe;

/**
 * A utility to construct {@link TimingTarget} instances that enables automating
 * animation of object properties. The returned {@link TimingTarget} instances
 * should be added as a target of timing events from an {@link Animator}. These
 * events will be used to change a specified property over time, according to
 * how the {@link PropertySetter} is constructed.
 * <p>
 * For example, here is an animation of the "background" property of some object
 * {@code obj} from blue to red over a period of one second:
 * 
 * <pre>
 * TimingTarget ps = PropertySetter.build(obj, &quot;background&quot;, Color.BLUE, Color.RED);
 * Animator animator = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * More complex animations can be created by passing in multiple values for the
 * property to take on, for example:
 * 
 * <pre>
 * TimingTarget ps = PropertySetter.build(obj, &quot;background&quot;, Color.BLUE, Color.RED, Color.GREEN);
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
 * TimingTarget ps = PropertySetter.build(obj, &quot;background&quot;, frames);
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
 * TimingTarget ps = PropertySetter.buildTo(obj, &quot;foreground&quot;, Color.WHITE, Color.BLUE);
 * Animator animator = new AnimatorBuilder().setDuration(5, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * Note the use of <tt>PropertySetter.<b>buildTo</b></tt>, rather than
 * <tt>PropertySetter.build</tt>, to construct an instance for a "to" animation.
 * <p>
 * As with <tt>PropertySetter.build</tt>, it is also possible with
 * <tt>PropertySetter.buildTo</tt> to define more involved and
 * tightly-controlled steps in the animation, including the times between the
 * values and how the values are interpolated by using the constructor that
 * takes a {@link KeyFrames} object. In the case of a "to" animation, the first
 * key value of the {@link KeyFrames} object is ignored&mdash;it is replaced
 * with the current value of the property. For example, here is the same
 * animation as above, specified through a list of {@link KeyFrames.Frame}
 * objects, where the WHITE color will be set 40% of the way through the
 * animation. The final transition to BLUE uses a {@link SplineInterpolator}
 * rather than the default {@link LinearInterpolator}. The RED color that is
 * specified as the starting value is ignored and replaced with the current
 * value of the foreground property.
 * 
 * <pre>
 * KeyFramesBuilder&lt;Color&gt; builder = new KeyFramesBuilder&lt;Color&gt;(Color.RED);
 * builder.setFrame(Color.WHITE, 0.4);
 * builder.setFrame(Color.BLUE, 1, new SplineInterpolator(0.00, 1.00, 1.00, 1.00));
 * KeyFrames&lt;Color&gt; frames = builder.build();
 * TimingTarget ps = PropertySetter.buildTo(obj, &quot;foreground&quot;, frames);
 * Animator animator = new AnimatorBuilder().setDuration(5, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@ThreadSafe
public class PropertySetter {

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
  public static <T> TimingTarget build(Object object, String propertyName, KeyFrames<T> keyFrames) {
    return new PropertyTimingTarget(object, propertyName, keyFrames, false);
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
  public static <T> TimingTarget build(Object object, String propertyName, T... values) {
    final KeyFrames<T> keyFrames = new KeyFramesBuilder<T>().addFrames(values).build();
    return build(object, propertyName, keyFrames);
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
  public static <T> TimingTarget build(Object object, String propertyName, Interpolator interpolator, T... values) {
    final KeyFrames<T> keyFrames = new KeyFramesBuilder<T>().setInterpolator(interpolator).addFrames(values).build();
    return build(object, propertyName, keyFrames);
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
  public static <T> TimingTarget buildTo(Object object, String propertyName, KeyFrames<T> keyFrames) {
    return new PropertyTimingTarget(object, propertyName, keyFrames, true);
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
  public static <T> TimingTarget buildTo(Object object, String propertyName, T... values) {
    final KeyFrames<T> keyFrames = new KeyFramesBuilder<T>(values[0]).addFrames(values).build();
    return buildTo(object, propertyName, keyFrames);
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
  public static <T> TimingTarget buildTo(Object object, String propertyName, Interpolator interpolator, T... values) {
    final KeyFrames<T> keyFrames = new KeyFramesBuilder<T>(values[0]).setInterpolator(interpolator).addFrames(values).build();
    return buildTo(object, propertyName, keyFrames);
  }

  private PropertySetter() {
    // no instances
  }

  private static class PropertyTimingTarget extends TimingTargetAdapter {
    private final Object f_object;
    private final String f_propertyName;
    private final AtomicReference<KeyFrames<Object>> f_keyFrames = new AtomicReference<KeyFrames<Object>>();
    private final boolean f_isToAnimation;
    private final Method f_propertySetter;
    private final Method f_propertyGetter;

    /**
     * Constructs a property setter instance.
     * <p>
     * This constructor should only be called from one of the static factory
     * methods defined above.
     * 
     * @param object
     *          an object.
     * @param propertyName
     *          the name of the the property to manipulate on <tt>object</tt>.
     * @param keyFrames
     *          a key frames instance that define how the property's value
     *          changes over time. The initial value is ignored and replaced
     *          with the current value of the object's property if this is a
     *          "to" animation.
     * @param toAnimation
     *          {@code true} if this is a "to" animation, {@code true} false
     *          otherwise.
     */
    private PropertyTimingTarget(Object object, String propertyName, KeyFrames<?> keyFrames, boolean toAnimation) {
      if (object == null)
        throw new IllegalArgumentException(I18N.err(1, "object"));
      f_object = object;
      if (propertyName == null)
        throw new IllegalArgumentException(I18N.err(1, "propertyName"));
      f_propertyName = propertyName;
      if (keyFrames == null)
        throw new IllegalArgumentException(I18N.err(1, "keyFrames"));
      @SuppressWarnings("unchecked")
      final KeyFrames<Object> copyKeyFrames = (KeyFrames<Object>) keyFrames;
      f_keyFrames.set(copyKeyFrames);
      f_isToAnimation = toAnimation;
      /*
       * Find the setter method for the property.
       */
      final String firstChar = f_propertyName.substring(0, 1);
      final String remainder = f_propertyName.substring(1);
      final String propertySetterName = "set" + firstChar.toUpperCase(Locale.ENGLISH) + remainder;
      try {
        final PropertyDescriptor pd = new PropertyDescriptor(f_propertyName, f_object.getClass(), null, propertySetterName);
        f_propertySetter = pd.getWriteMethod();
      } catch (IntrospectionException e) {
        throw new IllegalArgumentException(I18N.err(30, propertySetterName, propertyName, object.toString()), e);
      }
      /*
       * Find the getter method for the property if this is a "to" animations
       */
      if (f_isToAnimation) {
        final String propertyGetterName = "get" + firstChar.toUpperCase(Locale.ENGLISH) + remainder;
        try {
          final PropertyDescriptor pd = new PropertyDescriptor(f_propertyName, f_object.getClass(), propertyGetterName, null);
          f_propertyGetter = pd.getReadMethod();
        } catch (IntrospectionException e) {
          throw new IllegalArgumentException(I18N.err(30, propertyGetterName, propertyName, object.toString()), e);
        }
      } else {
        f_propertyGetter = null;
      }
    }

    @Override
    public void begin(Animator source) {
      if (f_isToAnimation) {
        try {
          final Object startValue = f_propertyGetter.invoke(f_object);
          final KeyFramesBuilder<Object> builder = new KeyFramesBuilder<Object>(startValue);
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
      }
    }

    @Override
    public void timingEvent(double fraction, Direction direction, Animator source) {
      try {
        f_propertySetter.invoke(f_object, f_keyFrames.get().getEvaluatedValueAt(fraction));
      } catch (Exception e) {
        throw new IllegalStateException(I18N.err(32, f_propertySetter.getName(), f_object.toString()), e);
      }
    }
  }
}
