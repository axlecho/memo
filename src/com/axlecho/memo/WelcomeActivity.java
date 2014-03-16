package com.axlecho.memo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.axlecho.memo.main.MainActivity;
import com.axlecho.memo.unit.SqlManager;

public class WelcomeActivity extends Activity {

	private int showInterFaceTime = 2; // time for loading interface

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);

		// stop for show the loading interface;
		final Intent intent = new Intent(WelcomeActivity.this,
				MainActivity.class);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				startActivity(intent);
				finish();
			}
		}, 1000 * showInterFaceTime);

		SqlManager sqm = new SqlManager(this);
		sqm.init();
	}
}
