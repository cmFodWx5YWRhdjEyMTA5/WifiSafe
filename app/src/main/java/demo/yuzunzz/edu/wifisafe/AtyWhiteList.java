package demo.yuzunzz.edu.wifisafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;
import demo.yuzunzz.edu.wifisafe.dao.Dao;

/**
 * Created by 97349 on 2016/4/5.
 */
public class AtyWhiteList extends Activity {

    private ListView lvWhiteList;
    private List<ScanResultPro> mWhiteList;
    private ListAdapter mAdapter;
    private Dao dao;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flag_list);
        dao = new Dao(this);
        mWhiteList = dao.getWhiteListAp();
        lvWhiteList = (ListView) findViewById(R.id.lv_flag_list);
        mAdapter = new ListAdapter(this,mWhiteList);
        lvWhiteList.setAdapter(mAdapter);

        lvWhiteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWhiteList.this);
                String[] items = new String[] {"从白名单中移除","添加至黑名单","查看详细信息"};;
                dialog.setTitle(mWhiteList.get(position).getSSID());
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                dao.changeApFlag(mWhiteList.get(position).getBSSID(),0);
                                mWhiteList = dao.getWhiteListAp();
                                mAdapter.refreshList(mWhiteList);
                                break;
                            case 1:
                                dao.changeApFlag(mWhiteList.get(position).getBSSID(),1);
                                mWhiteList = dao.getWhiteListAp();
                                mAdapter.refreshList(mWhiteList);
                                break;
                        }
                    }
                });
                dialog.create().show();
            }
        });
    }
}
