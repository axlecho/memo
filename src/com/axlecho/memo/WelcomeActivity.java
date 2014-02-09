package com.axlecho.memo;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);

		final Intent intent2main = new Intent(WelcomeActivity.this, MainActivity.class);
		Timer timer = new Timer();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				startActivity(intent2main);
				finish();
			}
		};
		timer.schedule(task, 1000 * 2);

		SQLiteDatabase mSQLiteDatabase = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);
		mSQLiteDatabase
				.execSQL("create table if not exists memo_datas (recordid integer primary key autoincrement, note varchar(256),pic_path varchar(256),voice_path varchar(256),time datetime default current_timestamp)");

		mSQLiteDatabase.close();
	}
}
