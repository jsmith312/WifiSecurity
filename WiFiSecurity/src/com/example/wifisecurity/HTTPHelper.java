package com.example.wifisecurity;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Base64;

public class HTTPHelper {
	public String username;
	public String password;
	public String ipAddress;
	public String mac;
	
	public HTTPHelper(String username, String password, String ipAddress, String MAC) {
		this.setUsername(username);
		this.setPassword(password);
		this.setIpAddress(ipAddress);
		this.setMac(MAC);
	}
	
	public int getResponse() throws Exception {
		String url = getIpAddress();
		String pw = getPassword();
		String u = getUsername();
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		String userCredentials = u+":"+pw;
		String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), 0));
		con.setRequestProperty ("Authorization", basicAuth);
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		return responseCode;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
}
