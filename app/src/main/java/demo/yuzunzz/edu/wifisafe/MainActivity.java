package demo.yuzunzz.edu.wifisafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button btnWifiList,btnOpenWifi,btnCloseWifi,btnCheckState,btnWhiteList,btnBlackList,btnWifiList_1;
    private TextView tvWifiState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    @Override
    public void onClick(View v) {

    }
}
