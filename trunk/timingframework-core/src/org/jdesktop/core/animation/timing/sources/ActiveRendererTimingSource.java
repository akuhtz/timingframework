package org.jdesktop.core.animation.timing.sources;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.TimingSource;

/**
 * A timing source when an active rendering loop is used. This timer can be
 * called manually at every loop by invoking the {@link #tick()} method. This
 * implementation allows use of {@link Animator}s when using an active rendering
 * loop.
 * <p>
 * The {@link #init()} and {@link #dispose()} methods do nothing in this
 * implementation and do not need to be invoked.
 * 
 * @author Tim Halloran
 */
public final class ActiveRendererTimingSource extends TimingSource {

	public ActiveRendererTimingSource() {
		super(null);
	}

	@Override
	public void init() {
		// nothing to do
	}

	/**
	 * Called from the active rendering loop to "tick" time along.
	 */
	public void tick() {
		notifyTickListeners();
	}

	@Override
	public void dispose() {
		// nothing to do
	}
}
