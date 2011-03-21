package org.jdesktop.swt.animation.timing.demos.ch15;

import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdesktop.core.animation.timing.AnimatorBuilder;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.triggers.FocusTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.MouseTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.TimingTrigger;
import org.jdesktop.core.animation.timing.triggers.TimingTriggerEvent;
import org.jdesktop.core.animation.timing.triggers.Trigger;
import org.jdesktop.swt.animation.timing.demos.DemoResources;
import org.jdesktop.swt.animation.timing.sources.SWTTimingSource;
import org.jdesktop.swt.animation.timing.triggers.EventTrigger;
import org.jdesktop.swt.animation.timing.triggers.FocusTrigger;
import org.jdesktop.swt.animation.timing.triggers.MouseTrigger;

/**
 * Simple program that demonstrates the use of several different {@link Trigger}
 * implementations available in the Timing Framework.
 * <p>
 * This demo is discussed in Chapter 15 on pages 388&ndash;391 of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008).
 * 
 * @author Chet Haase
 */
public class Triggers extends Composite {

	public static void main(String[] args) {
		final Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		shell.setText("SWT Triggers");

		final TimingSource ts = new SWTTimingSource(15, TimeUnit.MILLISECONDS,
				display);
		AnimatorBuilder.setDefaultTimingSource(ts);
		ts.init();

		createAndShowGUI(shell);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		ts.dispose();
		display.dispose();
	}

	SpherePanel armed, over, action, focus, timing;
	static Button triggerButton;

	/** Creates a new instance of Triggers */
	public Triggers(Composite parent, int style) {
		super(parent, style);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
		final RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.fill = true;
		layout.wrap = false;
		layout.spacing = 0;
		setLayout(layout);
		action = new SpherePanel(this, SWT.DOUBLE_BUFFERED,
				DemoResources.YELLOW_SPHERE, "B-Click");
		focus = new SpherePanel(this, SWT.DOUBLE_BUFFERED,
				DemoResources.BLUE_SPHERE, "Key-Foc");
		armed = new SpherePanel(this, SWT.DOUBLE_BUFFERED,
				DemoResources.RED_SPHERE, "M-Press");
		over = new SpherePanel(this, SWT.DOUBLE_BUFFERED,
				DemoResources.GREEN_SPHERE, "M-Enter");
		timing = new SpherePanel(this, SWT.DOUBLE_BUFFERED,
				DemoResources.GRAY_SPHERE, "1-Stop");

		/*
		 * Add triggers for each sphere, depending on what we want to trigger
		 * them.
		 */
		EventTrigger.addTrigger(triggerButton, SWT.Selection,
				action.getAnimator());
		FocusTrigger.addTrigger(triggerButton, focus.getAnimator(),
				FocusTriggerEvent.IN);
		MouseTrigger.addTrigger(triggerButton, armed.getAnimator(),
				MouseTriggerEvent.PRESS);
		MouseTrigger.addTrigger(triggerButton, over.getAnimator(),
				MouseTriggerEvent.ENTER);
		TimingTrigger.addTrigger(action.getAnimator(), timing.getAnimator(),
				TimingTriggerEvent.STOP);
	}

	private static void createAndShowGUI(Shell shell) {
		shell.setLayout(new GridLayout());
		final Composite buttonPanel = new Composite(shell, SWT.NONE);
		buttonPanel
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonPanel.setLayout(new GridLayout());
		// Note: "Other Button" exists only to provide another component to
		// move focus from/to, in order to show how FocusTrigger works
		final Button otherButton = new Button(buttonPanel, SWT.PUSH);
		otherButton.setText("Other Button");
		otherButton
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		triggerButton = new Button(buttonPanel, SWT.PUSH);
		triggerButton.setText("Trigger");
		triggerButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));

		final Composite c = new Triggers(shell, SWT.NONE);
		c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
}
