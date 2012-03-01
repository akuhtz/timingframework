package org.jdesktop.android.animation.demos;

import java.util.concurrent.TimeUnit;

import org.jdesktop.android.animation.timing.sources.AndroidTimingSource;
import org.jdesktop.core.animation.demos.ScheduledExecutorFactory;
import org.jdesktop.core.animation.demos.TimingSourceFactory;
import org.jdesktop.core.animation.demos.TimingSourceResolutionThread;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.sources.ScheduledExecutorTimingSource;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * An Android application that outputs benchmarks of the available
 * {@link TimingSource} implementations.
 * <p>
 * This is based upon the TimingResolution demo discussed in Chapter 12 on pages
 * 288&ndash;300 (the section on <i>Resolution</i>) of <i>Filthy Rich
 * Clients</i> (Haase and Guy, Addison-Wesley, 2008), however it used the timers
 * via their Timing Framework {@link TimingSource} implementations rather than
 * directly.
 * <p>
 * Two timing source configurations are benchmarked:
 * <ol>
 * <li>{@link AndroidTimingSource} (within Android UI thread) &ndash; As
 * discussed in the book, this timer has the advantage that all calls made from
 * it are within the Android UI thread.</li>
 * <li>{@link ScheduledExecutorTimingSource} (within timer thread) &ndash; This
 * timing source is provided by a <tt>util.concurrent</tt> and calls from it are
 * within its tread context.</li>
 * </ol>
 * 
 * @author Chet Haase
 * @author Tim Halloran
 */
public class TimingSourceResolution extends Activity implements TimingSourceResolutionThread.Depository {

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.timing_source_resolution);

    f_benchmarkOutput = (TextView) findViewById(R.id.TextViewTSR);
    f_scrollOutput = (ScrollView) findViewById(R.id.scrollViewTSR);
  }

  @Override
  protected void onResume() {
    super.onResume();

    f_benchmarkThread = new TimingSourceResolutionThread(this, new ScheduledExecutorFactory(), new AndroidFactory(this));
    f_benchmarkThread.start();
  }

  @Override
  protected void onPause() {
    super.onPause();

    if (f_benchmarkThread != null) {
      f_benchmarkThread.stopSafely();
      f_benchmarkThread = null;
    }
  }

  private ScrollView f_scrollOutput = null;
  private TextView f_benchmarkOutput = null;

  private TimingSourceResolutionThread f_benchmarkThread = null;

  /**
   * This method outputs the string to the GUI {@link #f_benchmarkOutput}.
   * 
   * @param s
   *          a string to append to the output.
   */
  public void out(final String s) {
    final Runnable addToTextArea = new Runnable() {
      @Override
      public void run() {
        final StringBuffer b = new StringBuffer(f_benchmarkOutput.getText());
        b.append(s);
        b.append("\n");
        f_benchmarkOutput.setText(b.toString());
        f_scrollOutput.fullScroll(View.FOCUS_DOWN);
      }
    };
    runOnUiThread(addToTextArea);
    final Runnable scrollToBottom = new Runnable() {
      @Override
      public void run() {
        f_scrollOutput.fullScroll(View.FOCUS_DOWN);
      }
    };
    runOnUiThread(scrollToBottom);
  }

  static class AndroidFactory implements TimingSourceFactory {
    final Activity f_activity;

    AndroidFactory(Activity activity) {
      f_activity = activity;
    }

    @Override
    public TimingSource getTimingSource(int periodMillis) {
      return new AndroidTimingSource(periodMillis, TimeUnit.MILLISECONDS, f_activity);
    }

    @Override
    public String toString() {
      return "AndroidTimingSource (Calls in UI thread)";
    }
  }
}
