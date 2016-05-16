package demo.yuzunzz.edu.wifisafe;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
//            System.out.println("count:"+scanResultPro.getSSID()+"_________"+Collections.frequency(set, scanResultPro.getSSID()));
            if (dao.getFirm(scanResultPro.getBSSID()).equals("unknown")
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
            } else {
                System.out.println("exist +1"+scanResultPro.getSSID().trim());
            }
            if (dao.getLatestFlag(scanResultPro.getBSSID()).equals("danger")){
                System.out.println("danager"+scanResultPro.getSSID().trim());
            } else {
                result.add(scanResultPro);
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
