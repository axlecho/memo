package com.axlecho.memo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class NewItemActivity extends SherlockActivity {

	private Bitmap result;
	private ImageView imageView;
	private Canvas canvas;
	private int old_x;
	private int old_y;

	private Button btnSelectColor;
	private View popupColorView;
	private PopupWindow popupColor;
	private Button btnSelectGreen;
	private Button btnSelectBlue;
	private Button btnSelectRed;
	private Button btnSelectYellow;

	private Button btnSelectSize;
	private View popupSizeView;
	private PopupWindow popupSize;
	private SeekBar seekbarSize;
	private TextView penSizeView;

	private Paint paint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_newitem);

		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(2);

		// imageView = new ImageView(this);
		imageView = (ImageView) findViewById(R.id.imgcontext);
		ViewTreeObserver vto2 = imageView.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

				result = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Config.ARGB_8888);
				imageView.setImageBitmap(result);
				canvas = new Canvas(result);
			}
		});
		imageView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent me) {
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					old_x = (int) me.getX();
					old_y = (int) me.getY();
				} else if (me.getAction() == MotionEvent.ACTION_MOVE) {
					canvas.drawLine(old_x, old_y, me.getX(), me.getY(), paint);

					old_x = (int) me.getX();
					old_y = (int) me.getY();
					imageView.invalidate();
				}
				return true;
			}
		});

		popupColorView = getLayoutInflater().inflate(R.layout.menu_selectcolor, null, true);
		popupColor = new PopupWindow(popupColorView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupColor.setBackgroundDrawable(new BitmapDrawable());
		popupColor.setOutsideTouchable(true);
		// popupColor.setAnimationStyle(R.style.PopupAnimation);

		btnSelectGreen = (Button) popupColorView.findViewById(R.id.btn_select_green);
		btnSelectBlue = (Button) popupColorView.findViewById(R.id.btn_select_blue);
		btnSelectRed = (Button) popupColorView.findViewById(R.id.btn_select_red);
		btnSelectYellow = (Button) popupColorView.findViewById(R.id.btn_select_yellow);

		btnSelectGreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				paint.setColor(Color.GREEN);
				popupColor.dismiss();

			}

		});
		btnSelectBlue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				paint.setColor(Color.BLUE);
				popupColor.dismiss();
			}

		});
		btnSelectRed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				paint.setColor(Color.RED);
				popupColor.dismiss();
			}

		});
		btnSelectYellow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				paint.setColor(Color.YELLOW);
				popupColor.dismiss();
			}

		});

		btnSelectColor = (Button) findViewById(R.id.btn_selectcolor);
		btnSelectColor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupColor.isShowing()) {
					popupColor.dismiss();
				} else {
					popupColor.showAsDropDown(v);
				}
			}

		});

		popupSizeView = getLayoutInflater().inflate(R.layout.menu_selectsize, null, true);
		popupSize = new PopupWindow(popupSizeView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupSize.setBackgroundDrawable(new BitmapDrawable());
		popupSize.setOutsideTouchable(true);

		penSizeView = (TextView) popupSizeView.findViewById(R.id.view_penSize);
		seekbarSize = (SeekBar) popupSizeView.findViewById(R.id.seekbar_selectsize);
		seekbarSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
				penSizeView.setText("" + progress);
				paint.setStrokeWidth(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

		});

		btnSelectSize = (Button) findViewById(R.id.btn_selectsize);
		btnSelectSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupSize.isShowing()) {
					popupSize.dismiss();
				} else {
					popupSize.showAsDropDown(v);
				}
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.newitem, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_save:
			try {
				insertRecord();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	private void insertRecord() throws IOException {

		File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Memo/");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		String note = "";
		String picPath = "";
		String voicePath = "";
		picPath = "memo_pic_data" + System.currentTimeMillis();

		File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Memo/" + picPath + ".png");
		f.createNewFile();
		FileOutputStream fOut = new FileOutputStream(f);
		result.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();

		SQLiteDatabase db = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);

		ContentValues record = new ContentValues();
		record.put("note", note);
		record.put("pic_path", Environment.getExternalStorageDirectory().getPath() + "/Memo/" + picPath);
		record.put("voice_path", voicePath);
		long rowid = db.insert("memo_datas", null, record);
		Log.i("axlecho", "插入数据库结果：" + rowid);
		db.close();
	}
}
