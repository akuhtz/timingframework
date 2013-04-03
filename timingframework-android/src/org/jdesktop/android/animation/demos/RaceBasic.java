package org.jdesktop.android.animation.demos;

import java.util.concurrent.TimeUnit;

import org.jdesktop.android.animation.timing.sources.AndroidTimingSource;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.TickListener;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public final class RaceBasic extends Activity implements SurfaceHolder.Callback {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.race_basic);
  }

  SurfaceView f_trackView = null;
  AndroidTimingSource f_timingSource = null;

  @Override
  protected void onResume() {
    super.onResume();

    f_trackView = (SurfaceView) findViewById(R.id.surfaceViewRace);
    if (f_trackView == null)
      throw new IllegalStateException("Can't find track SurfaceView for the demo");

    f_trackView.getHolder().addCallback(this);
    f_timingSource = new AndroidTimingSource(1, TimeUnit.SECONDS, this);
    f_timingSource.init();
  }

  @Override
  protected void onPause() {
    super.onPause();

    f_timingSource.dispose();
    f_timingSource = null;

    f_trackView.getHolder().removeCallback(this);
    f_trackView = null;
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    Log.v(RaceBasic.class.toString(), "surfaceChanged(" + holder + ", " + format + ", " + width + ", " + height + ")");

  }

  public void surfaceCreated(SurfaceHolder holder) {
    f_timingSource.addTickListener(f_tickListener);

  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    f_timingSource.removeTickListener(f_tickListener);

  }

  private final TickListener f_tickListener = new TickListener() {
    public void timingSourceTick(TimingSource source, long nanoTime) {
      final SurfaceHolder sh = f_trackView.getHolder();
      Canvas c = null;
      try {
        c = sh.lockCanvas();
        myDraw(c);
      } finally {
        if (c != null)
          sh.unlockCanvasAndPost(c);
      }
    }
  };

  private boolean f_toggle = true;

  void myDraw(final Canvas c) {
    Rect r = new Rect(0, 0, c.getWidth(), c.getHeight());
    Paint p = new Paint();
    p.setColor(f_toggle ? Color.RED : Color.BLUE);
    c.drawRect(r, p);

    f_toggle = !f_toggle;
  }
}
