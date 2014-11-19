package com.andrewovens.securechat;

import java.io.*;
import java.net.*;

public class NetworkOperations {
	public static String HttpGet(URL url) throws IOException
	{
		return HttpGet(url, "GET");
	}
	
	public static String HttpGet(URL url, String method) throws IOException
	{
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			urlConnection.setRequestMethod(method);
			
			BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return total.toString();
		}
    	finally {
    		urlConnection.disconnect();
    	}
	}
	
	public static String HttpGetWithToken(URL url, String token) throws IOException
	{
		return HttpGetWithToken(url, token, "GET");
	}
	
	public static String HttpGetWithToken(URL url, String token, String method) throws IOException
	{
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			urlConnection.setRequestMethod(method);
			
			String authHeader = "Bearer " + token;
		    urlConnection.setRequestProperty("Authorization", authHeader);
			
			BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return total.toString();
		}
    	finally {
    		urlConnection.disconnect();
    	}
	}
	
	public static String HttpPost(URL url, String content) throws IOException
	{
		return HttpPost(url, content, "POST");
	}
	
	public static String HttpPost(URL url, String content, String method) throws IOException
	{
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			urlConnection.setDoOutput(true);
		    urlConnection.setRequestMethod(method);
		    urlConnection.setRequestProperty("Content-Type", "application/json");
		    
		    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		    w.write(content);
		    w.flush();
		    w.close();
		    
			BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return total.toString();
		}
    	finally {
    		urlConnection.disconnect();
    	}
	}
	
	public static String HttpPostWithToken(URL url, String content, String token) throws IOException
	{
		return HttpPostWithToken(url, content, token, "POST");
	}
	
	public static String HttpPostWithToken(URL url, String content, String token, String method) throws IOException
	{
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			urlConnection.setDoOutput(true);
		    urlConnection.setRequestMethod(method);
		    urlConnection.setRequestProperty("Content-Type", "application/json");
		    
		    String authHeader = "Bearer " + token;
		    urlConnection.setRequestProperty("Authorization", authHeader);		    
		    
		    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		    w.write(content);
		    w.flush();
		    w.close();
		    
			BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return total.toString();
		}
    	finally {
    		urlConnection.disconnect();
    	}
	}
	
	public static String HttpPostUrlEncoded(URL url, String content, String method) throws IOException
	{
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		try {
			urlConnection.setDoOutput(true);
		    urlConnection.setRequestMethod(method);
		    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		    
		    BufferedWriter w = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		    w.write(content);
		    w.flush();
		    w.close();
		    
			BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return total.toString();
		}
    	finally {
    		urlConnection.disconnect();
    	}
	}
}
