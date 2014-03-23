package com.axlecho.memo.newitem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import com.axlecho.memo.R;

class CanvasManager {

	private Bitmap btmImage;
	private ImageView imageView;
	private Canvas canvasImage;

	private Bitmap btmSurface;
	private ImageView imageSurfaceView;
	private Canvas canvasSurface;

	private Path tmpPath;
	private float old_x;
	private float old_y;

	private Paint paint;
	private AnimotionManager am;

	public CanvasManager(Activity parent) {
		initImageView(parent);
		initImageSurfaceView(parent);
		am = new AnimotionManager(parent);
	}

	public void initImageView(Activity parent) {
		imageView = (ImageView) parent.findViewById(R.id.view_image_context);
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

	public void initImageSurfaceView(Activity parent) {
		imageSurfaceView = (ImageView) parent.findViewById(R.id.view_image_surface);
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
					// canvasSurface.drawPoint(me.getX(), me.getY(), paint);
					paint.setStyle(Style.FILL);
					canvasSurface.drawCircle(me.getX(), me.getY(), paint.getStrokeWidth() / 2.0f, paint);
					paint.setStyle(Style.STROKE);
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
					paint.setStyle(Style.FILL);
					canvasSurface.drawCircle(me.getX(), me.getY(), paint.getStrokeWidth() / 2.0f, paint);
					paint.setStyle(Style.STROKE);
					imageSurfaceView.invalidate();
				}
				return true;
			}
		});
	}

	public void setBgPic(String path) {
		Bitmap camerabitmap = BitmapFactory.decodeFile(path);
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
			Bitmap b = Tool.PicZoom(camerabitmap, camerabitmap.getWidth() / scale, camerabitmap.getHeight() / scale,
					flagRotate);

			canvasImage.drawBitmap(b, 0, 0, null);

			File f = new File(Environment.getExternalStorageDirectory() + "/workupload.jpg");
			f.delete();
		}
	}

	public void saveToPath(String path) throws IOException {
		// combine two layout
		canvasImage.drawBitmap(btmSurface, 0, 0, null);

		// save the image context.
		File f = new File(path);
		f.createNewFile();
		FileOutputStream fOut = new FileOutputStream(f);
		btmImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		fOut.flush();
		fOut.close();
	}

	public void clearSurface() {

		Bitmap bm = btmSurface.copy(Config.ARGB_8888, false);
		am.delAnimotion(bm); 

		Paint canvasClear = new Paint();
		canvasClear.setAlpha(0);
		canvasClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvasSurface.drawRect(0, 0, canvasSurface.getWidth(), canvasSurface.getHeight(), canvasClear);
		imageSurfaceView.invalidate();
	}

	public void clearBg() {
		Paint canvasClear = new Paint();
		canvasClear.setAlpha(0);
		canvasClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvasImage.drawRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight(), canvasClear);
		imageView.invalidate();
	}

	public void setPaint(Paint currentPaint) {
		paint = currentPaint;
	}
}
