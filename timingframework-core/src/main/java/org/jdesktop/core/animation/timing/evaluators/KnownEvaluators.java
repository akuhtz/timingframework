package org.jdesktop.core.animation.timing.evaluators;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdesktop.core.animation.timing.Evaluator;

import com.surelogic.Singleton;

/**
 * Manages a set of known immutable evaluator implementations that the program
 * can find by the type they work with.
 * <p>
 * This maintains a clean separation between the core of the Timing Framework
 * and the Swing and SWT portions by using reflection to attempt to load the
 * non-core evaluators.
 * 
 * @author Tim Halloran
 */
@Singleton
public final class KnownEvaluators {

  private static final KnownEvaluators INSTANCE = new KnownEvaluators();

  public static KnownEvaluators getInstance() {
    return INSTANCE;
  }

  private KnownEvaluators() {
    /*
     * Add implementations in core.
     */
    register(new EvaluatorByte());
    register(new EvaluatorShort());
    register(new EvaluatorInteger());
    register(new EvaluatorLong());
    register(new EvaluatorFloat());
    register(new EvaluatorDouble());

    /*
     * Add known non-core immutable implementations if we can find them. If not,
     * that just means we are missing that code (e.g., Swing or SWT). We do not
     * want to report the missing code as a problem.
     */
    for (final String className : f_nonCoreImmutable) {
      try {
        /*
         * The below is a bit messy. For these types we know nothing about the
         * static typing so we cast to Object. We check the types dynamically in
         * the createFor method below so this is not a problem.
         */
        @SuppressWarnings("unchecked")
        final Class<? extends Evaluator<Object>> evaluatorType = (Class<? extends Evaluator<Object>>) Class.forName(className);
        Evaluator<Object> evaluator = construct(evaluatorType);
        register(evaluator);
      } catch (Exception ignore) {
        // ignore
      }
    }
  }

  private final String[] f_nonCoreImmutable = { "org.jdesktop.swing.animation.timing.evaluators.EvaluatorArc2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorColor",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorCubicCurve2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorDimension2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorEllipse2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorLine2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorPoint2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorQuadCurve2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorRectangle2D",
      "org.jdesktop.swing.animation.timing.evaluators.EvaluatorRoundRectangle2D",
      "org.jdesktop.swt.animation.timing.evaluators.EvaluatorPoint",
      "org.jdesktop.swt.animation.timing.evaluators.EvaluatorRectangle",
      "org.jdesktop.swt.animation.timing.evaluators.EvaluatorRGB" };

  /**
   * A list of known immutable evaluators. The single instance stored in this
   * list is shared by all requesters.
   */
  private final List<Evaluator<?>> f_immutableImplementations = new CopyOnWriteArrayList<Evaluator<?>>();

  /**
   * Registers an immutable evaluator as known. The single instance passed to
   * this method is shared by all requesters.
   * 
   * @param singleton
   *          an immutable evaluator instance.
   */
  public void register(Evaluator<?> singleton) {
    f_immutableImplementations.add(singleton);
  }

  /**
   * Unregisters an immutable evaluator, making it unknown. Has no effect if the
   * passed instance was not previously registered.
   * 
   * @param singleton
   *          an immutable evaluator instance.
   */
  public void unregister(Evaluator<?> singleton) {
    f_immutableImplementations.remove(singleton);
  }

  /**
   * Gets the evaluator for the passed type. This class only registers immutable
   * evaluator implementations so multiple requests for the same type will share
   * the same evaluator.
   * 
   * @param <T>
   *          a type for which an evaluator is requested.
   * @param type
   *          the {@link Class} object for <tt>T</tt>.
   * @return an evaluator for <tt>T</tt>.
   * 
   * @throws IllegalArgumentException
   *           if no evaluator is registered for <tt>T</tt>.
   */
  public <T> Evaluator<T> getEvaluatorFor(Class<T> type) {
    Evaluator<T> result = null;
    /*
     * Look in the registered implementations.
     */
    for (Evaluator<?> e : f_immutableImplementations) {
      if (e.getEvaluatorClass().equals(type)) {
        /*
         * Exact type match, cast to the correct (static) type and return.
         */
        @SuppressWarnings("unchecked")
        final Evaluator<T> exact = (Evaluator<T>) e;
        return exact;
      } else {
        if (e.getEvaluatorClass().isAssignableFrom(type)) {
          /*
           * Assignable type match, cast to the correct (static) type and store.
           * We'll continue to look for an exact type match and only return this
           * result if none is found.
           */
          @SuppressWarnings("unchecked")
          final Evaluator<T> assignable = (Evaluator<T>) e;
          result = assignable;
        }
      }
    }
    if (result != null)
      return result;

    throw new IllegalArgumentException("No Evaluator" + " can be found for type " + type + "; consider using"
        + " different types for your values or supplying a custom" + " Evaluator");
  }

  /**
   * Constructs a new evaluator instance from its class object.
   * 
   * @param <T>
   *          a type for which a new evaluator instance is requested.
   * @param evaluatorType
   *          the {@link Class} object for an implementation of
   *          <tt>Evaluator<T></tt>.
   * @return an evaluator for <tt>T</tt>.
   * 
   * @throws IllegalStateException
   *           if something goes wrong. For example, if the evaluator
   *           implementation does not have a no-argument constructor.
   */
  private <T> Evaluator<T> construct(Class<? extends Evaluator<T>> evaluatorType) {
    Constructor<? extends Evaluator<T>> ctor = null;
    /*
     * The getDeclaredConstructors call is not parameterized correctly.
     */
    @SuppressWarnings("unchecked")
    final Constructor<? extends Evaluator<T>>[] ctors = (Constructor<? extends Evaluator<T>>[]) evaluatorType
        .getDeclaredConstructors();
    for (Constructor<? extends Evaluator<T>> c : ctors) {
      final boolean noArg = c.getParameterTypes().length == 0;
      if (noArg) {
        ctor = c;
      }
    }
    Exception cause = null;
    if (ctor != null) {
      try {
        final Evaluator<T> result = ctor.newInstance();
        return result;
      } catch (Exception e) {
        cause = e;
      }
    }
    String msg = "Unable to construct an instance of " + evaluatorType + ", does this class have a no-argument constructor?";
    if (cause == null)
      throw new IllegalStateException(msg);
    else
      throw new IllegalStateException(msg, cause);
  }
}
