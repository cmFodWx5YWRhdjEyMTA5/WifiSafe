package demo.yuzunzz.edu.wifisafe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;
import demo.yuzunzz.edu.wifisafe.dao.Dao;


/**
 * Created by 97349 on 2016/4/9.
 */
public class AtyWifiListPro extends FragmentActivity{
    private ListView lvResultList;
    private List<ScanResultPro> mResultList;
    private ScanResultProAdapter mAdapter;
    private WifiUtil mWifiUtil;
    private EditText etConnectPwd;
    private CheckBox cbCheckPwd;
    private Dao dao;
    private Thread check = null;
    private Thread refresh = null;
    private SafeDirect safeFilter;

    final int CONNECT_CHECK = 1;
    final int REFRESH = 2;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_reslut_list);
        mWifiUtil = new WifiUtil(this);
        dao = new Dao(this);
        safeFilter = new SafeDirect(this);

        mWifiUtil.startScan();
        mResultList = mWifiUtil.getWifiListPro();
        mResultList = safeFilter.safeDirect(mResultList);


        lvResultList = (ListView) findViewById(R.id.lv_scan_result_list);
        mAdapter = new ScanResultProAdapter(this,mResultList,mWifiUtil);
        lvResultList.setAdapter(mAdapter);


        lvResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

				ScanResultPro mScanResultPro = mResultList.get(position);
				WifiInfo mInfo = mWifiUtil.getConnectedWifiInfo();

                if( mScanResultPro != null) {
                    if (mInfo != null) {
                        if (mInfo.getSSID() != null && (mInfo.getSSID().equals(mScanResultPro.getSSID()) || mInfo.getSSID().equals("\"" + mScanResultPro.getSSID() + "\""))) {
                            int Ip = mInfo.getIpAddress() ;
                            String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
                            if(!strIp.equals("0.0.0.0")){
                                ShowWifiInfoDialog.show(AtyWifiListPro.this, mInfo, mScanResultPro, mWifiUtil);
                            }
                        } else {
                            connectAP(mScanResultPro);
                        }
                    } else {
                        connectAP(mScanResultPro);
                    }
                }


            }
        });


        lvResultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
                String[] items = new String[] {"添加至黑名单","添加至白名单","查看详细信息"};
                dialog.setTitle(mResultList.get(position).getSSID());
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                dao.changeApFlag(mResultList.get(position).getBSSID(),1);
                                Toast.makeText(AtyWifiListPro.this,"成功添加AP "+mResultList.get(position).getSSID()+" 至黑名单",Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                dao.changeApFlag(mResultList.get(position).getBSSID(),2);
                                Toast.makeText(AtyWifiListPro.this,"成功添加AP "+mResultList.get(position).getSSID()+" 至白名单",Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                ScanResultPro mScanResultPro = mResultList.get(position);
                                WifiInfo mInfo = mWifiUtil.getConnectedWifiInfo();
                                if (mInfo.getSSID() != null && (mInfo.getSSID().equals(mScanResultPro.getSSID()) || mInfo.getSSID().equals("\"" + mScanResultPro.getSSID() + "\""))) {
                                    ShowWifiInfoDialog.show(AtyWifiListPro.this, mInfo, mScanResultPro, mWifiUtil);
                                } else {
                                    showSimpleInfoDialog(mScanResultPro);
                                }
                                break;
                        }


                    }
                });
                dialog.create().show();
                return true;
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(refresh);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(refresh);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh = new Thread() {
            @Override
            public void run() {
                Message message = handler.obtainMessage(REFRESH);
                handler.sendMessage(message);
                handler.postDelayed(this, 1500);
            }
        };
        refresh.start();
    }

    public void checkWeakPassword(String pwd) {
        int grade = 0;
        char[] pPsdChars = pwd.toCharArray();

        int index = 0;
        int numIndex = 0;
        int sLetterIndex = 0;
        int lLetterIndex = 0;
        int symbolIndex = 0;

        for (char pPsdChar : pPsdChars) {
            int ascll = pPsdChar;
            if (ascll >= 48 && ascll <= 57) {
                numIndex++;
            } else if (ascll >= 65 && ascll <= 90) {
                lLetterIndex++;
            } else if (ascll >= 97 && ascll <= 122) {
                sLetterIndex++;
            } else if ((ascll >= 33 && ascll <= 47)
                    || (ascll >= 58 && ascll <= 64)
                    || (ascll >= 91 && ascll <= 96)
                    || (ascll >= 123 && ascll <= 126)) {
                symbolIndex++;
            }
        }

        if (pPsdChars.length <= 4) {
            index = 5;
        } else if (pPsdChars.length <= 7) {
            index = 10;
        } else {
            index = 25;
        }
        grade += index;

        if (lLetterIndex == 0 && sLetterIndex == 0) {
            index = 0;
        } else if (lLetterIndex != 0 && sLetterIndex != 0) {
            index = 20;
        } else {
            index = 10;
        }
        grade += index;

        if (numIndex == 0) {
            index = 0;
        } else if (numIndex == 1) {
            index = 10;
        } else {
            index = 20;
        }
        grade += index;

        if (symbolIndex == 0) {
            index = 0;
        } else if (symbolIndex == 1) {
            index = 10;
        } else {
            index = 25;
        }
        grade += index;

        if ((sLetterIndex != 0 || lLetterIndex != 0) && numIndex != 0) {
            index = 2;
        } else if ((sLetterIndex != 0 || lLetterIndex != 0) && numIndex != 0
                && symbolIndex != 0) {
            index = 3;
        } else if (sLetterIndex != 0 && lLetterIndex != 0 && numIndex != 0
                && symbolIndex != 0) {
            index = 5;
        }
        grade += index;

        if (dao.isPwdExist(pwd)){
            grade = 666666;
        }

        if (grade<50){
            AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
            dialog.setTitle("警告");
            dialog.setMessage("检测到您所连接的AP密码强度较弱，请注意上网安全");
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.create().show();
        }

        if (grade==666666){
            AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
            dialog.setTitle("警告");
            dialog.setMessage("检测到您所连接的AP密码为弱口令，请注意上网安全");
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.create().show();
        }
    }

	public void connectAP(final ScanResultPro mScanResultPro) {
        List<WifiConfiguration> mList = mWifiUtil.getWifiConfiguration();
        boolean flag = false;

        if (mList == null || mList.isEmpty()) {
            if (mScanResultPro.getCapabilities().equals("OPEN")) {
                mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mScanResultPro.getSSID(), "", 1));
            } else {
                connectDialog(mScanResultPro);
            }
        } else {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).SSID.equals("\"" + mScanResultPro.getSSID() + "\"")) {
                    mWifiUtil.addNetwork(mList.get(i));
                    flag = true;
                }
            }

            if (!flag){
                if (mScanResultPro.getCapabilities().equals("OPEN")) {
                    mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mScanResultPro.getSSID(), "", 1));
                } else {
                    connectDialog(mScanResultPro);
                }
            }
        }
    }

    public void connectDialog(final ScanResultPro mScanResultPro){
        AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
        dialog.setTitle(mScanResultPro.getSSID());
        View v = AtyWifiListPro.this.getLayoutInflater().inflate(R.layout.connect_dialog,null);
        dialog.setView(v);
        etConnectPwd = (EditText) v.findViewById(R.id.et_connect_pwd);
        cbCheckPwd = (CheckBox) v.findViewById(R.id.cb_check_pwd);
        cbCheckPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbCheckPwd.isChecked()){
                    etConnectPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    etConnectPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        dialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mScanResultPro.getCapabilities().contains("WEP")){
                    mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mScanResultPro.getSSID(),String.valueOf(etConnectPwd.getText()),2));
                } else {
                    mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mScanResultPro.getSSID(),String.valueOf(etConnectPwd.getText()),3));
                }
                check = new Thread() {
                    @Override
                    public void run() {
                        String info[] = new String[]{mScanResultPro.getSSID(),String.valueOf(etConnectPwd.getText())};
                        Message message = handler.obtainMessage(1, info);
                        handler.sendMessage(message);
                        handler.postDelayed(this, 1000);
                    }
                };
                check.start();
            }
        });
        dialog.setNegativeButton("取消",null);
        dialog.create().show();
    }

    int count = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONNECT_CHECK:
                    count++;
                    String info[] = (String[])msg.obj;
                    if (count == 10) {
                        count = 0;
                        handler.removeCallbacks(check);
                        Toast.makeText(AtyWifiListPro.this,"验证出错，连接失败",Toast.LENGTH_LONG).show();
                    }
                    WifiInfo mConnectedInfo = mWifiUtil.getConnectedWifiInfo();
                    int Ip = mConnectedInfo.getIpAddress() ;
                    String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
                    if(mConnectedInfo.getSSID() != null && mConnectedInfo.getBSSID() != null && !strIp.equals("0.0.0.0")){
                        Toast.makeText(AtyWifiListPro.this,"连接"+info[0]+"成功",Toast.LENGTH_SHORT).show();
                        checkWeakPassword(info[1]);
                        handler.removeCallbacks(check);
                        count = 0;
                    }
                    break;
                case REFRESH:
                    mWifiUtil.startScan();
                    mResultList = mWifiUtil.getWifiListPro();
                    mResultList = safeFilter.safeDirect(mResultList);
                    mAdapter.refreshResultList(mResultList);
                    break;
            }
            super.handleMessage(msg);
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(1,Menu.FIRST,Menu.FIRST,"显示密码");
        menu.add(1,Menu.FIRST+1,Menu.FIRST+1,"隐藏密码");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
                dialog.setTitle("查看已保存的密码");
                dialog.setMessage("请确认您已授权应用使用root权限");
                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mAdapter.refreshPwdList(WifiPwdUtil.Read());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AtyWifiListPro.this,"取消查看",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.create().show();
                break;
            case 2:
                mAdapter.hidePwdList();
                break;
        }
        return true;
    }

    
    public void showSimpleInfoDialog(ScanResultPro scanResultPro){
        AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
        dialog.setTitle(scanResultPro.getSSID());
        dialog.setMessage("状态消息\t\t未连接\n"+
                "安全性\t\t"+scanResultPro.getCapabilities()+"\t\t"+scanResultPro.getSafeLevel()+"\n"
                +"信号强度\t\t"+scanResultPro.getLevel()+"\n"
                +"AP MAC\t\t"+scanResultPro.getBSSID()+"\n"
                +"AP 型号\t\t"+scanResultPro.getFirm()+"\n"
                +"此AP上一次被搜索到的时间\n"+scanResultPro.getLastScanTime());
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.create().show();
    }

}
