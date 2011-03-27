package org.jdesktop.core.animation.timing;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Locale;

import org.jdesktop.core.animation.timing.Animator.Direction;
import org.jdesktop.core.animation.timing.evaluators.KnownEvaluators;

/**
 * A {@link TimingTarget} that enables automating animation of object
 * properties. Instances of this class should be added as a target of timing
 * events from an {@link Animator}. These events will be used to change a
 * specified property over time, according to how the PropertySetter is
 * constructed.
 * <p>
 * For example, here is an animation of the "background" property of some object
 * {@code obj} from blue to red over a period of one second:
 * 
 * <pre>
 * PropertySetter ps = new PropertySetter(obj, &quot;background&quot;, Color.BLUE, Color.RED);
 * Animator anim = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(ps).build();
 * anim.start();
 * </pre>
 * 
 * More complex animations can be created by passing in multiple values for the
 * property to take on, for example:
 * 
 * <pre>
 * PropertySetter ps = new PropertySetter(obj, &quot;background&quot;, Color.BLUE, Color.RED, Color.GREEN);
 * Animator anim = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(ps).build();
 * anim.start();
 * </pre>
 * 
 * It is also possible to define more involved and tightly-controlled steps in
 * the animation, including the times between the values and how the values are
 * interpolated by using the constructor that takes a {@link KeyFrames} object.
 * KeyFrames defines the fractional times at which an object takes on specific
 * values, the values to assume at those times, and the method of interpolation
 * between those values. For example, here is the same animation as above,
 * specified through KeyFrames, where the RED color will be set 10% of the way
 * through the animation (note that we are not setting an Interpolator, so the
 * timing intervals will use the default LinearInterpolator):
 * 
 * <pre>
 * KeyValues vals = KeyValues.create(Color.BLUE, Color.RED, Color.GREEN);
 * KeyTimes times = new KeyTimes(0.0f, .1f, 1.0f);
 * KeyFrames frames = new KeyFrames(vals, times);
 * PropertySetter ps = new PropertySetter(obj, &quot;background&quot;, frames);
 * Animator anim = new AnimatorBuilder().setDuration(1, TimeUnit.SECONDS).addTarget(ps).build();
 * anim.start();
 * </pre>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class PropertySetter extends TimingTargetAdapter {

  private Object f_object;
  private String f_propertyName;
  private KeyFrames<?> f_keyFrames;
  private Method f_propertySetter;
  private Method f_propertyGetter;

  /**
   * Creates a PropertySetter where the values the property takes on during the
   * animation are specified in a {@link KeyFrames} object.
   * 
   * @param object
   *          the object whose property will be animated.
   * @param propertyName
   *          the name of the property to be animated. For any property name
   *          {@code "foo"} there must be an accessible {@code setFoo} method on
   *          the object. If only one value is supplied in {@code keyFrames},
   *          the animation will also need a {@code getFoo} method.
   * @param keyFrames
   *          the fractional times, values, and interpolations to be used in
   *          calculating the values set on the object's property.
   * @throws IllegalArgumentException
   *           if the appropriate set/get methods cannot be found for the
   *           property.
   */
  public PropertySetter(Object object, String propertyName, KeyFrames<?> keyFrames) {
    f_object = object;
    f_propertyName = propertyName;
    f_keyFrames = keyFrames;
    try {
      setupMethodInfo();
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not find set/get methods for the property \"" + propertyName + "\"", e);
    }
  }

  /**
   * Creates a PropertySetter where the values the property takes on during the
   * animation are specified as parameters.
   * 
   * @param object
   *          the object whose property will be animated.
   * @param propertyName
   *          the name of the property to be animated. For any property name
   *          {@code "foo"} there must be an accessible {@code setFoo} method on
   *          the object. If only one value is supplied in {@code keyFrames},
   *          the animation will also need a {@code getFoo} method.
   * @param params
   *          the values that the object will take on during the animation.
   *          Internally, a {@link KeyFrames} object will be constructed that
   *          will use times that split the animation duration evenly. Supplying
   *          only one value implies that this is a <i>to</i> animation whose
   *          initial value will be determined dynamically by a call to the
   *          property's get method when the animation starts.
   * @throws IllegalArgumentException
   *           if the appropriate set/get methods cannot be found for the
   *           property.
   */
  public <T> PropertySetter(Object object, String propertyName, T... params) {
    this(object, propertyName, KeyFrames.build(KeyValues.build(params)));
  }

  /**
   * Creates a PropertySetter where the values the property takes on during the
   * animation are specified as parameters.
   * 
   * @param object
   *          the object whose property will be animated.
   * @param propertyName
   *          the name of the property to be animated. For any property name
   *          {@code "foo"} there must be an accessible {@code setFoo} method on
   *          the object. If only one value is supplied in {@code keyFrames},
   *          the animation will also need a {@code getFoo} method
   * @param evaluator
   *          {@link KeyValues} knows how to calculate intermediate values for
   *          many built-in types, but if you want to supply values in a type
   *          not known by {@link KeyValues} (which uses {@link KnownEvaluators}
   *          to find an {@link Evaluator} implementation), you will need to
   *          supply your own {@link Evaluator} implementation.
   * @param params
   *          the values that the object will take on during the animation.
   *          Internally, a {@link KeyFrames} object will be constructed that
   *          will use times that split the animation duration evenly. Supplying
   *          only one value implies that this is a <i>to</i> animation whose
   *          initial value will be determined dynamically by a call to the
   *          property's get method when the animation starts.
   * @throws IllegalArgumentException
   *           if the appropriate set/get methods cannot be found for the
   *           property.
   */
  public <T> PropertySetter(Object object, String propertyName, Evaluator<T> evaluator, T... params) {
    this(object, propertyName, KeyFrames.build(KeyValues.build(evaluator, params)));
  }

  /**
   * Translates the property name into the appropriate {@link Method} in the
   * object to be modified ({@link #f_object}). This method requires the use of
   * standard JavaBean naming conventions (e.g.,
   * <tt>propertyName<tt> would become
   * <tt>setPropertyName</tt>).
   * 
   * @throws NoSuchMethodException
   *           if there is no set/get method on the object with the appropriate
   *           name.
   * @throws SecurityException
   *           if the application does not have appropriate permissions to
   *           request access to the appropriate {@link Method}.
   */
  private void setupMethodInfo() throws Exception {
    final String firstChar = f_propertyName.substring(0, 1);
    final String remainder = f_propertyName.substring(1);
    final String propertySetterName = "set" + firstChar.toUpperCase(Locale.ENGLISH) + remainder;

    PropertyDescriptor prop = new PropertyDescriptor(f_propertyName, f_object.getClass(), null, propertySetterName);
    f_propertySetter = prop.getWriteMethod();
    if (isToAnimation()) {
      /*
       * Only need the getter for "to" animations
       */
      String propertyGetterName = "get" + firstChar.toUpperCase(Locale.ENGLISH) + remainder;
      prop = new PropertyDescriptor(f_propertyName, f_object.getClass(), propertyGetterName, null);
      f_propertyGetter = prop.getReadMethod();
    }
  }

  /**
   * This method is sets an initial value for the animation if appropriate; this
   * accounts for "to" animations, which need to start from the current value.
   * <p>
   * This method is not intended for use by application code.
   */
  @Override
  public void begin(Animator source) {
    if (isToAnimation()) {
      try {
        setStartValue(f_propertyGetter.invoke(f_object));
      } catch (Exception e) {
        System.out.println("Problem setting start value on object " + f_object + ": " + e);
      }
    }
  }

  /**
   * This invokes the property-setting method (as specified by the
   * {@code propertyName} passed to the constructor) with the appropriate value
   * of the property given the range of values in the {@link KeyValues} object
   * and the fraction of the timing cycle that has elapsed.
   * <p>
   * This method is not intended for use by application code.
   */
  @Override
  public void timingEvent(double fraction, Direction direction, Animator source) {
    setValue(f_object, f_propertySetter, fraction);
  }

  /**
   * Called during begin() if this is a "to" animation, to set the start value
   * of the animation to whatever the current value is.
   */
  @SuppressWarnings("unchecked")
  private void setStartValue(Object object) {
    ((KeyValues<Object>) f_keyFrames.getKeyValues()).setStartValue(object);
  }

  /**
   * Sets the appropriate value on the property given the current fraction
   */
  private void setValue(Object object, Method method, double fraction) {
    try {
      method.invoke(object, f_keyFrames.getValue(fraction));
    } catch (Exception e) {
      throw new IllegalStateException("An unexpected exception occurred when invoking the method " + method + " on " + object, e);
    }
  }

  /**
   * Utility method for determining whether this is a "to" animation (true if
   * the first value is null).
   */
  private boolean isToAnimation() {
    return (f_keyFrames.getKeyValues().isToAnimation());
  }
}
