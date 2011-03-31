package org.jdesktop.core.animation.timing;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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
 * takes a series of {@link KeyFrames.Frame} objects. {@link KeyFrames.Frame}
 * defines the fractional times at which an object takes on specific values, the
 * values to assume at those times, and the method of interpolation between
 * those values (it is the type that {@link KeyFrames} uses to represent
 * individual frames). For example, here is the same animation as above,
 * specified through a list of {@link KeyFrames.Frame} objects, where the WHITE
 * color will be set 40% of the way through the animation. The final transition
 * to BLUE uses a {@link SplineInterpolator} rather than the default
 * {@link LinearInterpolator}.
 * 
 * <pre>
 * TimingTarget ps = PropertySetter.buildTo(obj, &quot;background&quot;, new KeyFrames.Frame&lt;Color&gt;(Color.WHITE, 0.4),
 *     new KeyFrames.Frame&lt;Color&gt;(Color.BLUE, 1, new SplineInterpolator(0.00, 1.00, 1.00, 1.00)));
 * Animator animator = new AnimatorBuilder().setDuration(5, TimeUnit.SECONDS).addTarget(ps).build();
 * animator.start();
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
@ThreadSafe
public class PropertySetter extends TimingTargetAdapter {

  /**
   * 
   * @param object
   *          an object.
   * @param propertyName
   *          the name of the the property to manipulate on <tt>object</tt>.
   * @param keyFrames
   *          a list of key frames that define how the property's value changes
   *          over time.
   * @return
   */
  public static <T> TimingTarget build(Object object, String propertyName, KeyFrames<T> keyFrames) {
    return new PropertySetter(object, propertyName, keyFrames, null, null);
  }

  /**
   * 
   * @param <T>
   * @param object
   * @param propertyName
   * @param values
   * @return
   */
  public static <T> TimingTarget build(Object object, String propertyName, T... values) {
    final KeyFrames<T> keyFrames = new KeyFramesBuilder<T>().addFrames(values).build();
    return build(object, propertyName, keyFrames);
  }

  /**
   * 
   * @param <T>
   * @param object
   * @param propertyName
   * @param interpolator
   * @param values
   * @return
   */
  public static <T> TimingTarget build(Object object, String propertyName, Interpolator interpolator, T... values) {
    final KeyFrames<T> keyFrames = new KeyFramesBuilder<T>().setInterpolator(interpolator).addFrames(values).build();
    return build(object, propertyName, keyFrames);
  }

  /**
   * 
   * @param <T>
   * @param object
   * @param propertyName
   * @param interpolator
   * @param values
   * @return
   */
  public static <T> TimingTarget buildTo(Object object, String propertyName, Interpolator interpolator,
      KeyFrames.Frame<T>... values) {
    return new PropertySetter(object, propertyName, null, interpolator, values);
  }

  /**
   * 
   * @param <T>
   * @param object
   * @param propertyName
   * @param interpolator
   * @param values
   * @return
   */
  public static <T> TimingTarget buildTo(Object object, String propertyName, Interpolator interpolator, T... values) {
    final List<KeyFrames.Frame<T>> frames = new ArrayList<KeyFrames.Frame<T>>();
    for (T value : values)
      frames.add(new KeyFrames.Frame<T>(value));
    @SuppressWarnings("unchecked")
    final KeyFrames.Frame<T>[] framesArray = frames.toArray(new KeyFrames.Frame[frames.size()]);
    return buildTo(object, propertyName, interpolator, framesArray);
  }

  /**
   * 
   * @param <T>
   * @param object
   * @param propertyName
   * @param values
   * @return
   */
  public static <T> TimingTarget buildTo(Object object, String propertyName, T... values) {
    return buildTo(object, propertyName, null, values);
  }

  private final Object f_object;
  private final String f_propertyName;
  private final AtomicReference<KeyFrames<Object>> f_keyFrames = new AtomicReference<KeyFrames<Object>>();
  private final KeyFrames.Frame<Object>[] f_toFrames;
  private final Interpolator f_toInterpolator;
  private final Method f_propertySetter;
  private final Method f_propertyGetter;

  /**
   * 
   * @param object
   * @param propertyName
   * @param keyFrames
   * @param toInterpolator
   * @param toFrames
   */
  private PropertySetter(Object object, String propertyName, KeyFrames<?> keyFrames, Interpolator toInterpolator,
      KeyFrames.Frame<?>[] toFrames) {
    if (object == null)
      throw new IllegalArgumentException(I18N.err(1, "object"));
    f_object = object;
    if (propertyName == null)
      throw new IllegalArgumentException(I18N.err(1, "propertyName"));
    f_propertyName = propertyName;

    if ((keyFrames == null && toFrames == null) || (keyFrames != null && toFrames != null))
      throw new IllegalArgumentException(I18N.err(31));
    if (keyFrames != null) {
      @SuppressWarnings("unchecked")
      final KeyFrames<Object> copyKeyFrames = (KeyFrames<Object>) keyFrames;
      f_keyFrames.set(copyKeyFrames);
      f_toInterpolator = null;
      f_toFrames = null;
    } else {
      f_toInterpolator = toInterpolator == null ? LinearInterpolator.getInstance() : toInterpolator;
      @SuppressWarnings("unchecked")
      final KeyFrames.Frame<Object>[] copyToFrames = (KeyFrames.Frame<Object>[]) toFrames;
      f_toFrames = copyToFrames;
    }

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
    final boolean isToAnimation = f_toFrames != null;
    if (isToAnimation) {
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
    final boolean isToAnimation = f_toFrames != null;
    if (isToAnimation) {
      try {
        final Object startValue = f_propertyGetter.invoke(f_object);
        f_keyFrames.set(new KeyFramesBuilder<Object>(startValue).setInterpolator(f_toInterpolator).addFrames(f_toFrames).build());
      } catch (Exception e) {
        throw new IllegalStateException(I18N.err(32, f_propertyGetter.getName(), f_object.toString()), e);
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
