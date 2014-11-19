package com.andrewovens.securechat;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Calendar;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Register extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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
	
	public void registerClick(View view)
	{
		final EditText name = (EditText)findViewById(R.id.register_name);
		final EditText username = (EditText)findViewById(R.id.register_username);
		final EditText password = (EditText)findViewById(R.id.register_password);
		final EditText confirmPassword = (EditText)findViewById(R.id.register_confirm_password);
		
		new Thread(new Runnable(){
			@SuppressLint("TrulyRandom") public void run()
			{
				try {
					KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
					kpg.initialize(2048);
					KeyPair kp = kpg.genKeyPair();
					String privateKey = Base64.encodeToString(kp.getPrivate().getEncoded(), Base64.DEFAULT);
					String publicKey = Base64.encodeToString(kp.getPublic().getEncoded(), Base64.DEFAULT);
					String nameString = name.getText().toString();
					String usernameString = username.getText().toString();
					String passwordString = password.getText().toString();
					API.Register(nameString, usernameString, passwordString, confirmPassword.getText().toString(), publicKey);
					User me = new User();
					me.name = nameString;
					me.username = usernameString;
					me.publicKey = publicKey;
					Settings.setMe(Register.this, me);
					Settings.setPrivateKey(Register.this, privateKey);
					Settings.setPassword(Register.this, passwordString);
					TokenResponse tr = API.Login(me.username, passwordString);
					Settings.setToken(Register.this, tr.access_token);
					Calendar c = Calendar.getInstance();
					c.add(Calendar.SECOND, tr.expires_in);
					Settings.setExpiry(Register.this, c.getTimeInMillis());
					Register.this.setResult(Activity.RESULT_OK);
					Register.this.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void cancelClick(View view)
	{
		Register.this.setResult(Activity.RESULT_CANCELED);
		Register.this.finish();
	}
}
