package com.axlecho.memo;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class ShowActivity extends SherlockActivity {
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		String pic_path = (String) getIntent().getStringExtra("pic_path");
		imageView = (ImageView) findViewById(R.id.view_image);
		imageView.setImageBitmap(BitmapFactory.decodeFile(pic_path));

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.show, menu);
		return true;
	}
}
