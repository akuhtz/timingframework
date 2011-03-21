/**
 * Core classes of the Timing Framework; these classes provide the base
 * functionality that all animations will use.
 * <p>
 * This package provides the fundamental capabilities of the Timing
 * Framework. The core class of the entire framework is {@code Animator},
 * which is responsible for setting up and running animations. Animations
 * are constructed using an {@code AnimatorBuilder}.
 * <p>
 * The other elements of this package are {@code TimingTarget}, which is the
 * interface used by {@code Animator} to report timing events during the
 * animation, and {@code TimingTargetAdapter}, which is a utility class that
 * users may subclass to pick and choose the {@code TimingTarget} events
 * they are interested in receiving.
 * <p>
 * The{@code PropertySetter} class also implements {@code TimingTarget} in a
 * manner that makes it easy to change properties on an object in a
 * JavaBean-like manner. This class can be used in conjunction with the
 * {@code KeyFrames} class to construct sophisticated animations based upon
 * key frames.
 * <p>
 * The {@code TimingSource} class provides a base implementation for timers
 * that may be used with the Timing Framework.
 */
package org.jdesktop.core.animation.timing;