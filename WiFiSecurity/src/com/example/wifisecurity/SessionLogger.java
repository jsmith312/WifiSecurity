package com.example.wifisecurity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Date;

import com.example.wifidemo.R;

import android.util.Log;
import android.widget.TextView;

public class SessionLogger {
	private String SSID;
	private String MacAddress;
	private int testResult;
	
	public SessionLogger(String SSID, String MacAddress, int testResult) {
		this.setSSID(SSID);
		this.setMacAddress(MacAddress);
		this.setTestResult(testResult);
	}
	
	private void setLog(int response) {
		StringBuffer LOG = new StringBuffer();
		Date d = new Date();
		LOG.append("[");
		LOG.append(new Timestamp(d.getTime()));
		LOG.append("]: ");
		LOG.append(getSSID()+" ");
		LOG.append(getMacAddress());
		LOG.append(" default_pw:" + getTestResult());
		//WriteLogFile(LOG);
	}
	
	public String getSSID() {
		return SSID;
	}

	public void setSSID(String sSID) {
		SSID = sSID;
	}

	public String getMacAddress() {
		return MacAddress;
	}

	public void setMacAddress(String macAddress) {
		MacAddress = macAddress;
	}

	public int getTestResult() {
		return testResult;
	}

	public void setTestResult(int testResult) {
		this.testResult = testResult;
	}
}
