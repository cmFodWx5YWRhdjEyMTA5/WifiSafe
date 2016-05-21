package demo.yuzunzz.edu.wifisafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {

	private Context mContext;

	public DBHelper(Context context) {
		super(context, Configuration.DB_NAME, null, Configuration.DB_VERSION);
		mContext = context;
	}

	/**
	 * 数据库第一次创建时调用
	 * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}


	/**
	 * 数据库升级时调用
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//数据库不升级
		if (newVersion <= oldVersion) {
			return;
		}
		Configuration.oldVersion = oldVersion;
	}


	

}
