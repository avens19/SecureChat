package com.andrewovens.securechat;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class API {
private static String baseUrl = "http://securechatserver.azurewebsites.net/";

	public static void Register(String name, String username, String password, 
			String confirmPassword, String publicKey) throws JSONException, IOException
	{
		String urlString = baseUrl + "api/Account/Register";
		URL url = new URL(urlString);
		
		JSONObject registration = new JSONObject();
		registration.put("name", name);
		registration.put("username", username);
		registration.put("password", password);
		registration.put("confirmPassword", confirmPassword);
		registration.put("publicKey", publicKey);
		NetworkOperations.HttpPost(url, registration.toString());
	}
	
	public static TokenResponse Login(String username, String password) throws IOException
	{
		String urlString = baseUrl + "Token";
		URL url = new URL(urlString);
		
		String login = "grant_type=password&username=" + username + "&password=" + password; 
		
		String response = NetworkOperations.HttpPost(url, login, "POST");
		
		return new Gson().fromJson(response, TokenResponse.class);
	}
	
	public static Chat CreateChat(Chat chat, String token) throws IOException
	{
		String urlString = baseUrl + "api/Chats";
		URL url = new URL(urlString);
		
		String json = new Gson().toJson(chat, Chat.class);
		
		String response = NetworkOperations.HttpPostWithToken(url, json, token);
		
		return new Gson().fromJson(response, Chat.class);
	}
	
	public static Chat[] GetChats(String token) throws IOException
	{
		String urlString = baseUrl + "api/Chats";
		URL url = new URL(urlString);
		
		String response = NetworkOperations.HttpGetWithToken(url, token);
		
		return new Gson().fromJson(response, Chat[].class);
	}
	
	public static Message[] GetMessages(long chatId, String token) throws IOException
	{
		String urlString = baseUrl + "api/Chats/" + chatId + "/Messages?watermark=null";
		URL url = new URL(urlString);
		
		String response = NetworkOperations.HttpGetWithToken(url, token);
		
		return new Gson().fromJson(response, Message[].class);
	}
	
	public static Message[] GetMessages(long chatId, String watermark, String token) throws IOException
	{
		String urlString = baseUrl + "api/Chats/" + chatId + "/Messages?watermark=" + watermark;
		URL url = new URL(urlString);
		
		String response = NetworkOperations.HttpGetWithToken(url, token);
		
		return new Gson().fromJson(response, Message[].class);
	}
	
	public static Message SendMessage(long chatId, Message message, String token) throws IOException
	{
		String urlString = baseUrl + "api/Chats/" + chatId + "/Messages";
		URL url = new URL(urlString);
		
		String json = new Gson().toJson(message, Message.class);
		
		String response = NetworkOperations.HttpPostWithToken(url, json, token);
		
		return new Gson().fromJson(response, Message.class);
	}
}
