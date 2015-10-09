package com.example.wifisecurity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.SyncStateContract.Constants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import com.example.wifidemo.R;

public class MainActivity extends Activity  {
   private ListView lv;
   private WifiManager wifi;
   private String wifis[];
   TextView tv1;
   private WifiScanReceiver wifiReciever;
   private List<ScanResult> wifiList;
   private ScanResult wifisObj[];
   @SuppressLint("NewApi")
@Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (android.os.Build.VERSION.SDK_INT > 9) {
          StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
          StrictMode.setThreadPolicy(policy);
        }
      setContentView(R.layout.activity_main);
      tv1 = (TextView)findViewById(R.id.textview);
      //lv=(ListView)findViewById(R.id.listView);
      wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
      WifiInfo info = wifi.getConnectionInfo();
      tv1.setText(info.getSSID());
      tv1.setText(info.getMacAddress());
      
      //wifiReciever = new WifiScanReceiver();
      //wifi.startScan();
   }
   
   protected void onPause() {
      unregisterReceiver(wifiReciever);
      super.onPause();
   }
   
   protected void onResume() {
      registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
      super.onResume();
   }
   
   private class WifiScanReceiver extends BroadcastReceiver {
      public void onReceive(Context c, Intent intent) {
         List<ScanResult> wifiList = wifi.getScanResults();
         wifis = new String[wifiList.size()];
         wifisObj = new ScanResult[wifiList.size()]; 
         for(int i = 0; i < wifiList.size(); i++){
            wifis[i] = ((wifiList.get(i).SSID).toString());
            wifisObj[i] = wifiList.get(i);
         }
         lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,wifis));
         lv.setOnItemClickListener(new OnItemClickListener() {
 			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
 				Intent intent = new Intent(MainActivity.this, ConnectionActivity.class);
 				Bundle bundle = new Bundle();
 				bundle.putParcelable(Constants.DATA, wifisObj[position]);
 				intent.putExtras(bundle);
 				intent.putExtras(bundle);
 				startActivity(intent);
 			}
 		});
      }
   }
}