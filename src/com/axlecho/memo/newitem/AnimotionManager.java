package com.axlecho.memo.newitem;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.axlecho.memo.R;
import com.axlecho.memo.unit.Bezier;
import com.axlecho.memo.unit.Point;

class AnimotionManager {
	private SurfaceView sfv;
	private SurfaceHolder sfh;
	private final int DELETEANIMOTION = 0;
	private final int RESET = 1;
	private int height;
	private int width;

	int animDxRate = 15;
	int animDyRate = 15;

	private float tarWidthIn;
	private float tarWidthOut;
	private Timer timer;

	private Bitmap tarBtm;
	private Activity parent;
	private Button btnDel;

	public AnimotionManager(Activity parent) {
		initImageView(parent);
		this.parent = parent;
		btnDel = (Button) parent.findViewById(R.id.btn_del_content);
	}

	public void setPenSizeAnimation(Button btn, int size) {

		size = size + 1;
		Drawable bgdrawable = btn.getBackground();
		int w = bgdrawable.getIntrinsicWidth();
		int h = bgdrawable.getIntrinsicHeight();

		// BitmapDrawable bd = (BitmapDrawable) bgdrawable;
		// Bitmap bitmap = bd.getBitmap();
		Bitmap.Config config = bgdrawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(0xff, 0x22, 0x22, 0x22);
		paint.setTextSize(8);
		paint.setFakeBoldText(true);
		paint.setShadowLayer(2, 1.532f, 1.285f, 0xFF222222);
		Canvas canvas = new Canvas(bitmap);

		bitmap.eraseColor(Color.WHITE);
		canvas.drawText(String.valueOf(size), w / 2 + 3, h / 2 - 0.5f, paint);

		float x = -1;
		float y = -1;
		if (size <= 5) {

			x = w / 2.0f - 1;
			y = h / 2.0f + 2;
		} else {
			x = w / 2.0f - 1 - (size - 5) * 0.707f;
			y = h / 2.0f + 2 + (size - 5) * 0.707f;
		}

		canvas.drawCircle(x, y, size, paint);

		BitmapDrawable bd = new BitmapDrawable(bitmap);
		bd.setTargetDensity(parent.getResources().getDisplayMetrics());
		btn.setBackgroundDrawable(bd);
	}

	private void initImageView(Activity parent) {
		sfv = (SurfaceView) parent.findViewById(R.id.view_image_del);
		sfh = sfv.getHolder();
		sfv.setZOrderOnTop(true);
		sfh.setFormat(PixelFormat.TRANSLUCENT);
	}

	Handler handler = new Handler() {
		private int animWidth = 0;
		private int animHeigt = -1;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case RESET:
				animWidth = 0;
				animHeigt = 0;
				break;

			case DELETEANIMOTION:

				int animDx = width / animDxRate;
				int animDy = height / animDyRate;
				if (animWidth <= tarWidthIn) {
					Canvas canvas = sfh.lockCanvas();

					Paint canvasClear = new Paint();
					canvasClear.setAlpha(0);
					canvasClear.setXfermode(new PorterDuffXfermode(
							PorterDuff.Mode.DST_IN));
					canvas.drawRect(0, 0, width, height, canvasClear);

					Path tmpPath = new Path();
					tmpPath.moveTo(0, 0);
					tmpPath.cubicTo(0, height * 0.2f, animWidth, height * 0.6f,
							animWidth, height);
					tmpPath.lineTo(tarWidthOut, height);
					tmpPath.cubicTo(width, height * 0.2f, tarWidthOut,
							height * 0.6f, width, 0);
					tmpPath.lineTo(0, 0);

					Paint paint = new Paint();
					paint.setColor(0);
					paint.setAlpha(20);
					canvas.drawPath(tmpPath, paint);

					canvas.clipPath(tmpPath);

					//Matrix matrix = new Matrix();

					//float[] src = new float[] { 0, 0, // 左上
					//		width, 0,// 右上
					//		width, height,// 右下
					//		0, height // 左下
					//};

					//float[] dst = new float[] { 0, 0, // 左上
					//		width, 0,// 右上
					//		tarWidthOut, height,// 右下
					//		animWidth, height // 左下
					//};
					//matrix.setPolyToPoly(src, 0, dst, 0, src.length >> 1);
					Point src = new Point(0.0f,0.0f);
					Point dst = new Point(animWidth,height);
					Point c1 = new Point(0.0f,0.4f * height);
					Point c2 = new Point (animWidth,0.6f * height);
					
					Bezier bezier = new Bezier(src,dst,c1,c2);
					List<Point> points = bezier.getPoints(100);
					int[] scaleRateX = new int[103];
					int[] scaleRateY = new int[103];
					for(int i = 0;i < points.size();++ i){
						if(i > 102) {
							Log.e("am","arrayindex out bound! i:" + i + " points.size:" + points.size());
							return;
						}
						scaleRateX[i] = (int) points.get(i).x;
						scaleRateY[i] = (int) points.get(i).y;						
					}
					
					Bitmap bm = tarBtm.copy(Config.ARGB_8888, false);
					NdkDrawer.scale(bm,scaleRateX,scaleRateY);
					canvas.drawBitmap(bm,0,0, null);
					
					sfh.unlockCanvasAndPost(canvas);
					animWidth += animDx;

				} else {
					if (animHeigt > height) {
						timer.cancel();
						return;
					}
					animHeigt += animDy;
					Rect r = new Rect(0, 0, width, animHeigt);
					Canvas canvas = sfh.lockCanvas(r);
					Paint canvasClear = new Paint();
					canvasClear.setAlpha(0);
					canvasClear.setXfermode(new PorterDuffXfermode(
							PorterDuff.Mode.DST_IN));
					canvas.drawRect(0, 0, width, animHeigt, canvasClear);
					sfh.unlockCanvasAndPost(canvas);
				}

				break;
			default:
				break;
			}
		}
	};

	public void delAnimotion(Bitmap srcBtm) {
		tarBtm = srcBtm;
		height = srcBtm.getHeight() - btnDel.getHeight() - 10;
		width = srcBtm.getWidth();

		tarWidthIn = width - btnDel.getWidth() - 5.0f;
		tarWidthOut = width - 5.0f;

		handler.sendEmptyMessage(RESET);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(DELETEANIMOTION);
			}
		}, 0, 5);
	}
}