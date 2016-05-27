package demo.yuzunzz.edu.wifisafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;
import demo.yuzunzz.edu.wifisafe.db.DBManager;

/**
 * Created by 97349 on 2016/4/9.
 */
public class Dao {

    private SQLiteDatabase mDatabase;
    private DBManager mDBmgr;

    public Dao(Context context){
        mDBmgr = DBManager.getInstance(context);
        mDatabase = mDBmgr.openDB();
    }

    public void add(ScanResultPro scanResultPro){
        try {
            mDatabase.beginTransaction();
            String sql = "INSERT INTO list VALUES(null,?,?,?,?,?)";
            mDatabase.execSQL(sql,new Object[] {scanResultPro.getSSID(),scanResultPro.getBSSID(),scanResultPro.getFlag(),scanResultPro.getSafeLevel(),scanResultPro.getLastScanTime()});
            mDatabase.setTransactionSuccessful();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
            mDatabase.endTransaction();
        }
    }

    public boolean isApExist(String BSSID){
        Cursor c = mDatabase.rawQuery("SELECT * FROM list WHERE BSSID = ?", new String[]{BSSID});
        if (c.getCount() == 0){
            return  false;
        } else {
            return  true;
        }
    }

    public boolean isPwdExist(String password){
        Cursor c = mDatabase.rawQuery("SELECT * FROM PASSWORD WHERE password = ?", new String[]{password});
        if (c.getCount() == 0){
            return  false;
        } else {
            return  true;
        }
    }


    public String getLatestFlag(String BSSID){
        Cursor c = mDatabase.query("list",new String[]{"flag"},"BSSID=?",new String[] {BSSID},null,null,null);
        String flag = "";
        while (c.moveToNext()){
            flag = c.getString(c.getColumnIndex("flag"));
        }
        return flag;
    }

    public String getLastScanTime(String BSSID){
        Cursor c = mDatabase.query("list",new String[]{"lastScanTime"},"BSSID=?",new String[] {BSSID},null,null,null);
        String lastScanTime = "";
        while (c.moveToNext()){
            lastScanTime = c.getString(c.getColumnIndex("lastScanTime"));
        }
        return lastScanTime;
    }

    public List<ScanResultPro> getBlackListAp(){
        List<ScanResultPro> list = new ArrayList<ScanResultPro>();
        Cursor c = mDatabase.query("list",new String[]{"SSID","BSSID"},"flag=?",new String[] {"danger"},null,null,null);
        while (c.moveToNext()){
            String ssid = c.getString(c.getColumnIndex("SSID"));
            String bssid = c.getString(c.getColumnIndex("BSSID"));
            String firm = getFirm(bssid);
            String lastScanTime  = getLastScanTime(bssid);
            ScanResultPro scanResultPro = new ScanResultPro(ssid,bssid,"danger","dangerrous",firm,lastScanTime);
            list.add(scanResultPro);
        }
        return list;
    }

    public List<ScanResultPro> getWhiteListAp(){
        List<ScanResultPro> list = new ArrayList<ScanResultPro>();
        Cursor c = mDatabase.query("list",new String[]{"SSID","BSSID"},"flag=?",new String[] {"trust"},null,null,null);
        while (c.moveToNext()){
            String ssid = c.getString(c.getColumnIndex("SSID"));
            String bssid = c.getString(c.getColumnIndex("BSSID"));
            String firm = getFirm(bssid);
            String lastScanTime  = getLastScanTime(bssid);
            ScanResultPro scanResultPro = new ScanResultPro(ssid,bssid,"trust","safe",firm,lastScanTime);
            list.add(scanResultPro);
        }
        return list;
    }

    public String getFirm(String BSSID){
        String bssid = BSSID.substring(0,8).toUpperCase();
        String firm = "";
        Cursor c = mDatabase.query("MAC",new String[]{"firm"},"mac=?",new String[] {bssid},null,null,null);
        while (c.moveToNext()){
            firm = c.getString(c.getColumnIndex("firm"));
        }

        if (firm.equals("")){
            return "unknown";
        } else {
            return firm;
        }
    }
    public void changeApFlag(String BSSID,int flag){
        ContentValues cv = new ContentValues();
        switch (flag){
            case 0:
                cv.put("flag","default");
                break;
            case 1:
                cv.put("flag","danger");
                break;
            case 2:
                cv.put("flag","trust");
                break;
            default:
                break;
        }
        mDatabase.beginTransaction();
        mDatabase.update("list",cv,"BSSID=?",new String[] {BSSID});
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void updateLastScanTime(String BSSID,String time){
        ContentValues cv = new ContentValues();
        cv.put("lastScanTime",time);
        mDatabase.beginTransaction();
        mDatabase.update("list",cv,"BSSID=?",new String[] {BSSID});
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }
}
