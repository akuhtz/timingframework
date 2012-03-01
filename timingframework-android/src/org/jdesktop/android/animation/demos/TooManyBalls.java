package org.jdesktop.android.animation.demos;

import java.io.IOException;

import org.jdesktop.core.animation.demos.DemoResources;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public final class TooManyBalls extends Activity {
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(new BallView(this));
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onPause() {
    super.onPause();
  }

  private static class BallView extends View {

    Bitmap blueSphere;

    public BallView(Context context) {
      super(context);

      try {
        blueSphere = BitmapFactory.decodeStream(DemoResources.getResource(DemoResources.BLUE_SPHERE).openStream());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
      canvas.drawColor(Color.BLACK);
      Paint circlePaint = new Paint();
      circlePaint.setColor(Color.RED);
      canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 3, circlePaint);
      if (blueSphere != null) {
        canvas.drawBitmap(blueSphere, 10, 10, null);
      }
    }
  }
}
