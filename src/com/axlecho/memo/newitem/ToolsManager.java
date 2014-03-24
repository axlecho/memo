package com.axlecho.memo.newitem;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.axlecho.memo.R;
import com.axlecho.memo.unit.Const;

class ToolsManager {
	private Button btnEraser;
	private Button btnPen;

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

	private Paint penPaint;
	private Paint eraserPaint;
	private Paint currentPaint;

	private ColorSelectOnClickListener csOnClickListener;

	private AnimotionManager am;
	private CanvasManager cm;

	public ToolsManager(Activity parent) {

		csOnClickListener = new ColorSelectOnClickListener(parent.getResources());
		am = new AnimotionManager(parent);
		initPaint();
		initPenEraser(parent);
		initPopupSize(parent);
		initPopupColor(parent);

	}

	public void setCanvasManager(CanvasManager cm) {
		this.cm = cm;
		cm.setPaint(currentPaint);
	}

	private void initPenEraser(final Activity parent) {
		btnEraser = (Button) parent.findViewById(R.id.btn_eraser);
		btnEraser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				currentPaint = eraserPaint;
				cm.setPaint(currentPaint);
				am.setPenSizeAnimation(btnSelectSize, (int) currentPaint.getStrokeWidth());
				seekbarSize.setProgress((int) currentPaint.getStrokeWidth());
				btnPen.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.pen));
				btnEraser.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.eraserpress));

				// disable selectcolor button
				btnSelectColor.setOnClickListener(null);
				btnSelectColor.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.colorpress));

			}

		});

		btnPen = (Button) parent.findViewById(R.id.btn_pen);
		btnPen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				currentPaint = penPaint;
				cm.setPaint(currentPaint);

				am.setPenSizeAnimation(btnSelectSize, (int) currentPaint.getStrokeWidth());
				seekbarSize.setProgress((int) currentPaint.getStrokeWidth());
				penPaint.setAlpha(255);
				penPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
				btnPen.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.penpress));
				btnEraser.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.eraser));

				//enable selectcolor button
				btnSelectColor.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.btnstyle_color));
				btnSelectColor.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (popupColor.isShowing()) {
							popupColor.dismiss();
						} else {
							popupColor.showAsDropDown(v, 0, -(v.getHeight() + popupColorHeight + 5));

						}
					}

				});
			}
		});

		btnPen.setBackgroundDrawable(parent.getResources().getDrawable(R.drawable.penpress));

	}

	private void initPaint() {
		penPaint = new Paint();
		penPaint.setColor(Const.DEFAULTCOLOR);
		penPaint.setStrokeWidth(Const.DEFAULTPENSIZE);
		penPaint.setAntiAlias(true);
		penPaint.setStyle(Style.STROKE);

		eraserPaint = new Paint();
		eraserPaint.setStrokeWidth(Const.DEFAULTERASERSIZE);
		eraserPaint.setAlpha(0);
		eraserPaint.setStyle(Style.STROKE);
		eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		currentPaint = penPaint;
	}

	private int popupSizeHeight = -1;
	private int popupColorHeight = -1;

	private void initPopupSize(Activity parent) {
		popupSizeView = parent.getLayoutInflater().inflate(R.layout.menu_selectsize, null, true);
		popupSize = new PopupWindow(popupSizeView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupSize.setBackgroundDrawable(new BitmapDrawable());
		popupSize.setOutsideTouchable(true);

		penSizeView = (TextView) popupSizeView.findViewById(R.id.view_penSize);
		seekbarSize = (SeekBar) popupSizeView.findViewById(R.id.seekbar_selectsize);
		seekbarSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
				penSizeView.setText("" + progress);
				currentPaint.setStrokeWidth(progress);
				am.setPenSizeAnimation(btnSelectSize, progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

		});

		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		popupSizeView.measure(w, h);
		popupSizeHeight = popupSizeView.getMeasuredHeight();

		btnSelectSize = (Button) parent.findViewById(R.id.btn_selectsize);
		btnSelectSize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupSize.isShowing()) {
					popupSize.dismiss();
				} else {
					popupSize.showAsDropDown(v, 0, -(v.getHeight() + popupSizeHeight));
				}
			}

		});

		am.setPenSizeAnimation(btnSelectSize, seekbarSize.getProgress());
	}

	private void initPopupColor(Activity parent) {
		popupColorView = parent.getLayoutInflater().inflate(R.layout.menu_selectcolor, null, true);
		popupColor = new PopupWindow(popupColorView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupColor.setBackgroundDrawable(new BitmapDrawable());
		popupColor.setOutsideTouchable(true);

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

		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		popupColorView.measure(w, h);
		popupColorHeight = popupColorView.getMeasuredHeight();

		btnSelectColor = (Button) parent.findViewById(R.id.btn_selectcolor);
		btnSelectColor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupColor.isShowing()) {
					popupColor.dismiss();
				} else {
					popupColor.showAsDropDown(v, 0, -(v.getHeight() + popupColorHeight + 5));

				}
			}

		});
	}

	public class ColorSelectOnClickListener implements OnClickListener {
		private Resources r;

		public ColorSelectOnClickListener(Resources r) {
			this.r = r;
		}

		@Override
		public void onClick(View v) {
			// 禁用eraser模式下的colorselect
			if (currentPaint == eraserPaint) {
				popupColor.dismiss();
				return;
			}
			penPaint.setAlpha(255);
			penPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
			switch (v.getId()) {
			case R.id.btn_select_green:
				currentPaint.setColor(r.getColor(R.color.green));
				break;
			case R.id.btn_select_black:
				currentPaint.setColor(r.getColor(R.color.black));
				break;
			case R.id.btn_select_blue:
				currentPaint.setColor(r.getColor(R.color.blue));
				break;
			case R.id.btn_select_ivory:
				currentPaint.setColor(r.getColor(R.color.ivory));
				break;
			case R.id.btn_select_purple:
				currentPaint.setColor(r.getColor(R.color.purple));
				break;
			case R.id.btn_select_red:
				currentPaint.setColor(r.getColor(R.color.red));
				break;
			case R.id.btn_select_yellow:
				currentPaint.setColor(r.getColor(R.color.yellow));
				break;
			default:
				break;
			}

			popupColor.dismiss();
		}
	}

}