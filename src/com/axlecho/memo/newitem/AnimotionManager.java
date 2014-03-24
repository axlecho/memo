package com.axlecho.memo.newitem;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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

	private List<Point> getBezierPoints(float animWidth) {
		Point src = new Point(0.0f, 0.0f);
		Point dst = new Point(animWidth, height);
		Point c1 = new Point(0.0f, 0.2f * height);
		Point c2 = new Point(animWidth, 0.6f * height);

		Bezier bezier = new Bezier(src, dst, c1, c2);
		return bezier.getPoints(200);
	}

	private void earserBackground(Canvas canvas){
		Paint canvasClear = new Paint();
		canvasClear.setAlpha(0);
		canvasClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), canvasClear);
	}
	
	Handler handler = new Handler() {
		private float animWidth = -1.0f;
		// private float animHeigt = -1.0f;

		private int tmpT = 0;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			List<Point> finalPoints = getBezierPoints(tarWidthIn);

			int[] finalScaleRateX = new int[finalPoints.size()];
			int[] finalScaleRateY = new int[finalPoints.size()];
			for (int i = 0; i < finalPoints.size(); ++i) {
				finalScaleRateX[i] = (int) finalPoints.get(i).x;
				finalScaleRateY[i] = (int) finalPoints.get(i).y;
			}
			int finalT = finalPoints.size();

			switch (msg.what) {
			case RESET:
				animWidth = 0.0f;
				tmpT = 0;
				NdkDrawer.setBitmap(tarBtm);
				break;

			case DELETEANIMOTION:

				int animDx = width / animDxRate;

				if (animWidth <= tarWidthIn) {
					Canvas canvas = sfh.lockCanvas();
					earserBackground(canvas);
					
					Path tmpPath = new Path();
					tmpPath.moveTo(0, 0);
					tmpPath.cubicTo(0, height * 0.2f, animWidth, height * 0.6f, animWidth, height);
					tmpPath.lineTo(tarWidthOut, height);
					tmpPath.cubicTo(width, height * 0.2f, tarWidthOut, height * 0.6f, width, 0);
					tmpPath.lineTo(0, 0);
					canvas.clipPath(tmpPath);

					List<Point> points = getBezierPoints(animWidth);

					int[] scaleRateX = new int[points.size() + 1];
					int[] scaleRateY = new int[points.size() + 1];

					for (int i = 0; i < points.size(); ++i) {
						scaleRateX[i] = (int) points.get(i).x;
						scaleRateY[i] = (int) points.get(i).y;
					}

					NdkDrawer.scale(tarBtm, scaleRateX, scaleRateY);
					canvas.drawBitmap(tarBtm, 0, 0, null);

					sfh.unlockCanvasAndPost(canvas);

					// finish the horizontal scaling
					if (tarWidthIn - animWidth < 0.01) {
						animWidth += 1.0f;
						return;
					}

					animWidth += tarWidthIn - animWidth < animDx ? tarWidthIn - animWidth : animDx;

					Log.i("am", "horizontal scaling");
				} else {
					if (tmpT >= finalT) {
						timer.cancel();
						Log.i("am", "finish the work,timer cancel && release the img data.");
						NdkDrawer.releaseBitmap();
						Log.i("am", "release data ok");
						
						Canvas canvas = sfh.lockCanvas();
						if (canvas == null) {
							Log.e("am", "lockCanvas failed");
							return;
						}

						earserBackground(canvas);
						sfh.unlockCanvasAndPost(canvas);
						return;
					}

					int tmpx = finalScaleRateX[tmpT];
					int tmpy = finalScaleRateY[tmpT];

					int[] tmpScaleRateX = new int[finalT - tmpT];
					int[] tmpScaleRateY = new int[finalT - tmpT];

					for (int i = tmpT; i < finalT; ++i) {
						tmpScaleRateX[i - tmpT] = finalScaleRateX[i] - tmpx;
						tmpScaleRateY[i - tmpT] = finalScaleRateY[i] - tmpy;
					}

					Canvas canvas = sfh.lockCanvas();
					if (canvas == null) {
						Log.e("am", "lockCanvas failed");
						return;
					}

					earserBackground(canvas);

					Path tmpPath = new Path();
					tmpPath.moveTo(0, 0);
					tmpPath.cubicTo(0, height * 0.2f, tarWidthIn, height * 0.6f, tarWidthIn, height);
					tmpPath.lineTo(tarWidthOut, height);
					tmpPath.cubicTo(width, height * 0.2f, tarWidthOut, height * 0.6f, width, 0);
					tmpPath.lineTo(0, 0);
					canvas.clipPath(tmpPath);
					
					Bitmap tmpbm = Bitmap.createBitmap(width - (int) tmpx, height - (int) tmpy, Config.ARGB_8888);
					NdkDrawer.scale(tmpbm, tmpScaleRateX, tmpScaleRateY);
					canvas.drawBitmap(tmpbm, tmpx, tmpy, null);

					sfh.unlockCanvasAndPost(canvas);
					tmpT += 10;
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