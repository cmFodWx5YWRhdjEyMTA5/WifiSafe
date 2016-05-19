package demo.yuzunzz.edu.wifisafe;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import demo.yuzunzz.edu.wifisafe.bean.ScanResultPro;
import demo.yuzunzz.edu.wifisafe.dao.Dao;

/**
 * Created by 97349 on 2016/4/14.
 */
public class SafeDirect{
    private Dao dao;
    SafeDirect(Context context){
        dao = new Dao(context);
    }

    public List<ScanResultPro> safeDirect(List<ScanResultPro> list){
        List<ScanResultPro> orderList = safeOrder(list);
        List<ScanResultPro> result = new ArrayList<ScanResultPro>();
        List<String> set = new ArrayList<String>();
        for (int i = 0; i < orderList.size(); i++) {
            ScanResultPro scanResultPro = orderList.get(i);
            set.add(scanResultPro.getSSID());
        }

        for (int i = 0; i < orderList.size(); i++) {
            ScanResultPro scanResultPro = orderList.get(i);
            scanResultPro.setSameSSID(Collections.frequency(set, scanResultPro.getSSID()));
            String firm = dao.getFirm(scanResultPro.getBSSID());
            firm = firm.replaceAll(",."," ");
            firm = firm.replaceAll(".,"," ");
            scanResultPro.setFirm(firm);

            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String time = f.format(new Date());
            scanResultPro.setLastScanTime(time);
            if (scanResultPro.getFirm().equals("unknown")
                    || Collections.frequency(set, scanResultPro.getSSID())>1){
                scanResultPro.setSafeLevel("low");
            } else {
                if (scanResultPro.getEncryptString(scanResultPro.getCapabilities()).contains("OPEN")
                        || scanResultPro.getEncryptString(scanResultPro.getCapabilities()).contains("WEP")){
                    scanResultPro.setSafeLevel("medium");
                } else {
                    scanResultPro.setSafeLevel("high");
                }
            }

            if (!dao.isApExist(scanResultPro.getBSSID().trim())){
                dao.add(scanResultPro);
            }
            if (!dao.getLatestFlag(scanResultPro.getBSSID()).equals("danger")&& !scanResultPro.getSSID().contains("CMCC")){
                result.add(scanResultPro);
                dao.updateLastScanTime(scanResultPro.getBSSID(),scanResultPro.getLastScanTime());
            }
        }
       return  result;
    }

    public List<ScanResultPro> safeOrder(List<ScanResultPro> list){
        ComparatorResult comparator = new ComparatorResult();
        Collections.sort(list, comparator);
        return list;
    }

    class ComparatorResult implements Comparator{

        @Override
        public int compare(Object lhs, Object rhs) {
            ScanResultPro srp1 = (ScanResultPro)lhs;
            ScanResultPro srp2 = (ScanResultPro)rhs;
            int flag = String.valueOf(srp1.getLevel()).compareTo(String.valueOf(srp2.getLevel()));
            if (flag == 0){
                return srp1.getSSID().compareTo(srp2.getSSID());
            } else {
                return flag;
            }
        }
    }
}
