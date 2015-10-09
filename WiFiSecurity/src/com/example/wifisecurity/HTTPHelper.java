package com.example.wifisecurity;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

public class HTTPHelper {
	public String username;
	public String password;
	public String ipAddress;
	public String DEBUG = "YOYO";
	public HTTPHelper(String username, String password, String ipAddress) {
		this.setUsername(username);
		this.setPassword(password);
		this.setIpAddress(ipAddress);
	}
	
	public int getResponse() throws Exception {
		String authorization = "";
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
	
	@SuppressLint("NewApi")
	public String getMacAddress() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			Log.d(DEBUG, ip.toString());
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length-1; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			return sb.toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e){
			e.printStackTrace();
		}
		return "";
	}
	
	public String getBasicAuthenticationEncoding(String u, String pw) {
		byte[] data = null;
		String userPassword = u + ":" + pw;
        try {
			data = userPassword.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new String(Base64.encodeToString(data, Base64.DEFAULT));
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
