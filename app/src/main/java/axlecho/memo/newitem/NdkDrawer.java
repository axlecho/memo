package axlecho.memo.newitem;

import android.graphics.Bitmap;

public class NdkDrawer {

	static {
		System.loadLibrary("ndkDrawer");
	}

	static boolean flag = false;

	static boolean setBitmap(Bitmap btm) {
		if (flag == true) {
			return false;
		}

		setdata(btm);
		flag = true;
		return true;
	}

	static boolean releaseBitmap() {
		if (flag == false) {
			return false;
		}

		releasedata();
		flag = false;
		return true;
	}

	native static void scale(Bitmap btm, int[] scaleRateX, int[] scaleRateY);

	native static void fillwhite(Bitmap btm);

	native static void setdata(Bitmap btm);

	native static void releasedata();

}
