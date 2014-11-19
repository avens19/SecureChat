package com.andrewovens.securechat;

import java.util.Calendar;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class ChatList extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        
        final User me = Settings.getMe(this);
        if(me == null)
        {
        	Intent i = new Intent(this, Register.class);
        	startActivity(i);
        	return;
        }
        
        long now = Calendar.getInstance().getTimeInMillis();
        long expiry = Settings.getExpiry(this);
        
        if(expiry - now < 1000*60*60*24)
        {
        	new Thread(new Runnable(){
        		public void run(){
        			try
        			{
	        			TokenResponse tr = API.Login(me.username, Settings.getPassword(ChatList.this));
						Settings.setToken(ChatList.this, tr.access_token);
						Calendar c = Calendar.getInstance();
						c.add(Calendar.SECOND, tr.expires_in);
						Settings.setExpiry(ChatList.this, c.getTimeInMillis());
						load();
        			}
        			catch(Exception e)
        			{
        				e.printStackTrace();
        			}
					
        		}
        	}).start();
        	return;
        }
        load();
    }
    
    private void load()
    {
    	new Thread(new Runnable(){
			@Override
			public void run() {
				try
				{
					final Chat[] c = API.GetChats(Settings.getToken(ChatList.this));
					
					ChatList.this.runOnUiThread(new Runnable(){
						public void run(){
							ChatAdapter adapter = new ChatAdapter(ChatList.this, R.layout.chat_list_row, c);
							
							ListView lv = (ListView)ChatList.this.findViewById(R.id.list_chatList);
							
							lv.setOnItemClickListener(new OnItemClickListener(){
								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									Chat c = (Chat)parent.getItemAtPosition(position);
									Intent i = new Intent(ChatList.this, ChatWindow.class);
									i.putExtra("chat", new Gson().toJson(c, Chat.class));
									ChatList.this.startActivity(i);
								}
							});
							
							lv.setAdapter(adapter);
						}
					});
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	getMenuInflater().inflate(R.menu.chat_list, menu);
		MenuItem add = menu.findItem(R.id.action_add);
		add.setOnMenuItemClickListener(new OnMenuItemClickListener(){

			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ChatList.this);
				
				LayoutInflater inflater = (LayoutInflater) ChatList.this.getLayoutInflater();
				final View v = inflater.inflate(R.layout.new_chat_dialog, null);
				
				builder.setView(v);
				
				builder.setMessage(R.string.new_chat_dialog_text)
				.setTitle(R.string.new_chat_dialog_title)

				.setPositiveButton(R.string.new_chat_ok, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {
						final EditText username = (EditText)v.findViewById(R.id.new_chat_username);
						
						new Thread(new Runnable(){
							public void run(){
								try
								{
									User u = new User();
									u.username = username.getText().toString();
									Chat c = new Chat();
									c.chatters = new User[1];
									c.chatters[0] = u;
									Chat newChat = API.CreateChat(c, Settings.getToken(ChatList.this));
									Intent i = new Intent(ChatList.this, ChatWindow.class);
									i.putExtra("chat", new Gson().toJson(newChat, Chat.class));
									ChatList.this.startActivity(i);
									dialog.dismiss();
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
						}).start();
					}
				})

				.setNegativeButton(R.string.new_chat_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}});
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
    
    public class ChatAdapter extends ArrayAdapter<Chat>
    {
    	private final Context context;
		private final int resourceID;
		private Chat[] list;
		
		public ChatAdapter(Context context, int resource, Chat[] objects) {
			super(context, resource, objects);
			
			this.context = context;
			this.resourceID = resource;
			this.list = objects;
		}
			
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = convertView != null ? convertView : inflater.inflate(resourceID, parent, false);
			
			TextView name = (TextView)rowView.findViewById(R.id.chatRow_name);
			name.setText(list[position].chatters[0].name + " - " + list[position].chatters[1].name);
			
			return rowView;
		}
    	
    }
}
