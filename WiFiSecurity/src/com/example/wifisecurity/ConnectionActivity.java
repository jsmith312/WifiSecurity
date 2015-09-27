package com.example.wifisecurity;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.example.wifidemo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
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
	private String MAC_ADDRESS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);
		tv1 = (TextView)findViewById(R.id.tv1);
		tv2 = (TextView)findViewById(R.id.tv2);
		tv3 = (TextView)findViewById(R.id.tv3);
		response = (TextView)findViewById(R.id.response);
		testPW = (Button)findViewById(R.id.default_pw_button);
		MAC_ADDRESS = getMacAddress();
		tv3.setText("MAC_ADDRESS " + MAC_ADDRESS);
		
		Bundle b = this.getIntent().getExtras();
		if(b!=null) {
			CurrentScanResult = b.getParcelable(Constants.DATA);
			tv1.setText("SSID: " + CurrentScanResult.SSID.toString());
			SSID = CurrentScanResult.SSID.toString();
			tv2.setText("Capabilities: " + CurrentScanResult.capabilities.toString());
		}
		
		
		
		testPW.setOnClickListener( new OnClickListener () {
			@Override
			public void onClick(View v) {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost("http://www.macvendorlookup.com/api/v2/"+MAC_ADDRESS);
				
				try {
				    HttpResponse response = httpClient.execute(post);
				    HttpEntity entity = response.getEntity();
				    String responseString = EntityUtils.toString(entity, "UTF-8");
				    // write response to log
				    tv2.setText("RESPONSE: "+responseString);
				} catch (ClientProtocolException e) {
				    // Log exception
				    e.printStackTrace();
				} catch (IOException e) {
				    // Log exception
				    e.printStackTrace();
				}
				/*HTTPHelper http = new HTTPHelper("admin", "password", "http://192.168.1.1/");
				try {
					int resp = http.getResponse();
					//String mac = http.getMacAddress();
					//response.setText(mac+"");
					Log.d(DEBUG, resp+"");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				// conntecting to wifi source.
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
	
	private String getMacAddress() {
		// get the MAC address
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		String macAddress = wInfo.getBSSID();
		return macAddress;
	}
}
