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
    public ShowWifiInfoDialog() {
        // TODO Auto-generated constructor stub
    };

    FragmentActivity mActivity;

    public interface IRemoveWifi{

        public void onRemoveClick(int networkId);
    }

    IRemoveWifi mIRemoveWifi = null;

    WifiInfo mConnectedInfo;

    ScanResultPro mTemp;

    public static ShowWifiInfoDialog newInstance(IRemoveWifi mIRemoveWifi , WifiInfo mConnectedInfo ,ScanResultPro mTemp){

        ShowWifiInfoDialog mFragment = new ShowWifiInfoDialog();

        mFragment.mConnectedInfo = mConnectedInfo;

        mFragment.mTemp = mTemp;

        mFragment.mIRemoveWifi = mIRemoveWifi;

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
        TextView mStateTv = (TextView) view.findViewById(R.id.state_tv);
        String state = "";
        int Ip = mConnectedInfo.getIpAddress() ;
        String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
        if(mConnectedInfo.getSSID() != null && mConnectedInfo.getBSSID() != null && !strIp.equals("0.0.0.0")){
            mStateTv.setText("已连接");
        }else{
            mStateTv.setText("正在连接...");
        }
        TextView mSafetyTv = (TextView) view.findViewById(R.id.safety_tv);
        mSafetyTv.setText(mTemp.getCapabilities()+"  "+mTemp.getSafeLevel());

        TextView mLevelTv = (TextView) view.findViewById(R.id.level_tv);
        mLevelTv.setText(mConnectedInfo.getRssi() + "");

        TextView mSpeedTv = (TextView) view.findViewById(R.id.speed_tv);
        mSpeedTv.setText(mConnectedInfo.getLinkSpeed() + " Mbps");

        TextView mIpTv = (TextView) view.findViewById(R.id.ip_tv);
        mIpTv.setText(long2ip(mConnectedInfo.getIpAddress()));

        TextView mApMacTv = (TextView) view.findViewById(R.id.ap_mac_tv);
        mApMacTv.setText(mConnectedInfo.getBSSID());

        TextView mApFirmTv = (TextView) view.findViewById(R.id.ap_firm_tv);
        mApFirmTv.setText(mTemp.getFirm());

        TextView mNetMacTv = (TextView) view.findViewById(R.id.netcard_mac_tv);
        TextView mNetInterfaceTv = (TextView) view.findViewById(R.id.netcard_interface_tv);

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
            System.out.println("mNetMacTv:"+sb.toString()+"    "+mInterface.getDisplayName() + "/"+mInterface.getName());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        TextView mMaskTv = (TextView) view.findViewById(R.id.mask_tv);
        DhcpInfo mDhcpInfo = WifiUtil.getDhcpInfo(mActivity);
        mMaskTv.setText(long2ip(mDhcpInfo.netmask));
        TextView mGateWayTv = (TextView) view.findViewById(R.id.maskway_tv);
        mGateWayTv.setText(long2ip(mDhcpInfo.gateway));
        TextView mDns1Tv = (TextView) view.findViewById(R.id.dns1_tv);
        mDns1Tv.setText(long2ip(mDhcpInfo.dns1));
        TextView mDns2Tv = (TextView) view.findViewById(R.id.dns2_tv);
        mDns2Tv.setText(long2ip(mDhcpInfo.dns2));
        TextView mLastScanTime = (TextView) view.findViewById(R.id.tv_lastScanTime);
        mLastScanTime.setText(mTemp.getLastScanTime());


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());


        mBuilder.setView(view)
                .setTitle(mConnectedInfo.getSSID())
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .setPositiveButton("清除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mIRemoveWifi != null){
                            mIRemoveWifi.onRemoveClick(mConnectedInfo.getNetworkId());
                        }

                    }
                });

        return mBuilder.create();

    }

    public static void show(FragmentActivity mActivity, IRemoveWifi mIRemoveWifi , WifiInfo mWifiInfo , ScanResultPro mTemp){
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();

        Fragment mBefore = mActivity.getSupportFragmentManager().findFragmentByTag(ShowWifiInfoDialog.class.getSimpleName());

        if(mBefore != null){

            ((DialogFragment)mBefore).dismiss();

            ft.remove(mBefore);
        }
        ft.addToBackStack(null);

        DialogFragment mNow =  ShowWifiInfoDialog.newInstance(mIRemoveWifi , mWifiInfo , mTemp);

        mNow.show(ft, ShowWifiInfoDialog.class.getSimpleName());
    }

    public static String long2ip(long ip){
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
}
