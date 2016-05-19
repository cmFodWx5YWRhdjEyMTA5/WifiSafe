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
public class AtyBlackList extends Activity {

    private ListView lvBlackList;
    private List<ScanResultPro> mBlackList;
    private ListAdapter mAdapter;
    private Dao dao;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flag_list);
        dao = new Dao(this);
        mBlackList = dao.getBlackListAp();
        lvBlackList = (ListView) findViewById(R.id.lv_flag_list);
        mAdapter = new ListAdapter(this,mBlackList);
        lvBlackList.setAdapter(mAdapter);

        lvBlackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AtyBlackList.this);
                String[] items = new String[] {"从黑名单中移除","添加至白名单","查看详细信息"};;
                dialog.setTitle(mBlackList.get(position).getSSID());
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                dao.changeApFlag(mBlackList.get(position).getBSSID(),0);
                                mBlackList = dao.getBlackListAp();
                                mAdapter.refreshList(mBlackList);
                                break;
                            case 1:
                                dao.changeApFlag(mBlackList.get(position).getBSSID(),2);
                                mBlackList = dao.getBlackListAp();
                                mAdapter.refreshList(mBlackList);
                                break;
                            case 2:
                                AlertDialog.Builder dialog1 = new AlertDialog.Builder(AtyBlackList.this);
                                dialog1.setTitle(mBlackList.get(position).getSSID());
                                dialog1.setMessage("状态消息\t\t禁止连接\n"
                                        +"AP MAC\t\t"+mBlackList.get(position).getBSSID()+"\n"
                                        +"AP 型号\t\t"+mBlackList.get(position).getFirm()+"\n"
                                        +"此AP上一次被搜索到的时间\n"+mBlackList.get(position).getLastScanTime());
                                dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                dialog1.create().show();
                        }
                    }
                });
                dialog.create().show();
            }
        });



    }
}
