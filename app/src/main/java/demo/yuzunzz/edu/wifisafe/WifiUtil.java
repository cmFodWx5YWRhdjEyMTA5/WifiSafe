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
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<WifiConfiguration> mWifiConfiguration;
    private List<ScanResult> mScanResultList;
    private ArrayList<ScanResultPro> mScanResultProList;

    public WifiUtil(Context context) {
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        mContext = context;
    }

    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        } else {
            Toast.makeText(mContext, "wifi已开启", Toast.LENGTH_SHORT).show();
        }
    }

    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else {
            Toast.makeText(mContext, "wifi已关闭", Toast.LENGTH_SHORT).show();
        }
    }


	// public static final int WIFI_STATE_DISABLED = 1;
	// public static final int WIFI_STATE_DISABLING = 0;
	// public static final int WIFI_STATE_ENABLED = 3;
    // public static final int WIFI_STATE_ENABLING = 2;
    // public static final int WIFI_STATE_UNKNOWN = 4;
    public int checkState() {
        return mWifiManager.getWifiState();
    }


    public void startScan() {
        mWifiManager.startScan();
        mScanResultList = mWifiManager.getScanResults();
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }

    public ArrayList<ScanResultPro> getWifiListPro() {
        if (mScanResultList != null) {
            mScanResultProList = new ArrayList<>();
            for (int i = 0; i < mScanResultList.size(); i++) {
                ScanResult scanResult = mScanResultList.get(i);
                ScanResultPro scanResultPro = new ScanResultPro(scanResult);
                mScanResultProList.add(scanResultPro);
            }

        }
        return mScanResultProList;
    }

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

    public boolean removeWifi(int netId){
        return mWifiManager.removeNetwork(netId);

    }

    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public DhcpInfo getDhcpInfo(){
        return mWifiManager.getDhcpInfo();
    }

    public WifiInfo getConnectedWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public String long2ip(long ip){
        StringBuffer sb=new StringBuffer();
        sb.append(String.valueOf((int)(ip&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>8)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>16)&0xff)));
        sb.append('.');
        sb.append(String.valueOf((int)((ip>>24)&0xff)));
        return sb.toString();
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
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
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


}
