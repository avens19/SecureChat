package com.andrewovens.securechat;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatWindow extends Activity {
	private Chat thisChat;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		String json = this.getIntent().getStringExtra("chat");
		thisChat = new Gson().fromJson(json, Chat.class);
		
		load();
	}
	
	private void load(){
		new Thread(new Runnable(){
			public void run(){
				try {
					final Message[] messages = API.GetMessages(thisChat.id,Settings.getToken(ChatWindow.this));
					ChatWindow.this.runOnUiThread(new Runnable(){
						public void run(){
							ListView chatList = (ListView)ChatWindow.this.findViewById(R.id.chatlist);
							MessageAdapter adapter = new MessageAdapter(ChatWindow.this, R.layout.chat_row, messages);
							chatList.setAdapter(adapter);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("TrulyRandom") 
	public void send(View view){
		final EditText et = (EditText)findViewById(R.id.chatedit);
		final String message = et.getText().toString();
		if(message.trim().length() > 0)
		{
			final Message m = new Message();
			m.messageType = "Text";
			m.contents = new EncryptedMessage[thisChat.chatters.length];
			for(int i = 0; i < thisChat.chatters.length; i++){
				try {
					User u = thisChat.chatters[i];
					EncryptedMessage em = new EncryptedMessage();
					em.recipientUsername = u.username;
					em.publicKey = u.publicKey;
					
					User me = Settings.getMe(ChatWindow.this);
					
					byte[] pubKeyBytes = Base64.decode(u.publicKey, Base64.DEFAULT);
					
					PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKeyBytes));
					
					Cipher cipher = Cipher.getInstance("RSA");
				    cipher.init(Cipher.ENCRYPT_MODE, key);
			    
					byte[] encryptedBytes = cipher.doFinal(message.getBytes());
					em.content = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
					m.contents[i] = em;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			new Thread(new Runnable(){
				public void run(){
					try {
						API.SendMessage(thisChat.id, m, Settings.getToken(ChatWindow.this));
						ChatWindow.this.runOnUiThread(new Runnable(){
							public void run(){
								et.setText("");
							}
						});
						load();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	public class MessageAdapter extends ArrayAdapter<Message>
    {
    	private final Context context;
		private final int resourceID;
		private Message[] list;
		private User me;
		private PrivateKey pk;
		
		public MessageAdapter(Context context, int resource, Message[] objects) {
			super(context, resource, objects);
			
			this.context = context;
			this.resourceID = resource;
			this.list = objects;
			this.me = Settings.getMe(context);
			String privateKey = Settings.getPrivateKey(context);
			try {
				this.pk = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflate.inflate(R.layout.chat_row, parent, false);

			//place the item in the row.
			TextView sender = (TextView)row.findViewById(R.id.message_sender);
			TextView message = (TextView)row.findViewById(R.id.message_body);

			Message m = list[position];
			sender.setText(m.sender.name);
			sender.setTextColor(Color.BLACK);
			
			String encryptedContent = "";
			for(EncryptedMessage em: m.contents){
				if(em.recipientUsername.equals(me.username)){
					encryptedContent = em.content;
					break;
				}
			}
			
			try{
				Cipher cipher1 = Cipher.getInstance("RSA");
			    cipher1.init(Cipher.DECRYPT_MODE, pk);
			    byte[] decryptedBytes = cipher1.doFinal(Base64.decode(encryptedContent, Base64.DEFAULT));
			    String decrypted = new String(decryptedBytes);
	
				message.setTextColor(Color.BLACK);
				message.setText(decrypted);
			}
			catch(Exception e){
				e.printStackTrace();
			}

			return row;
		}
    	
    }
}
