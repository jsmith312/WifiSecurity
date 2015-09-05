package com.example.wifisecurity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
   ListView lv;
   WifiManager wifi;
   String wifis[];
   WifiScanReceiver wifiReciever;
   List<ScanResult> wifiList;
   ScanResult wifisObj[];
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      lv=(ListView)findViewById(R.id.listView);
      wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
      wifiReciever = new WifiScanReceiver();
      wifi.startScan();
   }
   
   protected void onPause() {
      unregisterReceiver(wifiReciever);
      super.onPause();
   }
   
   protected void onResume() {
      registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
      super.onResume();
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      
      int id = item.getItemId();
      
      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
         return true;
      }
      return super.onOptionsItemSelected(item);
   }
   
   private class WifiScanReceiver extends BroadcastReceiver{
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