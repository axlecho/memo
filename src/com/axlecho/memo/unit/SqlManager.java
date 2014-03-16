package com.axlecho.memo.unit;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqlManager {
	private Context context;

	public SqlManager(Context context) {
		this.context = context;
	}

	public void init() {
		// initialize SQL database
		SQLiteDatabase db = context.openOrCreateDatabase("datas",
				Context.MODE_PRIVATE, null);
		db.execSQL("create table if not exists memo_datas "
				+ "(recordid integer primary key autoincrement, "
				+ "note varchar(256)," + "pic_path varchar(256),"
				+ "voice_path varchar(256),"
				+ "time datetime default current_timestamp)");
		db.close();
	}

	public void deleteRecord(Map<String, Object> map) {
		SQLiteDatabase db = context.openOrCreateDatabase("datas",
				Context.MODE_PRIVATE, null);
		String sql = "delete from memo_datas where recordid=" + map.get("id");
		Log.i("axlecho", "delete string:" + sql);
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

	public void initDatas(List<Map<String, Object>> datas) {
		datas.clear();
		SQLiteDatabase db = context.openOrCreateDatabase("datas",
				Context.MODE_PRIVATE, null);
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
			datas.add(map);

			Log.i("axlecho", "note:" + cursor.getString(noteColumn));
			Log.i("axlecho", "pic_path:" + cursor.getString(picPathColumn));
			Log.i("axlecho", "time:" + cursor.getString(timeColume));
		}
		db.close();
	}

	public void insertRecord(String note, String picPath, String voicePath) {

		// insert record to database.
		SQLiteDatabase db = context.openOrCreateDatabase("datas",
				Context.MODE_PRIVATE, null);
		ContentValues record = new ContentValues();
		record.put("note", note);
		record.put("pic_path", picPath);
		record.put("voice_path", voicePath);
		long rowid = db.insert("memo_datas", null, record);
		Log.i("database", "插入数据库结果：" + rowid);
		db.close();
	}
}
