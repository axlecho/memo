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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {
	private ListView listView;
	private List<Map<String, Object>> listDatas = new ArrayList<Map<String, Object>>();
	private SimpleAdapter adapter;
	private Bitmap bm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initListDatas();

		String[] from = new String[] { "note", "time", "img" };
		int[] to = new int[] { R.id.note, R.id.time, R.id.img };
		adapter = new SimpleAdapter(this, listDatas, R.layout.list_item_view, from, to);
		adapter.setViewBinder(new ViewBinder() {

			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView) {
					ImageView iv = (ImageView) view;
					iv.setImageBitmap(BitmapFactory.decodeFile((String) data));
					return true;
				}
				return false;
			}
		});

		listView = new ListView(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				final Intent intent = new Intent(MainActivity.this, ShowActivity.class);
				Map<String, Object> m = listDatas.get(pos);
				Log.i("axlecho", (String) m.get("img"));
				intent.putExtra("pic_path", (String) m.get("img"));

				startActivity(intent);
			}

		});
		setContentView(listView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem it) {
		switch (it.getItemId()) {
		case R.id.menu_add:
			Intent intent = new Intent(MainActivity.this, NewItemActivity.class);
			startActivityForResult(intent, Const.NEWITEMRESULT);
			break;
		case R.id.menu_edit:
			Log.i("menu", "item edit");
			break;
		case R.id.action_settings:
			Log.i("menu", "item setting");
			break;
		default:
			return super.onOptionsItemSelected(it);
		}
		return super.onOptionsItemSelected(it);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Const.NEWITEMRESULT)
			initListDatas();
		adapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void initListDatas() {
		listDatas.clear();
		SQLiteDatabase db = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);
		Cursor cursor = db.rawQuery("select * from memo_datas", null);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			int noteColumn = cursor.getColumnIndex("note");
			int picPathColumn = cursor.getColumnIndex("pic_path");
			// TODO int voiceColume = cursor.getColumnIndex("voice_path");
			int timeColume = cursor.getColumnIndex("time");

			map.put("note", cursor.getString(noteColumn));
			map.put("time", cursor.getString(timeColume));
			map.put("img", cursor.getString(picPathColumn));
			listDatas.add(map);

			Log.i("axlecho", "note:" + cursor.getString(noteColumn));
			Log.i("axlecho", "pic_path:" + cursor.getString(picPathColumn));
			Log.i("axlecho", "time:" + cursor.getString(timeColume));
		}
		db.close();
	}
}
