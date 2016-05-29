package demo.yuzunzz.edu.wifisafe;

import android.content.Context;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        List<ScanResultPro> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ScanResultPro scanResultPro = list.get(i);
            String firm = dao.getFirm(scanResultPro.getBSSID());
            firm = firm.replaceAll(",."," ");
            firm = firm.replaceAll(".,"," ");
            scanResultPro.setFirm(firm);

            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String time = f.format(new Date());
            scanResultPro.setLastScanTime(time);
            if (scanResultPro.getFirm().equals("unknown")){
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

    public String getEncryptString(String capability){
        StringBuilder sb = new StringBuilder();
        if(TextUtils.isEmpty(capability))
            return "unknow";
        if(capability.contains("WEP")){
            sb.append("WEP");
            return sb.toString();
        }
        if(capability.contains("WPA")){
            sb.append("WPA");
        }
        if(capability.contains("WPA2")){
            sb.append("/");
            sb.append("WPA2");
        }

        if(TextUtils.isEmpty(sb))
            return "OPEN";
        return sb.toString();
    }


}
