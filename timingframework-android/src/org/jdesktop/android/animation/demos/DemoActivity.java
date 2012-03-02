package org.jdesktop.android.animation.demos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DemoActivity extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    setupButton(R.id.button_tsr, TimingSourceResolution.class);
    setupButton(R.id.button_tmb, TooManyBalls.class);
  }

  private void setupButton(int viewId, final Class<? extends Activity> cls) {
    if (cls == null)
      throw new IllegalArgumentException("The activity class, cls, must be non-null");

    Button button = (Button) findViewById(viewId);
    if (button == null)
      throw new IllegalArgumentException("The view id " + viewId + " does not reference a view");

    button.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), cls));
      }
    });
  }
}