package com.example.wifisecurity;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;

import com.example.wifidemo.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.NavUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

import org.json.*;
public class ConnectionActivity extends Activity {
	protected static final String DEBUG = "RESPONSE CODE";
	private ScanResult CurrentScanResult;
	private String SSID;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView response;
	private Button getManufacturer;
	private String MAC_ADDRESS;
	private DhcpInfo d;
	private WifiManager wifi;
	private String gatewayIP;
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
		
		wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		d=wifi.getDhcpInfo();

        /*String s_dns1="DNS 1: "+String.valueOf(d.dns1);
        String s_dns2="DNS 2: "+String.valueOf(d.dns2);    
        String s_gateway="Default Gateway: "+String.valueOf(d.gateway);    
        String s_ipAddress="IP Address: "+String.valueOf(d.ipAddress); 
        String s_leaseDuration="Lease Time: "+String.valueOf(d.leaseDuration);     
        String s_netmask="Subnet Mask: "+String.valueOf(d.netmask);    
        String s_serverAddress="Server IP: "+String.valueOf(d.serverAddress);*/
        gatewayIP = FormatIP(d.gateway);
        
        //tv1.setText("Network Info\n"+s_dns1+"\n"+s_dns2+"\n"+s_gateway+"\n"+s_ipAddress+"\n"+s_leaseDuration+"\n"+s_netmask+"\n"+s_serverAddress);
        tv1.setText(gatewayIP);
	    WifiInfo info = wifi.getConnectionInfo();
	    SSID = info.getSSID().toString();
	    MAC_ADDRESS = info.getBSSID();
	    
	    Log.d(DEBUG, info.getMacAddress());
	    HTTPHelper http = new HTTPHelper("admin", "password", "http://"+gatewayIP);
	    try {
			http.getResponse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    getManufacturer.setOnClickListener( new OnClickListener () {
			@Override
			public void onClick(View v) {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost("http://www.macvendorlookup.com/api/v2/"+MAC_ADDRESS);
				try {
				    HttpResponse response = httpClient.execute(post);
				    HttpEntity entity = response.getEntity();
				    String responseString = EntityUtils.toString(entity, "UTF-8");
				    //tv2.setText("RESPONSE:" + responseString);
				    String jsonObj = responseString.substring(1,responseString.length()-1);
				    JSONObject result = new JSONObject(jsonObj);
				    String company =result.getString("company");
				    Log.d(DEBUG, responseString.toString());
				    Log.d(DEBUG, company);
				    tv1.setText(company);
				} catch (ClientProtocolException e) {
				    // Log exception
				    e.printStackTrace();
				} catch (IOException e) {
				    // Log exception
				    e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// replace the first 2 parameters with the API call that will 
				// send back the username and password. Once server is setup.
				HTTPHelper http = new HTTPHelper("admin", "password", "http://"+gatewayIP);
				try {
					int resp = http.getResponse();
					Log.d(DEBUG, resp+"");
					if (resp == 401) {
						Toast.makeText(getApplicationContext(), "Not using default password",
								   Toast.LENGTH_LONG).show();
						setLog(0);
					} else {
						Toast.makeText(getApplicationContext(), "Still using default password",
								   Toast.LENGTH_LONG).show();
						setLog(1);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
}
