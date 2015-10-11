package com.example.wifisecurity;

import java.sql.Timestamp;
import java.util.Date;

import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;

import com.example.wifidemo.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class ConnectionActivity extends Activity {
	protected static final String DEBUG = "RESPONSE CODE";
	private String SSID;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView response;
	private Button getManufacturer;
	private String MAC_ADDRESS;
	private DhcpInfo d;
	private WifiManager wifi;
	private HTTPHelper httpHelper;
	private String gatewayIP;
	private NetworkInfo networkInfo;
	private ConnectivityManager connMgr;
	
	@SuppressLint("NewApi")	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
	          StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	          StrictMode.setThreadPolicy(policy);
	        }
		setContentView(R.layout.activity_connection);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		tv1 = (TextView)findViewById(R.id.tv1);
		tv2 = (TextView)findViewById(R.id.tv2);
		tv3 = (TextView)findViewById(R.id.tv3);
		getManufacturer = (Button)findViewById(R.id.manufacturer_button);
		connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    networkInfo = connMgr.getActiveNetworkInfo();
	    
	    // ensure network connection
        if (networkInfo != null && networkInfo.isConnected()) {
        	wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
    		d=wifi.getDhcpInfo();
            gatewayIP = FormatIP(d.gateway);
            tv1.setText(gatewayIP);
            WifiInfo info = wifi.getConnectionInfo();
    	    SSID = info.getSSID().toString();
    	    Log.d(DEBUG, info.getMacAddress());
    	    MAC_ADDRESS = info.getBSSID();
    	    httpHelper = new HTTPHelper("admin", "password", "http://"+gatewayIP, MAC_ADDRESS);
    	    try {
    	    	httpHelper.getResponse();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        } else {
        	tv1.setText("No network connection available.");
        }
	    
	    getManufacturer.setOnClickListener( new OnClickListener () {
			@Override
			public void onClick(View v) {
				if (networkInfo != null && networkInfo.isConnected()) {
					new HTTPThread().execute(httpHelper);
				} else {
		        	tv1.setText("No network connection available.");
		        }	
			}
		});
	}
	
	private void setLog(int response) {
		StringBuffer LOG = new StringBuffer();
		Date d = new Date();
		LOG.append("[");
		LOG.append(new Timestamp(d.getTime()));
		LOG.append("]: ");
		LOG.append(SSID+" ");
		LOG.append(MAC_ADDRESS);
		LOG.append(" default_pw:" + response);
		tv3.setText(LOG);
		//WriteLogFile(LOG);
		Log.d(DEBUG, LOG.toString());
	}
	
	private void WriteLogFile(StringBuffer Log) {
		String eol = System.getProperty("line.separator");
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					openFileOutput("LOG", MODE_WORLD_WRITEABLE)));
			writer.write(Log.append(eol).toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(openFileInput("LOG")));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);
			}
			TextView textView = (TextView) findViewById(R.id.tv3);
				if (textView == null) {
			}
			textView.setText(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String FormatIP(int IpAddress) {
	    return Formatter.formatIpAddress(IpAddress);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
