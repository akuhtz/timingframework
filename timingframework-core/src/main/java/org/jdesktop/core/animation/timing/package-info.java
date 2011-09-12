/**
 * Core classes of the Timing Framework; these classes provide the base
 * functionality that all animations will use.
 * <p>
 * This package provides the fundamental capabilities of the Timing
 * Framework. The core class of the entire framework is {@code Animator},
 * which is responsible for setting up and running animations. Animations
 * are constructed using an {@code Animator.Builder}.
 * <p>
 * {@code TimingTarget} is the interface used by {@code Animator} to report
 * timing events during the animation, and {@code TimingTargetAdapter},
 * which is a utility class that users may subclass to pick and choose
 * the {@code TimingTarget} events they are interested in receiving.
 * <p>
 * The {@code KeyFrames} class manages a list of key frames to animate
 * values via interpolation between a series of key values at key times.
 * Instances are constructed using a {@code KeyFrames.Builder}.
 * A {@code KeyFramesTimingTarget} simplifies construction of a timing
 * target that uses key frames.
 * <p>
 * The {@code PropertySetter} class provides several static factory methods
 * that provide a {@code TimingTarget} instance that animate properties by
 * changing a property on an object in a JavaBean-like manner. This class
 * can be used in conjunction with the {@code KeyFrames} class to construct
 * sophisticated animations based upon key frames.
 * <p>
 * The {@code Trigger} and {@code TriggerEvent} interfaces specify the
 * interface for triggers, an event-driven approach to starting animations,
 * and trigger events.
 * <p>
 * The {@code TimingSource} class provides a base implementation for timers
 * that may be used with the Timing Framework.
 */
package org.jdesktop.core.animation.timing;