package axlecho.memo.newitem;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Tool {
	public static Bitmap PicZoom(Bitmap bmp, int width, int height,
			boolean rotate) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);
		if (rotate)
			matrix.postRotate(90);
		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
	}
}
