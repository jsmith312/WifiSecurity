package com.example.wifisecurity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutionException;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	protected static final String DEBUG = "RESPONSE CODE";
	private String SSID;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView response;
	private Button TestDefaultPass;
	private Button logInfo;
	private Button sendLogInfo;
	private Button setPassUser;
	private String MAC_ADDRESS;
	private DhcpInfo d;
	private WifiManager wifi;
	private HTTPHelper httpHelper;
	private String gatewayIP;
	private NetworkInfo networkInfo;
	private ConnectivityManager connMgr;
	private StringBuffer LOG_INFO;
	private String Company;
	private Reporter rep;
	private EditText user;
	private EditText pass;
	
	@SuppressLint("NewApi")	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
	          StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	          StrictMode.setThreadPolicy(policy);
	        }
		setContentView(R.layout.activity_connection);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Text Views 
		tv1 = (TextView)findViewById(R.id.tv1);
		tv2 = (TextView)findViewById(R.id.tv2);
		tv3 = (TextView)findViewById(R.id.tv3);
		
		// EditText
		user = (EditText)findViewById(R.id.user);
		pass = (EditText)findViewById(R.id.pass);
		
		// Buttons 
		TestDefaultPass = (Button)findViewById(R.id.default_pass_button);
		logInfo = (Button)findViewById(R.id.log_info);
		sendLogInfo = (Button)findViewById(R.id.send_log_info);
		setPassUser = (Button)findViewById(R.id.set_pw);
		
		// Network 
		connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	    networkInfo = connMgr.getActiveNetworkInfo();
	    
	    // ensure network connection
        if (networkInfo != null && networkInfo.isConnected()) {
        	wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
    		d=wifi.getDhcpInfo();
            gatewayIP = FormatIP(d.gateway);
            tv3.setText("Default Gateway: " + gatewayIP);
            WifiInfo info = wifi.getConnectionInfo();
    	    SSID = info.getSSID().toString();
    	    tv1.setText("SSID: " + SSID);
    	    //Log.d(DEBUG, info.getMacAddress());
    	    MAC_ADDRESS = info.getBSSID();
    	    httpHelper = new HTTPHelper("", "", "http://"+gatewayIP, 
    	    		MAC_ADDRESS, getApplicationContext());
    	    try {
    	    	int response = httpHelper.getResponse();
    	    	//Log.d(DEBUG, response+"");
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        } else {
        	tv1.setText("No network connection available.");
        }
	}
	
	public void setUserPass(View view) {
		InputMethodManager inputManager = 
		        (InputMethodManager) getApplicationContext().
		            getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(
		        this.getCurrentFocus().getWindowToken(),
		        InputMethodManager.HIDE_NOT_ALWAYS); 
	}
	
	public void sendLogInfo(View view) {
		// Send to server when available
		WriteLogFile(LOG_INFO);
		tv2.setText("");
	}
	
	public void showLogInfo(View view) {
		tv2.setText(LOG_INFO);
	}
	
	public void testUserPass(View view) {
		if (networkInfo != null && networkInfo.isConnected()) {
			if (!user.getText().toString().matches("") && 
					!user.getText().toString().matches("")) {
				try {
					httpHelper.setUsername(user.getText().toString());
					httpHelper.setPassword(pass.getText().toString());
	    	    	int response = httpHelper.getResponse();
	    	    	if (response == 401) {
	    	    		Toast.makeText(getApplicationContext(), "Incorrect password",
								   Toast.LENGTH_LONG).show();
	    	    		setLog(false);
	    	    	} else {
	    	    		Toast.makeText(getApplicationContext(), "Correct password",
								   Toast.LENGTH_LONG).show();
	    	    		tv3.setText("Company: " + Company + "\n" +
	    	    				"username: " + user.getText().toString() + "\n" +
								"password: " + pass.getText().toString() + "\n");
	    	    		setLog(true);
	    	    	}
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
				user.setText("");
				pass.setText("");
			}
		} else {
        	tv1.setText("No network connection available.");
        }
	}
	
	public void TestPW(View view) {
		if (networkInfo != null && networkInfo.isConnected()) {
			HTTPThread thread = new HTTPThread();
			thread.execute(httpHelper);
			try {
					rep = thread.get();
					if(rep.getHasDefaultPassword() == true) {
						Toast.makeText(getApplicationContext(), "Found default password",
							   Toast.LENGTH_LONG).show();
						user.setText(rep.getDefaultUserName());
						pass.setText(rep.getDefaultPassword());
						Company = rep.getCompany();
					} else {
						Toast.makeText(getApplicationContext(), "No default password found",
								   Toast.LENGTH_LONG).show();	
					}
					setLog(rep.getHasDefaultPassword());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else {
        	tv1.setText("No network connection available.");
        }
	 }
	
	private void setLog(boolean response) {
		Date d = new Date();
		StringBuffer LOG = new StringBuffer(); 
		LOG.append("[");
		LOG.append(new Timestamp(d.getTime()));
		LOG.append("]: ");
		LOG.append(SSID+" ");
		LOG.append(MAC_ADDRESS);
		LOG.append(" default_pw:" + response+"\n");
		LOG_INFO = LOG;
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
	}
	
	private void ReadLogFile() {
		String eol = System.getProperty("line.separator");
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(openFileInput("LOG")));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				buffer.append(line + eol);
			}
			TextView textView = (TextView) findViewById(R.id.tv3 );
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
