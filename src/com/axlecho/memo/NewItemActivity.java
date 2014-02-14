package com.axlecho.memo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
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

	private Button btnAddText;
	private View popupAddTextView;
	private PopupWindow popupAddWindow;
	private EditText editAddTextView;

	private Paint paint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newitem);

		initPaint();
		initImageView();
		initPopupSize();
		initPopupColor();
		initPopupAddText();
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
		case R.id.menu_photo:
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "workupload.jpg"));
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(cameraIntent, Const.CAMERARESULT);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Const.CAMERARESULT) {
			Bitmap camorabitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
					+ "/workupload.jpg");
			if (null != camorabitmap) {
				// 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。
				int scale = reckonThumbnail(camorabitmap.getWidth(), camorabitmap.getHeight(), canvas.getWidth(),
						canvas.getHeight());
				Bitmap b = PicZoom(camorabitmap, camorabitmap.getWidth() / scale, camorabitmap.getHeight() / scale);
				// Rect r = new Rect(0, 0, canvas.getWidth(),
				// canvas.getHeight());
				// canvas.drawBitmap(b, null, r, null);
				canvas.drawBitmap(b, 0, 0, null);
				// imageView.setImageBitmap(b);
			}
		}
	}

	public static int reckonThumbnail(int oldWidth, int oldHeight, int newWidth, int newHeight) {

		return oldHeight / newWidth;
	}

	public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);
		matrix.postRotate(90);
		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
	}

	private void insertRecord() throws IOException {

		File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Memo/");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String note = editAddTextView.getText().toString();
		String picPath = Environment.getExternalStorageDirectory().getPath() + "/Memo/" + "memo_pic_data"
				+ System.currentTimeMillis() + ".png";
		String voicePath = "";

		// save the image context.
		File f = new File(picPath);
		f.createNewFile();
		FileOutputStream fOut = new FileOutputStream(f);
		result.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();

		// insert record to datebase.
		SQLiteDatabase db = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);
		ContentValues record = new ContentValues();
		record.put("note", note);
		record.put("pic_path", picPath);
		record.put("voice_path", voicePath);
		long rowid = db.insert("memo_datas", null, record);
		Log.i("axlecho", "插入数据库结果：" + rowid);
		db.close();
	}

	private void initPaint() {
		paint = new Paint();
		paint.setColor(Const.DEFAULTCOLOR);
		paint.setStrokeWidth(Const.DEFAULTPENSIZE);
		paint.setAntiAlias(true);
	}

	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.view_image_context);
		// get the sizes of imageView
		ViewTreeObserver vto = imageView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
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
	}

	private void initPopupSize() {
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

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

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

	private void initPopupColor() {
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
	}

	private void initPopupAddText() {
		popupAddTextView = getLayoutInflater().inflate(R.layout.menu_addtext, null, true);
		popupAddWindow = new PopupWindow(popupAddTextView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupAddWindow.setBackgroundDrawable(new BitmapDrawable());
		popupAddWindow.setOutsideTouchable(true);
		editAddTextView = (EditText) popupAddTextView.findViewById(R.id.view_addnote);

		btnAddText = (Button) findViewById(R.id.btn_addtext);
		btnAddText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupAddWindow.isShowing()) {
					popupAddWindow.dismiss();
				} else {
					popupAddWindow.showAsDropDown(v);
				}
			}

		});
	}
}
