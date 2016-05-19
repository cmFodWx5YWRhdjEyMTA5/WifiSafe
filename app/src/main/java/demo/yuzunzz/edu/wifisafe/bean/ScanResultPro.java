package demo.yuzunzz.edu.wifisafe.bean;
import android.net.wifi.ScanResult;
import android.text.TextUtils;

/**
 * Created by 97349 on 2016/2/29.
 */
public class ScanResultPro {
    private String SSID,BSSID,capabilities,flag,safeLevel,firm,lastScanTime;
    private int level,frequency,sameSSID;
    public ScanResultPro(ScanResult mScanResult) {
        this.SSID = mScanResult.SSID;
        this.BSSID = mScanResult.BSSID;
        this.capabilities = mScanResult.capabilities;
        this.level = mScanResult.level;
        this.frequency = mScanResult.frequency;
        this.flag = "default";
        this.safeLevel = "unknown";
        this.firm = "unknown";
        this.lastScanTime = "";
    }

    public ScanResultPro(String SSID,String BSSID,String flag,String safeLevel,String firm,String time){
        this.SSID = SSID;
        this.BSSID = BSSID;
        this.flag = flag;
        this.safeLevel = safeLevel;
        this.capabilities = null;
        this.level = 0;
        this.frequency = 0;
        this.firm = firm;
        this.lastScanTime = time;
    }

    public static String getEncryptString(String capability){
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
//        if(capability.contains("WPS")){
//            sb.append("/");
//            sb.append("WPS");
//        }
        if(TextUtils.isEmpty(sb))
            return "OPEN";
        return sb.toString();
    }



    public String getSSID() {
        return SSID;
    }
    public String getBSSID() {
        return BSSID;
    }
    public String getFlag() {
        return flag;
    }
    public String getSafeLevel(){return safeLevel;}
    public String getCapabilities() {
        return getEncryptString(capabilities);
    }
    public int getLevel() {
        return level;
    }
    public String getLastScanTime() {
        return lastScanTime;
    }
    public int getFrequency() {
        return frequency;
    }
    public void setSSID(String sSID) {
        SSID = sSID;
    }
    public void setBSSID(String bSSID) {
        BSSID = bSSID;
    }
    public String getFirm() {return firm;}
    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
    public void setFlag(String flag){
        this.flag = flag;
    }
    public void setSafeLevel(String safeLevel){
        this.safeLevel = safeLevel;
    }
    public void setLastScanTime(String time) {this.lastScanTime = time;}
    public String toString() {
        return SSID+" "+BSSID+" "+level+" "+flag;
    }


    public int getSameSSID() {
        return sameSSID;
    }

    public void setSameSSID(int sameSSID) {
        this.sameSSID = sameSSID;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }
}
