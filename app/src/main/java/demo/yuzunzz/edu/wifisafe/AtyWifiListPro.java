package demo.yuzunzz.edu.wifisafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import demo.yuzunzz.edu.wifisafe.ShowWifiInfoDialog.IRemoveWifi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;
import demo.yuzunzz.edu.wifisafe.dao.Dao;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

/**
 * Created by 97349 on 2016/4/9.
 */
public class AtyWifiListPro extends FragmentActivity implements IRemoveWifi,OnRefreshListener{
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
    private SwipeRefreshLayout mSwipeLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_reslut_list);
        mWifiUtil = new WifiUtil(this);
        dao = new Dao(this);
        safeFilter = new SafeDirect(this);

        mWifiUtil.startScan();
        mResultList = mWifiUtil.getWifiListPro();
        mResultList = safeFilter.safeDirect(mResultList);


        for (int i = 0; i < mResultList.size(); i++){
            ScanResultPro scanResultPro = mResultList.get(i);
//            System.out.println(i+"*****"+scanResultPro.getSSID()+"*****"+scanResultPro.getFlag());
        }


        lvResultList = (ListView) findViewById(R.id.lv_scan_result_list);
        mAdapter = new ScanResultProAdapter(this,mResultList,mWifiUtil);
        lvResultList.setAdapter(mAdapter);

        /*
        列表项点击事件添加
         */
        lvResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

				ScanResultPro mTemp = mResultList.get(position);
				WifiInfo mInfo = mWifiUtil.getConnectedWifiInfo();

                if( mTemp != null) {
                    if (mInfo != null) {
                        if (mInfo.getSSID() != null && (mInfo.getSSID().equals(mTemp.getSSID()) || mInfo.getSSID().equals("\"" + mTemp.getSSID() + "\""))) {
                            ShowWifiInfoDialog.show(AtyWifiListPro.this, AtyWifiListPro.this, mInfo, mTemp);
                        } else {
                            connectAP(mTemp);
                        }
                    } else {
                        connectAP(mTemp);
                    }
                }

            }
        });

        /*
        列表项长按事件添加
         */
        lvResultList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
                String[] items = new String[] {"添加至黑名单","添加至白名单","查看详细信息"};;
                dialog.setTitle(mResultList.get(position).getSSID());
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                dao.changeApFlag(mResultList.get(position).getBSSID(),1);
                                Toast.makeText(AtyWifiListPro.this,"成功添加AP "+mResultList.get(position).getSSID()+" 至黑名单",Toast.LENGTH_SHORT).show();

                                mWifiUtil.startScan();
                                mResultList = mWifiUtil.getWifiListPro();
                                mResultList = safeFilter.safeDirect(mResultList);

                                mAdapter.refreshResultList(mResultList);
                                break;
                            case 1:
                                dao.changeApFlag(mResultList.get(position).getBSSID(),2);
                                Toast.makeText(AtyWifiListPro.this,"成功添加AP "+mResultList.get(position).getSSID()+" 至白名单",Toast.LENGTH_SHORT).show();

                                mWifiUtil.startScan();
                                mResultList = mWifiUtil.getWifiListPro();
                                mResultList = safeFilter.safeDirect(mResultList);

                                mAdapter.refreshResultList(mResultList);
                                break;
                            case 2:
                                ScanResultPro mTemp = mResultList.get(position);
                                WifiInfo mInfo = mWifiUtil.getConnectedWifiInfo();
                                if (mInfo.getSSID() != null && (mInfo.getSSID().equals(mTemp.getSSID()) || mInfo.getSSID().equals("\"" + mTemp.getSSID() + "\""))) {
                                    ShowWifiInfoDialog.show(AtyWifiListPro.this, AtyWifiListPro.this, mInfo, mTemp);
                                } else {
                                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(AtyWifiListPro.this);
                                    dialog1.setTitle(mTemp.getSSID());
                                    dialog1.setMessage("状态消息\t\t未连接\n"+
                                            "安全性\t\t"+mTemp.getCapabilities()+"\t\t"+mTemp.getSafeLevel()+"\n"
                                            +"信号强度\t\t"+mTemp.getLevel()+"\n"
                                            +"AP MAC\t\t"+mTemp.getBSSID()+"\n"
                                            +"AP 型号\t\t"+mTemp.getFirm()+"\n"
                                            +"此AP上一次被搜索到的时间\n"+mTemp.getLastScanTime());
                                    dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    dialog1.create().show();
                                }
                                break;
                        }


                    }
                });
                dialog.create().show();
                return true;
            }
        });

//        btnShowPwd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
//                dialog.setTitle("查看已保存的密码");
//                dialog.setMessage("请确认您已授权应用使用root权限");
//                dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        try {
//                            mAdapter.refreshPwdList(WifiPwdUtil.Read());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                dialog.setNeutralButton("隐藏", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mAdapter.hidePwdList();
//                    }
//                });
//                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(AtyWifiListPro.this,"取消查看",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                dialog.create().show();
//            }
//        });

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);


    }



//    public void checkWeakPassword(String pwd){
//        String level = "";
//
//        Toast.makeText(AtyWifiListPro.this,pwd + level,Toast.LENGTH_LONG).show();
//    }

