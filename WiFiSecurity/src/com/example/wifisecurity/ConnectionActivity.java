package com.example.wifisecurity;

import com.example.wifidemo.R;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ConnectionActivity extends Activity {
	protected static final String DEBUG = "RESPONSE CODE";
	private ScanResult CurrentScanResult;
	private String SSID;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView response;
	private Button testPW;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);
		tv1 = (TextView)findViewById(R.id.tv1);
		tv2 = (TextView)findViewById(R.id.tv2);
		tv3 = (TextView)findViewById(R.id.tv3);
		response = (TextView)findViewById(R.id.response);
		testPW = (Button)findViewById(R.id.default_pw_button);
		Bundle b = this.getIntent().getExtras();
		if(b!=null) {
			CurrentScanResult = b.getParcelable(Constants.DATA);
			tv1.setText("SSID: " + CurrentScanResult.SSID.toString());
			SSID = CurrentScanResult.SSID.toString();
			tv3.setText("BSSID: " + CurrentScanResult.BSSID.toString());
			tv2.setText("Capabilities: " + CurrentScanResult.capabilities.toString());
		}
		
		testPW.setOnClickListener( new OnClickListener () {
			@Override
			public void onClick(View v) {
				HTTPHelper http = new HTTPHelper("root", "admin", "http://192.168.1.1/");
				try {
					int resp = http.getResponse();
					response.setText(resp+"");
					Log.d(DEBUG, resp+"");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*WifiConfiguration wifiConfig = new WifiConfiguration();
				wifiConfig.SSID = String.format("\"%s\"", SSID);
				wifiConfig.preSharedKey = String.format("\"%s\"", "*");

				WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
				//remember id
				int netId = wifiManager.addNetwork(wifiConfig);
				wifiManager.disconnect();
				wifiManager.enableNetwork(netId, true);
				wifiManager.reconnect();*/
			}
		});
		
		
		
	}
}
