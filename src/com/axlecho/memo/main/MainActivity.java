package com.axlecho.memo.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.axlecho.memo.R;
import com.axlecho.memo.ShowActivity;
import com.axlecho.memo.newitem.NewItemActivity;
import com.axlecho.memo.unit.Const;
import com.axlecho.memo.unit.SqlManager;

public class MainActivity extends SherlockActivity {
	private ListView listView;
	private List<Map<String, Object>> listDatas = new ArrayList<Map<String, Object>>();
	private ListAdapter adapter;
	private SqlManager sqlManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sqlManager = new SqlManager(this);
		sqlManager.initDatas(listDatas);

		adapter = new ListAdapter(listDatas, this);
		listView = new ListViewEx(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				final Intent intent = new Intent(MainActivity.this,
						ShowActivity.class);
				Map<String, Object> m = listDatas.get(pos);
				Log.i("axlecho", (String) m.get("img"));
				intent.putExtra("pic_path", (String) m.get("img"));
				intent.putExtra("note", (String) m.get("note"));
				startActivity(intent);
			}

		});
		setContentView(listView);

		listView.setBackgroundColor(this.getResources().getColor(
				R.color.bgwhite));
		if (listDatas.isEmpty()) {
			listView.setBackgroundResource(R.drawable.master_null_bg);
		}
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
		if (requestCode == Const.NEWITEMRESULT) {
			sqlManager.initDatas(listDatas);
			adapter.notifyDataSetChanged();
		}

		if (!listDatas.isEmpty()) {
			listView.setBackgroundColor(this.getResources().getColor(
					R.color.bgwhite));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class ListViewEx extends ListView implements OnTouchListener {

		private int pointX = -1;
		private int pointY = -1;
		private int position = -1;
		private int endX = -1;
		// private int endY = -1;
		// private int newpos = -1;

		private Button curDel_btn;
		private TextView curMask;
		private Context context;

		public ListViewEx(Context context) {
			super(context);
			setOnTouchListener(this);
			this.context = context;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				// 手指按下,计算焦点位于ListView的那个条目
				pointX = (int) event.getX();
				pointY = (int) event.getY();
				position = this.pointToPosition(pointX, pointY);
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
				// endY = (int) event.getY();
				// newpos = listView.pointToPosition(endX, endY);
				// 原本想着加上这个条件（newpos==position）是不是更精确些，
				// 经过实践发现，其实我们在滑动listView的列表的时候有时候更渴望有滑动就ok
				// 只允许从右向左滑
				if (endX - pointX < -50) {

					// 获取到ListView第一个可见条目的position
					int firstVisiblePosition = listView
							.getFirstVisiblePosition();
					View view = listView.getChildAt(position
							- firstVisiblePosition);

					if (view == null)
						break;
					Button delbtn = (Button) view.findViewById(R.id.btn_del);
					TextView deletebtnMask = (TextView) view
							.findViewById(R.id.btn_del_mask);
					TextView bottomMask = (TextView) view
							.findViewById(R.id.note);

					bottomMask.setVisibility(View.INVISIBLE);
					delbtn.setVisibility(View.VISIBLE);

					deletebtnMask.setVisibility(View.VISIBLE);
					deletebtnMask.startAnimation(AnimationUtils.loadAnimation(
							context, R.anim.delete_show));
					deletebtnMask.setVisibility(View.INVISIBLE);

					curDel_btn = delbtn;
					curMask = bottomMask;
					delbtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							sqlManager.deleteRecord(listDatas.get(position));
							listDatas.remove(position);
							adapter.notifyDataSetChanged();
							if (listDatas.isEmpty()) {
								listView.setBackgroundResource(R.drawable.master_null_bg);
							}
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

}
