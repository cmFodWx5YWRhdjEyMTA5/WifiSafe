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
import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;
import demo.yuzunzz.edu.wifisafe.dao.Dao;

/**
 * Created by 97349 on 2016/4/9.
 */
public class AtyWifiListPro extends FragmentActivity implements IRemoveWifi{
    private ListView lvResultList;
    private List<ScanResultPro> mResultList;
    private ScanResultProAdapter mAdapter;
    private Button btnShowPwd;
    private WifiUtil mWifiUtil;
    private EditText etConnectPwd;
    private CheckBox cbCheckPwd;
    private Dao dao;
    private int count = 0;
    private Thread check = null;
    private SafeDirect safeFilter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_reslut_list);
        btnShowPwd = (Button) findViewById(R.id.btn_show_pwd);
        mWifiUtil = new WifiUtil(this);
        dao = new Dao(this);
        safeFilter = new SafeDirect(this);

        mWifiUtil.startScan();
        mResultList = mWifiUtil.getWifiListPro();
        mResultList = safeFilter.safeDirect(mResultList);


        for (int i = 0; i < mResultList.size(); i++){
            ScanResultPro scanResultPro = mResultList.get(i);
            System.out.println(i+"*****"+scanResultPro.getSSID()+"*****"+scanResultPro.getFlag());
        }


        lvResultList = (ListView) findViewById(R.id.lv_scan_result_list);
        mAdapter = new ScanResultProAdapter(this,mResultList);
        lvResultList.setAdapter(mAdapter);

        /*
        列表项点击事件添加
         */
        lvResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

				ScanResultPro mTemp = mResultList.get(position);
				WifiInfo mInfo = mWifiUtil.getConnectedWifiInfo();
				
                if (mInfo != null && mInfo.getBSSID().equals(mTemp.getBSSID())){
					ShowWifiInfoDialog.show(AtyWifiListPro.this, AtyWifiListPro.this, mInfo, mTemp.getCapabilities());
				} else {
					connectAP(mTemp);
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
//                                Toast.makeText(AtyWifiListPro.this,dao.getFirm(mResultList.get(position).getBSSID()),Toast.LENGTH_SHORT).show();
                                ShowWifiInfoDialog.show(AtyWifiListPro.this, AtyWifiListPro.this, mInfo, mTemp.getCapabilities());
                                break;
                        }


                    }
                });
                dialog.create().show();
                return true;
            }
        });

        btnShowPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                dialog.setNeutralButton("隐藏", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.hidePwdList();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AtyWifiListPro.this,"取消查看",Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.create().show();
            }
        });


    }



    public void checkWeakPassword(String pwd){
        String level = "";
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (pwd.matches(regexZ) || pwd.matches(regexS) || pwd.matches(regexT)) level = "weak";
        if (pwd.matches(regexZT) || pwd.matches(regexST) || pwd.matches(regexZS)) level = "middle";
        if (pwd.matches(regexZST)) level = "strong";
        Toast.makeText(AtyWifiListPro.this,pwd + level,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRemoveClick(int networkId) {
        mWifiUtil.removeWifi(networkId);
        mWifiUtil.startScan();
    }
	
	public void connectAP(ScanResultPro mTemp){
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
}
