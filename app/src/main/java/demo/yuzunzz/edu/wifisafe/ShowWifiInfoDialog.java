package demo.yuzunzz.edu.wifisafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;


/**
 * Created by 97349 on 2016/5/18.
 */
public class ShowWifiInfoDialog extends DialogFragment {
    private FragmentActivity mActivity;
    private WifiInfo mConnectedInfo;
    private ScanResultPro mScanResultPro;
    private WifiUtil mWifiUtil;

    public ShowWifiInfoDialog() {
    }

    public static ShowWifiInfoDialog newInstance(WifiInfo mConnectedInfo ,ScanResultPro mScanResultPro, WifiUtil mWifiUtil){

        ShowWifiInfoDialog mFragment = new ShowWifiInfoDialog();
        mFragment.mConnectedInfo = mConnectedInfo;
        mFragment.mScanResultPro = mScanResultPro;
        mFragment.mWifiUtil = mWifiUtil;
        return mFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivity = (FragmentActivity) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.show_connected_wifi_page,null);
        TextView mStateTv = (TextView) view.findViewById(R.id.tv_scwp_state);
        int Ip = mConnectedInfo.getIpAddress() ;
        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
        if(mConnectedInfo.getSSID() != null && mConnectedInfo.getBSSID() != null && !strIp.equals("0.0.0.0")){
            mStateTv.setText("已连接");
        }else{
            mStateTv.setText("正在连接...");
        }
        TextView mSafetyTv = (TextView) view.findViewById(R.id.tv_scwp_safety);
        mSafetyTv.setText(mScanResultPro.getCapabilities()+"  "+mScanResultPro.getSafeLevel());

        TextView mLevelTv = (TextView) view.findViewById(R.id.tv_scwp_level);
        mLevelTv.setText(mConnectedInfo.getRssi() + "");

        TextView mSpeedTv = (TextView) view.findViewById(R.id.tv_scwp_speed);
        mSpeedTv.setText(mConnectedInfo.getLinkSpeed() + " Mbps");

        TextView mIpTv = (TextView) view.findViewById(R.id.tv_scwp_ip);
        mIpTv.setText(mWifiUtil.long2ip(mConnectedInfo.getIpAddress()));

        TextView mApMacTv = (TextView) view.findViewById(R.id.tv_scwp_ap_mac);
        mApMacTv.setText(mConnectedInfo.getBSSID());

        TextView mApFirmTv = (TextView) view.findViewById(R.id.tv_scwp_ap_firm);
        mApFirmTv.setText(mScanResultPro.getFirm());

        TextView mNetMacTv = (TextView) view.findViewById(R.id.tv_scwp_netcard_mac);
        TextView mNetInterfaceTv = (TextView) view.findViewById(R.id.tv_scwp_netcard_interface);

        try {
            Field mField = mConnectedInfo.getClass().getDeclaredField("mIpAddress");
            mField.setAccessible(true);
            InetAddress mInetAddr = (InetAddress) mField.get(mConnectedInfo);
            NetworkInterface mInterface = NetworkInterface.getByInetAddress(mInetAddr);
            byte[] mac = mInterface.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i<mac.length;i++){
                sb.append(String.format("%02X%s", mac[i],(i<mac.length-1)?":":""));
            }
            mNetMacTv.setText(sb.toString());
            mNetInterfaceTv.setText(mInterface.getDisplayName() + "/"+mInterface.getName());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        TextView mMaskTv = (TextView) view.findViewById(R.id.tv_scwp_mask);
        DhcpInfo mDhcpInfo = mWifiUtil.getDhcpInfo();
        mMaskTv.setText(mWifiUtil.long2ip(mDhcpInfo.netmask));
        TextView mGateWayTv = (TextView) view.findViewById(R.id.tv_scwp_maskway);
        mGateWayTv.setText(mWifiUtil.long2ip(mDhcpInfo.gateway));
        TextView mDns1Tv = (TextView) view.findViewById(R.id.tv_scwp_dns1);
        mDns1Tv.setText(mWifiUtil.long2ip(mDhcpInfo.dns1));
        TextView mDns2Tv = (TextView) view.findViewById(R.id.tv_scwp_dns2);
        mDns2Tv.setText(mWifiUtil.long2ip(mDhcpInfo.dns2));
        TextView mLastScanTime = (TextView) view.findViewById(R.id.tv_scwp_lastScanTime);
        mLastScanTime.setText(mScanResultPro.getLastScanTime());

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

        mBuilder.setView(view)
                .setTitle(mConnectedInfo.getSSID())
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("断开", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mWifiUtil.disconnectWifi(mConnectedInfo.getNetworkId());
                    }
                })
                .setPositiveButton("清除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mWifiUtil.removeWifi(mConnectedInfo.getNetworkId());
                    }
                });

        return mBuilder.create();

    }

    public static void show(FragmentActivity mActivity,  WifiInfo mWifiInfo , ScanResultPro mScanResultPro,  WifiUtil mWifiUtil){
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment mBefore = mActivity.getSupportFragmentManager().findFragmentByTag(ShowWifiInfoDialog.class.getSimpleName());
        if(mBefore != null){
            ((DialogFragment)mBefore).dismiss();
            ft.remove(mBefore);
        }
        ft.addToBackStack(null);
        DialogFragment mNow =  ShowWifiInfoDialog.newInstance( mWifiInfo , mScanResultPro , mWifiUtil);
        mNow.show(ft, ShowWifiInfoDialog.class.getSimpleName());
    }
}
