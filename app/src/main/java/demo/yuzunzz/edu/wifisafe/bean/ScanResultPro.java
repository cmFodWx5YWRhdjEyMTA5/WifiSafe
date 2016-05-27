package demo.yuzunzz.edu.wifisafe.bean;
import android.net.wifi.ScanResult;
import android.text.TextUtils;

/**
 * Created by 97349 on 2016/2/29.
 */
public class ScanResultPro{
    private String SSID,BSSID,capabilities,flag,safeLevel,firm,lastScanTime;
    private int level,frequency;
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
        this.capabilities = "";
        this.level = 0;
        this.frequency = 0;
        this.firm = firm;
        this.lastScanTime = time;
    }


    public String getLastScanTime() {
        return lastScanTime;
    }

    public void setLastScanTime(String lastScanTime) {
        this.lastScanTime = lastScanTime;
    }

    public String getFirm() {
        return firm;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }

    public String getSafeLevel() {
        return safeLevel;
    }

    public void setSafeLevel(String safeLevel) {
        this.safeLevel = safeLevel;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCapabilities() {
        return getEncryptString(capabilities);
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
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
