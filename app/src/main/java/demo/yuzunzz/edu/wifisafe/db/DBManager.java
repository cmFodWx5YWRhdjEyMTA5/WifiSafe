package demo.yuzunzz.edu.wifisafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DBManager {

	private AtomicInteger mCount = new AtomicInteger();

	private static DBManager instance;
	private static DBHelper helper;
	private SQLiteDatabase db;

	// 在构造方法中打开数据库接口
	private DBManager() {
	}

	// 对外暴露数据库管理类
	public static synchronized DBManager getInstance(Context context) {
		if (instance == null) {
			helper = new DBHelper(context);
			instance = new DBManager();
		}
		return instance;
	}

	// 开启数据库
	public synchronized SQLiteDatabase openDB() {
		if (mCount.incrementAndGet() == 1) {
			db = helper.getWritableDatabase();
		}
		return db;
	}

	// 当连接数为0时关闭数据库
	public synchronized void closeDB() {
		if (mCount.decrementAndGet() == 0) {
			db.close();
		}
	}

	public void executeTask(QueryExecutor executor) {
		SQLiteDatabase database = openDB();
		executor.execute(database);
		closeDB();
	}

	//子线程异步执行任务
	public void executeAsyncTask(final QueryExecutor executor) {
		new Thread(new Runnable() {
			public void run() {
				SQLiteDatabase database = openDB();
				executor.execute(database);
				closeDB();
			}

		}).start();
	}
}
