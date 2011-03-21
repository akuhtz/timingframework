package org.jdesktop.swing.animation.timing.demos.ch15;

import java.awt.BorderLayout;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.Trigger;
import org.jdesktop.swing.animation.timing.demos.DemoResources;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;
import org.jdesktop.swing.animation.timing.triggers.ActionTrigger;
import org.jdesktop.swing.animation.timing.triggers.FocusTrigger;
import org.jdesktop.swing.animation.timing.triggers.MouseTrigger;

/**
 * Simple program that demonstrates the use of several different {@link Trigger}
 * implementations available in the Timing Framework.
 * <p>
 * This demo is discussed in Chapter 15 on pages 388&ndash;391 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 */
public class Triggers extends JComponent {

	SpherePanel armed, over, action, focus, timing;
	static JButton triggerButton;

	/** Creates a new instance of Triggers */
	public Triggers() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		action = new SpherePanel(DemoResources.YELLOW_SPHERE, "B-Click");
		focus = new SpherePanel(DemoResources.BLUE_SPHERE, "Key-Foc");
		armed = new SpherePanel(DemoResources.RED_SPHERE, "M-Press");
		over = new SpherePanel(DemoResources.GREEN_SPHERE, "M-Enter");
		timing = new SpherePanel(DemoResources.GRAY_SPHERE, "1-Stop");

		add(action);
		add(focus);
		add(armed);
		add(over);
		add(timing);

		/*
		 * Add triggers for each sphere, depending on what we want to trigger
		 * them.
		 */
		ActionTrigger.addTrigger(triggerButton, action.getAnimator());
		FocusTrigger.addTrigger(triggerButton, focus.getAnimator(),
				FocusTriggerEvent.IN);
		MouseTrigger.addTrigger(triggerButton, armed.getAnimator(),
				MouseTriggerEvent.PRESS);
		MouseTrigger.addTrigger(triggerButton, over.getAnimator(),
				MouseTriggerEvent.ENTER);
		TimingTrigger.addTrigger(action.getAnimator(), timing.getAnimator(),
				TimingTriggerEvent.STOP);
	}

	private static void createAndShowGUI() {
		JFrame f = new JFrame("Triggers");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		// Note: "Other Button" exists only to provide another component to
		// move focus from/to, in order to show how FocusTrigger works
		buttonPanel.add(new JButton("Other Button"), BorderLayout.NORTH);
		triggerButton = new JButton("Trigger");
		buttonPanel.add(triggerButton, BorderLayout.SOUTH);
		f.add(buttonPanel, BorderLayout.NORTH);
		f.add(new Triggers(), BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
	}

	public static void main(String args[]) {
		System.setProperty("swing.defaultlaf",
				"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

		TimingSource ts = new SwingTimerTimingSource(10, TimeUnit.MILLISECONDS);
		AnimatorBuilder.setDefaultTimingSource(ts);
		ts.init();

		Runnable doCreateAndShowGUI = new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		};
		SwingUtilities.invokeLater(doCreateAndShowGUI);
	}

	private static final long serialVersionUID = -907905936402755070L;
}
