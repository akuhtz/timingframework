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

    Button button_tsr = (Button) findViewById(R.id.button_tsr);
    button_tsr.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(), TimingSourceResolution.class));
      }
    });
  }
}