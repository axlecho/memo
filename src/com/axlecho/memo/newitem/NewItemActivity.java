package com.axlecho.memo.newitem;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.axlecho.memo.R;
import com.axlecho.memo.unit.Const;
import com.axlecho.memo.unit.SqlManager;

public class NewItemActivity extends SherlockActivity {

	private Button btnAddText;
	private Button btnAddPic;
	private View popupAddView;
	private PopupWindow popupAdd;
	private EditText editAddTextView;
	private TextView noteView;
	private LinearLayout context;
	private Button btnDel;
	private Button btnSave;

	private ToolsManager tm;
	private CanvasManager cm;
	private SqlManager sqm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newitem);
		sqm = new SqlManager(this);
		// TODO 适应横竖
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		noteView = (TextView) findViewById(R.id.view_note);

		popupAddView = getLayoutInflater().inflate(R.layout.menu_add, null, true);
		popupAdd = new PopupWindow(popupAddView, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		popupAdd.setBackgroundDrawable(new ColorDrawable(0xfff5f5f5));
		popupAdd.setOutsideTouchable(true);
		context = (LinearLayout) popupAddView.findViewById(R.id.context);
		btnAddText = (Button) popupAddView.findViewById(R.id.btn_addtext);
		btnAddText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				context.setVisibility(View.GONE);
				editAddTextView.setVisibility(View.VISIBLE);
				editAddTextView.setFocusable(true);
				editAddTextView.requestFocus();
				editAddTextView.setFocusableInTouchMode(true);
			}
		});

		editAddTextView = (EditText) popupAddView.findViewById(R.id.view_addnote);
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

			}

		});

		btnAddPic = (Button) popupAddView.findViewById(R.id.btn_addpic);
		btnAddPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "workupload.jpg"));
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(cameraIntent, Const.CAMERARESULT);
				popupAdd.dismiss();
			}

		});

		btnSave = (Button) findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					insertRecord();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finish();
			}

		});

		tm = new ToolsManager(this);
		cm = new CanvasManager(this);

		tm.setCanvasManager(cm);

		btnDel = (Button) findViewById(R.id.btn_del_content);
		
		final AlertDialog.Builder builder = new Builder(this);
		btnDel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				builder.setMessage("清除屏幕");
				builder.setTitle("警告");
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//cm.clearSurface();
						//cm.clearBg();
						
						cm.clear();
					}
				});

				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog alertDialog = builder.create();
				alertDialog.setCancelable(true);
				alertDialog.show();
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.newitem, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_content:
			if (popupAdd.isShowing()) {
				popupAdd.dismiss();
			} else {
				context.setVisibility(View.VISIBLE);
				editAddTextView.setVisibility(View.GONE);
				View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
				popupAdd.showAsDropDown(v, 0, -v.getHeight());
			}
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
			cm.setBgPic(Environment.getExternalStorageDirectory() + "/workupload.jpg");
		}
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
		cm.saveToPath(picPath);
		sqm.insertRecord(note, picPath, voicePath);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		} else {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("丢弃记录？");
			builder.setTitle("警告");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					NewItemActivity.this.finish();

				}
			});

			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog alertDialog = builder.create();
			alertDialog.setCancelable(false);
			alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_SEARCH) {
						return true;
					} else {
						return false; // 默认返回 false
					}
				}
			});
			alertDialog.show();
		}
		return super.onKeyDown(keyCode, event);
	}

}
