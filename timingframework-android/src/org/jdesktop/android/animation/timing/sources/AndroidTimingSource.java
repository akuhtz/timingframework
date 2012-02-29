package org.jdesktop.android.animation.timing.sources;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jdesktop.core.animation.i18n.I18N;
import org.jdesktop.core.animation.timing.TimingSource;

import android.app.Activity;
import android.os.Handler;

import com.surelogic.ThreadSafe;
import com.surelogic.Vouch;

@ThreadSafe
public final class AndroidTimingSource extends TimingSource {

  private final int f_periodMillis;
  private final AtomicBoolean f_running = new AtomicBoolean(true);
  @Vouch("ThreadSafe")
  private final Activity f_activity;

  private class Periodic implements Runnable {

    Periodic(Handler handler) {
      f_handler = handler;
    }

    private final Handler f_handler;

    public void run() {
      if (f_running.get()) {
        getNotifyTickListenersTask().run();
        f_handler.postDelayed(new Periodic(f_handler), f_periodMillis);
      }
    }
  }

  public AndroidTimingSource(long period, TimeUnit unit, final Activity activity) {
    int periodMillis = (int) unit.toMillis(period);
    if (periodMillis < 1)
      periodMillis = 1;
    f_periodMillis = periodMillis;
    if (activity == null)
      throw new IllegalArgumentException(I18N.err(1, "activity"));
    f_activity = activity;

  }

  @Override
  public void init() {
    f_activity.runOnUiThread(new Runnable() {
      public void run() {
        final Handler handler = new Handler();
        handler.postDelayed(new Periodic(handler), f_periodMillis);
      }
    });
  }

  @Override
  public void dispose() {
    f_running.set(false);
  }

  @Override
  protected void runTaskInThreadContext(Runnable task) {
    f_activity.runOnUiThread(task);
  }
}