//    static final String VERY_WEAK="非常弱";
//    static final String WEAK="弱";
//    static final String AVERAGE=" 一般";
//    static final String STRONG="强";
//    static final String VERY_STRONG="非常强";
//    static final String SECURE="安全";
//    static final String VERY_SECURE="非常安全";

    public void checkWeakPassword(String pwd) {
//        String safelevel = VERY_WEAK;
        int grade = 0;
        int index = 0;
        char[] pPsdChars = pwd.toCharArray();

        int numIndex = 0;
        int sLetterIndex = 0;
        int lLetterIndex = 0;
        int symbolIndex = 0;

        for (char pPsdChar : pPsdChars) {
            int ascll = pPsdChar;
            /*
             * 数字 48-57 A-Z 65 - 90 a-z 97 - 122 !"#$%&'()*+,-./ (ASCII码：33~47)
             * :;<=>?@ (ASCII码：58~64) [\]^_` (ASCII码：91~96) {|}~
             * (ASCII码：123~126)
             */
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
        /*
         * 一、密码长度: 5 分: 小于等于 4 个字符 10 分: 5 到 7 字符 25 分: 大于等于 8 个字符
         */
        if (pPsdChars.length <= 4) {
            index = 5;
        } else if (pPsdChars.length <= 7) {
            index = 10;
        } else {
            index = 25;
        }
        grade += index;

        /*
         * 二、字母: 0 分: 没有字母 10 分: 全都是小（大）写字母 20 分: 大小写混合字母
         */
        if (lLetterIndex == 0 && sLetterIndex == 0) {
            index = 0;
        } else if (lLetterIndex != 0 && sLetterIndex != 0) {
            index = 20;
        } else {
            index = 10;
        }
        grade += index;
        /*
         * 三、数字: 0 分: 没有数字 10 分: 1 个数字 20 分: 大于 1 个数字
         */
        if (numIndex == 0) {
            index = 0;
        } else if (numIndex == 1) {
            index = 10;
        } else {
            index = 20;
        }
        grade += index;

        /*
         * 四、符号: 0 分: 没有符号 10 分: 1 个符号 25 分: 大于 1 个符号
         */
        if (symbolIndex == 0) {
            index = 0;
        } else if (symbolIndex == 1) {
            index = 10;
        } else {
            index = 25;
        }
        grade += index;
        /*
         * 五、奖励: 2 分: 字母和数字 3 分: 字母、数字和符号 5 分: 大小写字母、数字和符号
         */
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

        /*
         * 最后的评分标准: >= 90: 非常安全 >= 80: 安全（Secure） >= 70: 非常强 >= 60: 强（Strong） >=
         * 50: 一般（Average） >= 25: 弱（Weak） >= 0: 非常弱
         */
//        if(grade >=90){
//            safelevel = VERY_SECURE;
//        }else if(grade >= 80){
//            safelevel = SECURE;
//        }else if(grade >= 70){
//            safelevel = VERY_STRONG;
//        }else if(grade >= 60){
//            safelevel = STRONG;
//        }else if(grade >= 50){
//            safelevel = AVERAGE;
//        }else if(grade >= 25){
//            safelevel = WEAK;
//        }else if(grade >= 0){
//            safelevel = VERY_WEAK;
//        }
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


    @Override
    public void onRemoveClick(int networkId) {
        mWifiUtil.removeWifi(networkId);
        refresh = new Thread() {
            @Override
            public void run() {
                Message message = handler.obtainMessage(2);
                handler.sendMessage(message);
                handler.postDelayed(this, 1000);
            }
        };
        refresh.start();
    }
	
	public void connectAP(final ScanResultPro mTemp){
		if (!mTemp.getCapabilities().contains("OPEN")){
			AlertDialog.Builder dialog = new AlertDialog.Builder(AtyWifiListPro.this);
			dialog.setTitle(mTemp.getSSID());
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
			final ScanResultPro mTemp1 = mTemp;
			dialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mTemp1.getCapabilities().contains("WEP")){
							mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mTemp1.getSSID(),String.valueOf(etConnectPwd.getText()),2));
						} else {
							mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mTemp1.getSSID(),String.valueOf(etConnectPwd.getText()),3));
						}
                        check = new Thread() {
                            @Override
                            public void run() {
                                String info[] = new String[]{mTemp.getSSID(),String.valueOf(etConnectPwd.getText())};
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
		} else {
//                    mWifiUtil.addNetwork(mWifiUtil.CreateWifiInfo(mResultList.get(position).getSSID(),"",1));
//                    System.out.println(".......");
			Toast.makeText(AtyWifiListPro.this,"o00ps!!!!",Toast.LENGTH_SHORT).show();
		}
	}

    int count1 = 0;
    int count2 = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:

                    count1++;
                    String info[] = (String[])msg.obj;
                    if (count1 == 10) {
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
                        refresh = new Thread() {
                            @Override
                            public void run() {
                                Message message = handler.obtainMessage(2);
                                handler.sendMessage(message);
                                handler.postDelayed(this, 500);
                            }
                        };
                        refresh.start();
                    }
                    Log.d("ipipipipi",mConnectedInfo.getSSID() + "   "+ mConnectedInfo.getBSSID() + "   "+ strIp);
                    mWifiUtil.startScan();
                    mResultList = mWifiUtil.getWifiListPro();
                    mResultList = safeFilter.safeDirect(mResultList);
                    mAdapter.refreshResultList(mResultList);
                    break;
                case 2:
                    count2++;
                    if (count2 == 2) {
                        handler.removeCallbacks(refresh);
                        count2 = 0;
                    }
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
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(3, 1200);
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what) {
                case 3:
                    mWifiUtil.startScan();
                    mResultList = mWifiUtil.getWifiListPro();
                    mResultList = safeFilter.safeDirect(mResultList);
                    mAdapter.refreshResultList(mResultList);
                    mSwipeLayout.setRefreshing(false);
                    break;
                default:
                    break;
            }
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
}
