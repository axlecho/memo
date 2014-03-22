package com.axlecho.memo.newitem;

import android.graphics.Bitmap;

public class NdkDrawer {

	native static void scale(Bitmap btm, float[] scaleRate);
}
