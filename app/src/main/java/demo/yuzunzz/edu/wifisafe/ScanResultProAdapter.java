package demo.yuzunzz.edu.wifisafe;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private LayoutInflater mInflater;

    public ScanResultProAdapter(Context context, List<ScanResultPro> list){
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    public void refreshResultList(List<ScanResultPro> list){
        if(mList == null)
            return;
        this.mList = list;
        notifyDataSetChanged();
    }

    public void refreshPwdList(List<WifiPwdUtil.WifiInfomation> mResult){
        if(mResult == null)
            return;
        mWifiPwdInfo.clear();
        mWifiPwdInfo.addAll(mResult);
        mWifiPwdMap.clear();
        for(int i = 0 ; i < mWifiPwdInfo.size() ; i++){
            Log.d(MainActivity.class.getSimpleName(), "ssid = " + mWifiPwdInfo.get(i).Ssid + " pwd = " + mWifiPwdInfo.get(i).Password);
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
        TextView tvSsid = (TextView) convertView.findViewById(R.id.tv_ssid);
        tvSsid.setText(scanResultPro.getSSID());

        System.out.println(scanResultPro.getSSID()+""+scanResultPro.getSameSSID());
        TextView tvLevel = (TextView) convertView.findViewById(R.id.tv_level);
        tvLevel.setText(String.valueOf(scanResultPro.getLevel()));

        TextView tvSafeLevel = (TextView) convertView.findViewById(R.id.tv_safe_level);
        tvSafeLevel.setText(String.valueOf(scanResultPro.getSafeLevel()));

        TextView tvEncrypt = (TextView) convertView.findViewById(R.id.tv_encrypt);
        tvEncrypt.setText(String.valueOf(scanResultPro.getCapabilities()));

        TextView tvPwd = (TextView) convertView.findViewById(R.id.tv_pwd);
        if(mWifiPwdMap.containsKey(scanResultPro.getSSID())){
            tvPwd.setVisibility(View.VISIBLE);
            tvPwd.setText(mWifiPwdMap.get(scanResultPro.getSSID()));
            tvPwd.setTextColor(ColorStateList.valueOf(Color.RED));
        }else{
            tvPwd.setVisibility(View.GONE);
        }
        return convertView;
    }
}
