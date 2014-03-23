package com.axlecho.memo.newitem;

import android.graphics.Bitmap;

public class NdkDrawer {
	static {
		System.loadLibrary("ndkDrawer");
	}
	
	native static void scale(Bitmap btm, float[] scaleRate);
}
