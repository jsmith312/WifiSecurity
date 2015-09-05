package com.example.wifisecurity;

import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHelper {
	public String username;
	public String password;
	public String ipAddress;
	
	public HTTPHelper(String username, String password, String ipAddress) {
		this.setUsername(username);
		this.setPassword(password);
		this.setIpAddress(ipAddress);
	}
	
	public int getResponse() throws Exception {
		String url = getIpAddress();
		String u = getIpAddress();
		String pw = getUsername();
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
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
	
}
