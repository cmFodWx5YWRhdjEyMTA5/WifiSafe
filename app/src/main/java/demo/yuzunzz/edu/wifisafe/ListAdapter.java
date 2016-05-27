package demo.yuzunzz.edu.wifisafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;

/**
 * Created by 97349 on 2016/4/8.
 */
public class ListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ScanResultPro> mList = new ArrayList<ScanResultPro>();

    ListAdapter(Context context, List<ScanResultPro> list){
        this.inflater = LayoutInflater.from(context);
        this.mList = list;
    }

    public void refreshList(List<ScanResultPro> list){
        this.mList = list;
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.flag_list_item, null);
        }
        ScanResultPro scanResultPro = mList.get(position);
        TextView tvBlackSsid = (TextView) convertView.findViewById(R.id.tv_flag_ssid);
        TextView tvBlackBssid = (TextView) convertView.findViewById(R.id.tv_flag_bssid);

        tvBlackSsid.setText(scanResultPro.getSSID());
        tvBlackBssid.setText(scanResultPro.getBSSID());
        return convertView;
    }
}
