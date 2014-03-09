package com.axlecho.memo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {
	private ListView listView;
	private List<Map<String, Object>> listDatas = new ArrayList<Map<String, Object>>();
	private ListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initListDatas();
		adapter = new ListAdapter();
		listView = new ListViewEx(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				final Intent intent = new Intent(MainActivity.this, ShowActivity.class);
				Map<String, Object> m = listDatas.get(pos);
				Log.i("axlecho", (String) m.get("img"));
				intent.putExtra("pic_path", (String) m.get("img"));
				intent.putExtra("note", (String) m.get("note"));
				startActivity(intent);
			}

		});
		setContentView(listView);

		listView.setBackgroundColor(this.getResources().getColor(R.color.bgwhite));
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
		case R.id.action_about:
			Log.i("menu", "item about");
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
			int recordIdColumn = cursor.getColumnIndex("recordid");
			int noteColumn = cursor.getColumnIndex("note");
			int picPathColumn = cursor.getColumnIndex("pic_path");
			int voiceColume = cursor.getColumnIndex("voice_path");
			int timeColume = cursor.getColumnIndex("time");

			map.put("note", cursor.getString(noteColumn));
			map.put("time", cursor.getString(timeColume));
			map.put("img", cursor.getString(picPathColumn));
			map.put("id", cursor.getString(recordIdColumn));
			map.put("voice", cursor.getString(voiceColume));
			listDatas.add(map);

			Log.i("axlecho", "note:" + cursor.getString(noteColumn));
			Log.i("axlecho", "pic_path:" + cursor.getString(picPathColumn));
			Log.i("axlecho", "time:" + cursor.getString(timeColume));
		}
		db.close();
	}

	private void deleteRecord(Map<String, Object> map) {
		SQLiteDatabase db = this.openOrCreateDatabase("datas", MODE_PRIVATE, null);
		String sql = "delete from memo_datas where recordid=" + map.get("id");
		Log.i("axlecho", "删除语句:" + sql);
		db.execSQL(sql);

		File pic_file = new File((String) map.get("img"));
		if (pic_file.exists() && pic_file.isFile()) {
			pic_file.delete();
		}

		File voice_file = new File((String) map.get("voice"));
		if (voice_file.exists() && voice_file.isFile()) {
			voice_file.delete();
		}
		db.close();
	}

	private class ListViewEx extends ListView implements OnTouchListener {
		private int pointX = -1;
		private int pointY = -1;
		private int position = -1;
		private int endX = -1;
		private int endY = -1;
		private int newpos = -1;
		private Button curDel_btn;
		private TextView curMask;
		private Context parentContext;

		public ListViewEx(Context context) {
			super(context);
			setOnTouchListener(this);
			parentContext = context;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 手指按下,计算焦点位于ListView的那个条目
				pointX = (int) event.getX();
				pointY = (int) event.getY();
				position = listView.pointToPosition(pointX, pointY);
				if (curDel_btn != null) {
					curDel_btn.setVisibility(View.GONE);
				}
				if (curMask != null) {
					curMask.setVisibility(View.VISIBLE);
				}
				break;
			case MotionEvent.ACTION_MOVE:

				break;
			case MotionEvent.ACTION_UP:
				endX = (int) event.getX();
				endY = (int) event.getY();
				newpos = listView.pointToPosition(endX, endY);
				// 原本想着加上这个条件（newpos==position）是不是更精确些，
				// 经过实践发现，其实我们在滑动listView的列表的时候有时候更渴望有滑动就ok
				// 只允许从右向左滑
				if (endX - pointX < -50) {
					// 获取到ListView第一个可见条目的position
					int firstVisiblePosition = listView.getFirstVisiblePosition();

					View view = listView.getChildAt(position - firstVisiblePosition);

					if (view == null)
						break;
					Button delbtn = (Button) view.findViewById(R.id.btn_del);
					TextView delbtnMask = (TextView) view.findViewById(R.id.btn_del_mask);
					TextView bottomMask = (TextView) view.findViewById(R.id.note);

					bottomMask.setVisibility(View.INVISIBLE);
					delbtn.setVisibility(View.VISIBLE);
					delbtnMask.setVisibility(View.VISIBLE);
					delbtnMask.startAnimation(AnimationUtils.loadAnimation(parentContext, R.anim.delete_show));
					delbtnMask.setVisibility(View.INVISIBLE);

					curDel_btn = delbtn;
					curMask = bottomMask;
					delbtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							deleteRecord(listDatas.get(position));
							listDatas.remove(position);
							adapter.notifyDataSetChanged();
						}
					});
				}
				break;

			default:
				break;
			}
			return false;
		}
	}

	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listDatas.size();
		}

		@Override
		public Object getItem(int pos) {
			return listDatas.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return 0;
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup parent) {
			String time = (String) listDatas.get(pos).get("time");
			String dates[] = time.split(" ");
			View view = getLayoutInflater().inflate(R.layout.list_item_view, null);
			TextView noteView = (TextView) view.findViewById(R.id.note);
			TextView timeView = (TextView) view.findViewById(R.id.time);
			TextView dateView = (TextView) view.findViewById(R.id.date);
			ImageView imageView = (ImageView) view.findViewById(R.id.img);

			noteView.setText((String) listDatas.get(pos).get("note"));
			dateView.setText(dates[0]);
			timeView.setText(dates[1]);
			imageView.setImageBitmap(BitmapFactory.decodeFile((String) listDatas.get(pos).get("img")));

			return view;
		}
	}
}
