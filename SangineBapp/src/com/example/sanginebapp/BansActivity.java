package com.example.sanginebapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

public class BansActivity extends AsyncTask<String, Void, String> {
	private Context context;
	private TextView contador;
	
	public BansActivity(Context context) {
		this.context = context;
		this.contador = contador;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String user = (String) params[0];
			String link = "http://localhost/sanginebapp/show_ban.php?user=" + user;
			URL url = new URL(link);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(link));
			HttpResponse response = client.execute(request);
			BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
				break;
			}
			
			in.close();
			return sb.toString();
		} catch(Exception e) {
			return new String("Exception: " + e.getMessage());
		}
	}
	
	@Override
	protected void onPostExecute(String result) {
		this.contador.setText(result);
	}
}
