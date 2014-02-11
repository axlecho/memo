package com.axlecho.memo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {
	private ListView listView;
	private List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getData();
		listView = new ListView(this);
		adapter = new SimpleAdapter(this, datas, R.layout.list_item_view, new String[] { "node", "time", "img" },
				new int[] { R.id.note, R.id.time, R.id.img });

		adapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		});

		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		setContentView(listView);

	}

	private void getData() {
		datas.clear();
		SQLiteDatabase db = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);
		Cursor cursor = db.rawQuery("select * from memo_datas", null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			int noteColumn = cursor.getColumnIndex("note");
			int picPathColumn = cursor.getColumnIndex("pic_path");
			// int voiceColume = cursor.getColumnIndex("voice_path");
			int timeColume = cursor.getColumnIndex("time");

			map.put("note", cursor.getString(noteColumn));
			map.put("time", cursor.getString(timeColume));
			map.put("img", BitmapFactory.decodeFile(cursor.getString(picPathColumn)));
			datas.add(map);

			Log.i("axlecho", "note:" + cursor.getString(noteColumn));
			Log.i("axlecho", "pic_path:" + cursor.getString(picPathColumn));
			Log.i("axlecho", "time:" + cursor.getString(timeColume));
		}
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add:
			Intent intent = new Intent(MainActivity.this, NewItemActivity.class);
			startActivityForResult(intent, 100);
			break;
		case R.id.menu_edit:
			Log.i("menu", "item edit");
			break;
		case R.id.action_settings:
			Log.i("menu", "item setting");
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		getData();
		adapter.notifyDataSetChanged();
		Log.i("axlecho", "update data");
		super.onActivityResult(requestCode, resultCode, data);
	}

}
