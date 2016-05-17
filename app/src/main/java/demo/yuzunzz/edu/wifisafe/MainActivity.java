package demo.yuzunzz.edu.wifisafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button btnWifiList,btnOpenWifi,btnCloseWifi,btnCheckState,btnWhiteList,btnBlackList,btnWifiList_1;
    private TextView tvWifiState;
    public static WifiUtil mWifiUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiUtil = new WifiUtil(this);
        btnWifiList = (Button) findViewById(R.id.btn_wifi_list);
        btnOpenWifi = (Button) findViewById(R.id.btn_open_wifi);
        btnCloseWifi = (Button) findViewById(R.id.btn_close_wifi);
        btnCheckState = (Button) findViewById(R.id.btn_check_state);
        btnWhiteList = (Button) findViewById(R.id.btn_white_list);
        btnBlackList = (Button) findViewById(R.id.btn_black_list);
        tvWifiState = (TextView) findViewById(R.id.tv_wifi_state);

        btnWifiList.setOnClickListener(this);
        btnOpenWifi.setOnClickListener(this);
        btnCloseWifi.setOnClickListener(this);
        btnCheckState.setOnClickListener(this);
        btnWhiteList.setOnClickListener(this);
        btnBlackList.setOnClickListener(this);
        checkState();

        String dbDirPath = "/data/data/demo.yuzunzz.edu.wifisafe/databases";
        File dbDir = new File(dbDirPath);
        if(!dbDir.exists()) // 如果不存在该目录则创建
            dbDir.mkdir();
        // 打开静态数据库文件的输入流
        InputStream is = this.getResources().openRawResource(R.raw.test);
        // 打开目标数据库文件的输出流
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(dbDirPath+"/test.db");
            byte[] buffer = new byte[1024];
            int count = 0;
            // 将静态数据库文件拷贝到目的地
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            System.out.println("finish copy");
            is.close();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_wifi_list:
                if (mWifiUtil.checkState() == 3) {
                    Toast.makeText(this, "wifi_list_activity", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, AtyWifiListPro.class);
                    startActivity(i);
                } else {
                    Toast.makeText(this, "wifi未开启或正在开启...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_open_wifi:
                mWifiUtil.openWifi();
                checkState();
                break;
            case R.id.btn_close_wifi:
                mWifiUtil.closeWifi();
                checkState();
                break;
            case R.id.btn_check_state:
                checkState();
                break;
            case R.id.btn_white_list:
                Toast.makeText(this, "white_list_activity", Toast.LENGTH_SHORT).show();
                Intent j = new Intent(MainActivity.this, AtyWhiteList.class);
                startActivity(j);
                break;
            case R.id.btn_black_list:
                Toast.makeText(this, "black_list_activity", Toast.LENGTH_SHORT).show();
                Intent k = new Intent(MainActivity.this, AtyBlackList.class);
                startActivity(k);
                break;
            default:
                break;
        }
    }

    public void checkState(){
        String state = "";
        switch (mWifiUtil.checkState()) {
            case 0:
            case 1:
                state = "wifi已关闭";
                break;
            case 2:
                state = "wifi正在打开...";
                break;
            case 3:
                state = "wifi已开启...";
                break;
            case 4:
                state = "wifi状态未知";
                break;

            default:
                break;
        }
        tvWifiState.setText(state);
    }
}
