package demo.yuzunzz.edu.wifisafe;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;

/**
 * Created by 97349 on 2016/4/9.
 */
public class ScanResultProAdapter extends BaseAdapter {
    private List<ScanResultPro> mList = new ArrayList<ScanResultPro>();
    List<WifiPwdUtil.WifiInfomation> mWifiPwdInfo = new ArrayList<WifiPwdUtil.WifiInfomation>();
    Map<String , String> mWifiPwdMap = new HashMap<String, String>();
    WifiInfo mInfo;
    WifiUtil wifiUtil;
    private LayoutInflater mInflater;

    public ScanResultProAdapter(Context context, List<ScanResultPro> list,WifiUtil wifiUtil){
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
        this.wifiUtil = wifiUtil;
        this.mInfo = wifiUtil.getConnectedWifiInfo();
    }

    public void refreshResultList(List<ScanResultPro> list){
        if(mList == null)
            return;
        this.mList = list;
        this.mInfo = wifiUtil.getConnectedWifiInfo();
        notifyDataSetChanged();
    }

    public void refreshPwdList(List<WifiPwdUtil.WifiInfomation> mResult){
        if(mResult == null)
            return;
        mWifiPwdInfo.clear();
        mWifiPwdInfo.addAll(mResult);
        mWifiPwdMap.clear();
        for(int i = 0 ; i < mWifiPwdInfo.size() ; i++){
            if(mWifiPwdInfo.get(i) != null){
                mWifiPwdMap.put(mWifiPwdInfo.get(i).Ssid, mWifiPwdInfo.get(i).Password);
            }
        }
        notifyDataSetChanged();
    }

    public void hidePwdList(){
        mWifiPwdInfo.clear();
        mWifiPwdMap.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.scan_result_item, null);
        }

        ScanResultPro scanResultPro = mList.get(position);
        TextView tvSsid = (TextView) convertView.findViewById(R.id.tv_sri_ssid);
        tvSsid.setText(scanResultPro.getSSID());

        TextView tvLevel = (TextView) convertView.findViewById(R.id.tv_sri_level);
        tvLevel.setText(String.valueOf(scanResultPro.getLevel()));

        TextView tvSafeLevel = (TextView) convertView.findViewById(R.id.tv_sri_safe_level);
        tvSafeLevel.setText(String.valueOf(scanResultPro.getSafeLevel()));

        TextView tvEncrypt = (TextView) convertView.findViewById(R.id.tv_sri_encrypt);
        tvEncrypt.setText(String.valueOf(scanResultPro.getCapabilities()));

        TextView tvPwd = (TextView) convertView.findViewById(R.id.tv_sri_pwd);
        if(mWifiPwdMap.containsKey(scanResultPro.getSSID())){
            tvPwd.setVisibility(View.VISIBLE);
            tvPwd.setText(mWifiPwdMap.get(scanResultPro.getSSID()));
            tvPwd.setTextColor(ColorStateList.valueOf(Color.RED));
        }else{
            tvPwd.setVisibility(View.GONE);
        }

        TextView tvConnect = (TextView) convertView.findViewById(R.id.tv_sri_connect);
        if(mInfo != null){
            if(mInfo.getSSID() != null && mInfo.getSSID().equals("\"" + scanResultPro.getSSID() + "\"")){
                tvConnect.setVisibility(View.VISIBLE);
                int Ip = mInfo.getIpAddress() ;
                String strIp = "" + (Ip & 0xFF) + "." + ((Ip >> 8) & 0xFF) + "." + ((Ip >> 16) & 0xFF) + "." + ((Ip >> 24) & 0xFF);
                if(mInfo.getBSSID() != null && mInfo.getSSID() != null && strIp != null && !strIp.equals("0.0.0.0")){
                    tvConnect.setText("已连接");
                }else{
                    tvConnect.setText("正在连接...");
                }
            }else{
                tvConnect.setVisibility(View.GONE);
            }
        }else{
            tvConnect.setVisibility(View.GONE);
        }
        return convertView;
    }
}
