package com.axlecho.memo.main;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.axlecho.memo.R;

class ListAdapter extends BaseAdapter {

	private List<Map<String, Object>> datas;
	private Activity parent;

	public ListAdapter(List<Map<String, Object>> datas, Activity parent) {
		this.datas = datas;
		this.parent = parent;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int pos) {
		return datas.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return 0;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup __parent) {
		String time = (String) datas.get(pos).get("time");
		String dates[] = time.split(" ");
		View itemView = parent.getLayoutInflater().inflate(
				R.layout.list_item_view, null);
		TextView noteView = (TextView) itemView.findViewById(R.id.note);
		TextView timeView = (TextView) itemView.findViewById(R.id.time);
		TextView dateView = (TextView) itemView.findViewById(R.id.date);
		ImageView imageView = (ImageView) itemView.findViewById(R.id.img);

		noteView.setText((String) datas.get(pos).get("note"));
		dateView.setText(dates[0]);
		timeView.setText(dates[1]);

		String picPath = (String) datas.get(pos).get("img");
		imageView.setImageBitmap(BitmapFactory.decodeFile(picPath));

		return itemView;
	}
}