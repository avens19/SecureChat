package com.andrewovens.securechat;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	public static final String SETTINGS_NAME = "SECURE_CHAT_SETTINGS";
	public static final String WATERMARK = "WATERMARK";
	public static final String TOKEN = "TOKEN";
	public static final String EXPIRY = "EXPIRY";
	public static final String ME = "ME";
	public static final String PASSWORD = "PASSWORD";
	public static final String PRIVATEKEY = "PRIVATEKEY";
	
	public static String getWatermark(Context cxt)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		return settings.getString(WATERMARK, null);
	}
	public static void setWatermark(Context cxt, String watermark)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(WATERMARK, watermark);
		editor.commit();
	}
	
	public static String getToken(Context cxt)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		return settings.getString(TOKEN, null);
	}
	
	public static void setToken(Context cxt, String token)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(TOKEN, token);
		editor.commit();
	}
	
	public static long getExpiry(Context cxt)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		return settings.getLong(EXPIRY, 0);
	}
	
	public static void setExpiry(Context cxt, long expiry)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(EXPIRY, expiry);
		editor.commit();
	}
	
	public static User getMe(Context cxt)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		String user = settings.getString(ME, null);
		if(user == null)
			return null;
		
		return new Gson().fromJson(user, User.class);
	}
	
	public static void setMe(Context cxt, User me)
	{
		if(me == null)
			return;
		
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ME, new Gson().toJson(me, User.class));
		editor.commit();
	}
	
	public static String getPassword(Context cxt)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		return settings.getString(PASSWORD, null);
	}
	
	public static void setPassword(Context cxt, String pk)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PASSWORD, pk);
		editor.commit();
	}
	
	public static String getPrivateKey(Context cxt)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		return settings.getString(PRIVATEKEY, null);
	}
	
	public static void setPrivateKey(Context cxt, String pk)
	{
		SharedPreferences settings = cxt.getSharedPreferences(SETTINGS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PRIVATEKEY, pk);
		editor.commit();
	}
}
