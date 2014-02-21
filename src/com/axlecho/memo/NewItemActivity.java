package com.axlecho.memo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

	private Bitmap btmImage;
	private ImageView imageView;
	private Canvas canvasImage;

	private Bitmap btmSurface;
	private ImageView imageSurfaceView;
	private Canvas canvasSurface;

	private float old_x;
	private float old_y;

	private Button btnSelectColor;
	private View popupColorView;
	private PopupWindow popupColor;
	private Button btnSelectGreen;
	private Button btnSelectBlue;
	private Button btnSelectRed;
	private Button btnSelectYellow;
	private Button btnSelectBlack;
	private Button btnSelectIvory;
	private Button btnSelectPurple;

	private Button btnSelectSize;
	private View popupSizeView;
	private PopupWindow popupSize;
	private SeekBar seekbarSize;
	private TextView penSizeView;

	private Button btnAddText;
	private View popupAddTextView;
	private PopupWindow popupAddWindow;
	private EditText editAddTextView;

	private Button btnEraser;

	private Paint paint;
	private TextView noteView;
	private Path tmpPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newitem);

		// TODO 适应横竖
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initPaint();
		initImageView();
		initImageSurfaceView();
		initPopupSize();
		initPopupColor();
		initPopupAddText();

		btnEraser = (Button) findViewById(R.id.btn_eraser);
		btnEraser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				paint.setAlpha(0);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			}

		});

		noteView = (TextView) findViewById(R.id.view_note);
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
			Bitmap camerabitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
					+ "/workupload.jpg");

			if (null != camerabitmap) {
				// 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。

				int oldWidth = camerabitmap.getWidth();
				int oldHeight = camerabitmap.getHeight();
				int newWidth = canvasImage.getWidth();
				int newHeight = canvasImage.getHeight();

				boolean flagRotate = false;
				int scale = 1;
				if ((oldWidth - oldHeight) * (newWidth - newHeight) < 0) {
					flagRotate = true;
					scale = oldWidth / newHeight;
				} else {
					scale = oldWidth / newWidth;
				}

				Bitmap b = PicZoom(camerabitmap, camerabitmap.getWidth() / scale, camerabitmap.getHeight() / scale,
						flagRotate);
				canvasImage.drawBitmap(b, 0, 0, null);

				File f = new File(Environment.getExternalStorageDirectory() + "/workupload.jpg");
				f.delete();
			}

		}
	}

	public static Bitmap PicZoom(Bitmap bmp, int width, int height, boolean rotate) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);
		if (rotate)
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

		// combine two layout
		canvasImage.drawBitmap(btmSurface, 0, 0, null);

		// save the image context.
		File f = new File(picPath);
		f.createNewFile();
		FileOutputStream fOut = new FileOutputStream(f);
		btmImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
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
		paint.setStyle(Style.STROKE);
	}

	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.view_image_context);
		ViewTreeObserver vto = imageView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				imageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				btmImage = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Config.ARGB_8888);
				imageView.setImageBitmap(btmImage);
				canvasImage = new Canvas(btmImage);
			}
		});
	}

	private void initImageSurfaceView() {
		imageSurfaceView = (ImageView) findViewById(R.id.view_image_surface);
		ViewTreeObserver vto = imageSurfaceView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				imageSurfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				btmSurface = Bitmap.createBitmap(imageSurfaceView.getWidth(), imageSurfaceView.getHeight(),
						Config.ARGB_8888);
				imageSurfaceView.setImageBitmap(btmSurface);
				canvasSurface = new Canvas(btmSurface);
			}
		});
		imageSurfaceView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent me) {
				if (me.getAction() == MotionEvent.ACTION_DOWN) {
					old_x = me.getX();
					old_y = me.getY();
					tmpPath = new Path();
					tmpPath.moveTo(me.getX(), me.getY());
				} else if (me.getAction() == MotionEvent.ACTION_MOVE) {
					if (tmpPath != null)
						canvasSurface.drawPath(tmpPath, paint);

					final float dx = Math.abs(me.getX() - old_x);
					final float dy = Math.abs(me.getY() - old_y);

					// 两点之间的距离大于等于3时，生成贝塞尔绘制曲线
					if (dx >= 3 || dy >= 3) {
						// 设置贝塞尔曲线的操作点为起点和终点的一半
						float cX = (me.getX() + old_x) / 2;
						float cY = (me.getY() + old_y) / 2;

						// 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
						tmpPath.quadTo(old_x, old_y, cX, cY);

					}
					old_x = me.getX();
					old_y = me.getY();

					imageSurfaceView.invalidate();
				} else if (me.getAction() == MotionEvent.ACTION_UP) {
					tmpPath.lineTo(me.getX(), me.getY());
					canvasSurface.drawPath(tmpPath, paint);
					imageSurfaceView.invalidate();
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
		ColorSelectOnClickListener csOnClickListener = new ColorSelectOnClickListener(this.getResources());
		popupColorView = getLayoutInflater().inflate(R.layout.menu_selectcolor, null, true);
		popupColor = new PopupWindow(popupColorView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupColor.setBackgroundDrawable(new BitmapDrawable());
		popupColor.setOutsideTouchable(true);
		// popupColor.setAnimationStyle(R.style.PopupAnimation);

		btnSelectGreen = (Button) popupColorView.findViewById(R.id.btn_select_green);
		btnSelectBlue = (Button) popupColorView.findViewById(R.id.btn_select_blue);
		btnSelectRed = (Button) popupColorView.findViewById(R.id.btn_select_red);
		btnSelectYellow = (Button) popupColorView.findViewById(R.id.btn_select_yellow);
		btnSelectBlack = (Button) popupColorView.findViewById(R.id.btn_select_black);
		btnSelectIvory = (Button) popupColorView.findViewById(R.id.btn_select_ivory);
		btnSelectPurple = (Button) popupColorView.findViewById(R.id.btn_select_purple);

		btnSelectGreen.setOnClickListener(csOnClickListener);
		btnSelectBlue.setOnClickListener(csOnClickListener);
		btnSelectRed.setOnClickListener(csOnClickListener);
		btnSelectYellow.setOnClickListener(csOnClickListener);
		btnSelectBlack.setOnClickListener(csOnClickListener);
		btnSelectIvory.setOnClickListener(csOnClickListener);
		btnSelectPurple.setOnClickListener(csOnClickListener);

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
		editAddTextView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable ed) {
				noteView.setText(ed.toString());
				noteView.setVisibility(View.VISIBLE);

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				noteView.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

		});
	}

	class ColorSelectOnClickListener implements OnClickListener {
		private Resources r;

		public ColorSelectOnClickListener(Resources r) {
			this.r = r;
		}

		@Override
		public void onClick(View v) {
			paint.setAlpha(255);
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
			switch (v.getId()) {
			case R.id.btn_select_green:
				paint.setColor(r.getColor(R.color.green));
				break;
			case R.id.btn_select_black:
				paint.setColor(r.getColor(R.color.black));
				break;
			case R.id.btn_select_blue:
				paint.setColor(r.getColor(R.color.blue));
				break;
			case R.id.btn_select_ivory:
				paint.setColor(r.getColor(R.color.ivory));
				break;
			case R.id.btn_select_purple:
				paint.setColor(r.getColor(R.color.purple));
				break;
			case R.id.btn_select_red:
				paint.setColor(r.getColor(R.color.red));
				break;
			case R.id.btn_select_yellow:
				paint.setColor(r.getColor(R.color.yellow));
				break;
			default:
				break;
			}

			popupColor.dismiss();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		} else {
		}
	}
}
