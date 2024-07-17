package org.jdesktop.core.animation.timing;

/**
 * Implements the {@link TimingTarget} interface, providing stubs for all timing target methods. Subclasses may extend
 * this adapter rather than implementing the {@link TimingTarget} interface if they only care about a subset of the
 * events provided. For example, sequencing animations may only require monitoring the {@link TimingTarget#end} method,
 * so subclasses of this adapter may ignore the other methods such as timingEvent.
 * <p>
 * This class provides a useful "debug" name via {@link #setDebugName(String)} and {@link #getDebugName()}. The debug
 * name is also output by {@link #toString()}. This feature is intended to aid debugging.
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class TimingTargetAdapter implements TimingTarget {

    @Override
    public void begin(Animator source) {
        // default is to do nothing
    }

    @Override
    public void end(Animator source) {
        // default is to do nothing
    }

    @Override
    public void repeat(Animator source) {
        // default is to do nothing
    }

    @Override
    public void reverse(Animator source) {
        // default is to do nothing
    }

    @Override
    public void timingEvent(Animator source, double fraction) {
        // default is to do nothing
    }

    volatile String f_debugName = null;

    public final void setDebugName(String name) {
        f_debugName = name;
    }

    public final String getDebugName() {
        return f_debugName;
    }

    @Override
    public String toString() {
        final String debugName = f_debugName;
        final StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append('@');
        b.append(debugName != null ? debugName : Integer.toHexString(hashCode()));
        return b.toString();
    }
}
