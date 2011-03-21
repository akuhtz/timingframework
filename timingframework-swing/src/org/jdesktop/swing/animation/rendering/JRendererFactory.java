package org.jdesktop.swing.animation.rendering;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.util.concurrent.TimeUnit;

import org.jdesktop.core.animation.rendering.JRenderer;
import org.jdesktop.core.animation.rendering.JRendererTarget;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;

public final class JRendererFactory {

	public static JRenderer<JRendererPanel> getDefaultRenderer(
			JRendererPanel on,
			JRendererTarget<GraphicsConfiguration, Graphics2D> target,
			boolean hasChildren) {
		final JRenderer<JRendererPanel> result;
		if (useActiveRenderer()) {
			result = new JActiveRenderer(on, target, hasChildren);
		} else {
			final TimingSource timingSource = new SwingTimerTimingSource(15,
					TimeUnit.MILLISECONDS);
			result = new JPassiveRenderer(on, target, timingSource);
			timingSource.init();
		}
		AnimatorBuilder.setDefaultTimingSource(result.getTimingSource());
		return result;
	}

	public static final String PROPERTY = "org.jdesktop.renderer.active";

	public static boolean useActiveRenderer() {
		return System.getProperty(PROPERTY) != null;
	}

	private JRendererFactory() {
		// no instances
	}
}
