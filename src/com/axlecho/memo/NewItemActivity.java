package com.axlecho.memo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class NewItemActivity extends Activity {

	private Bitmap result;
	private Canvas canvas;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_newitem);
		imageView = (ImageView) findViewById(R.id.view_canvas);
		result = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Config.ARGB_8888);
		canvas = new Canvas(result);

		canvas.drawColor(Color.GREEN);
		imageView.setImageBitmap(result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.newitem, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_save:
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
