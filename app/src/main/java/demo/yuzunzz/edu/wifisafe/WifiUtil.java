package demo.yuzunzz.edu.wifisafe;

/**
 * Created by 97349 on 2016/2/29.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.text.TextUtils;
import android.widget.Toast;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;

public class WifiUtil {
    private Context mContext;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mScanResultList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    private ConnectivityManager mConnectivityManager;
    // 定义一个WifiLock
    WifiLock mWifiLock;


    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }



    //    ScanResultPro mItemScanResultPro;
    ArrayList<ScanResultPro> mScanResultProList;

    // 构造器
    public WifiUtil(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        mContext = context;
    }



    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        } else {
            Toast.makeText(mContext, "wifi已开启", Toast.LENGTH_SHORT).show();
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else {
            Toast.makeText(mContext, "wifi已关闭", Toast.LENGTH_SHORT).show();
        }
    }

    // 检查当前WIFI状态
    // Field descriptor #21 I
//    public static final int WIFI_STATE_DISABLED = 1;

    // Field descriptor #21 I
//    public static final int WIFI_STATE_DISABLING = 0;

    // Field descriptor #21 I
//    public static final int WIFI_STATE_ENABLED = 3;

    // Field descriptor #21 I
//    public static final int WIFI_STATE_ENABLING = 2;

    // Field descriptor #21 I
//    public static final int WIFI_STATE_UNKNOWN = 4;
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mScanResultList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mScanResultList;
    }

    public ArrayList<ScanResultPro> getWifiListPro() {
        if (mScanResultList != null) {
            mScanResultProList = new ArrayList<ScanResultPro>();
            for (int i = 0; i < mScanResultList.size(); i++) {
                ScanResult scanResult = mScanResultList.get(i);
                ScanResultPro scanResultPro = new ScanResultPro(scanResult);
                mScanResultProList.add(scanResultPro);
            }

        }
        return mScanResultProList;

    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,
                true);
    }

    // 添加一个网络并连接
    public boolean addNetwork(WifiConfiguration wcg) {
        if (mWifiInfo != null){
            mWifiManager.disableNetwork(mWifiInfo.getNetworkId());
        }
        boolean flag = false;
        if (wcg.networkId > 0){
            flag = mWifiManager.enableNetwork(wcg.networkId,true);
            mWifiManager.updateNetwork(wcg);
        } else {
            int netId = mWifiManager.addNetwork(wcg);
            if (netId > 0){
                mWifiManager.saveConfiguration();
                flag = mWifiManager.enableNetwork(netId, true);
            }
        }
        return  flag;
    }

    public boolean removeWifi(int networkId){
        return mWifiManager.removeNetwork(networkId);

    }

    public static DhcpInfo getDhcpInfo(Context mContext){
        WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo di = wm.getDhcpInfo();
        return di;
    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    //实际应用方法
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs)
        {
            if (existingConfig.SSID.equals("\""+SSID+"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }

    //分为三种情况：1没有密码2用wep加密3用wpa加密


    public WifiInfo getConnectedWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public int getConnectStatus(String SSID){
        mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo != null){
            if(mWifiInfo.getSSID() != null && mWifiInfo.getSSID().equals(SSID)){
                int Ip = mWifiInfo.getIpAddress();
                String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
                if(mWifiInfo.getBSSID() != null && mWifiInfo.getSSID() != null && strIp != null && !strIp.equals("0.0.0.0")){
                    return 2;
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        }
        return 0;
    }


}
