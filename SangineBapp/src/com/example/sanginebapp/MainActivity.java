package com.example.sanginebapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView txtContador;
	Button btnBanear;
	MediaPlayer sndMartillazo, sndSangine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtContador = (TextView) findViewById(R.id.texto_contador);
		btnBanear = (Button) findViewById(R.id.btn_banear);
		sndMartillazo = MediaPlayer.create(this, R.raw.punch);
		sndSangine = MediaPlayer.create(this, R.raw.sangine2);

		btnBanear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sndMartillazo.start();
				sndSangine.start();
				postBans();
			}
		});

		// Inicializacion del contador:
		txtContador.setText(getBans());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public String getBans() {
		String contador = "";
		try {
			URL url = new URL(
					"http://entrepantallas.hol.es/sanginebapp/get_bans.php");
			HttpURLConnection conexion = (HttpURLConnection) url
					.openConnection();
			conexion.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");

			if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conexion.getInputStream()));
				String linea = reader.readLine();

				while (linea != null) {
					contador += linea;
					linea = reader.readLine();
				}
				reader.close();
			} else {
				Toast.makeText(this, "ERROR:" + conexion.getResponseMessage(),
						Toast.LENGTH_SHORT).show();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contador;
	}

	public void postBans() {
		if (!getBans().equals("")) {
			try {
				URL url = new URL(
						"http://entrepantallas.hol.es/sanginebapp/update_bans.php");
				HttpURLConnection conexion = (HttpURLConnection) url
						.openConnection();
				conexion.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");

				if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
					Toast.makeText(this, "¡ZAS! Un ban menos para la cuenta",
							Toast.LENGTH_SHORT).show();

					txtContador.setText(getBans());
				} else {
					Toast.makeText(this,
							"ERROR: " + conexion.getResponseMessage(),
							Toast.LENGTH_SHORT).show();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this,
					"WTF, ¡a Sangine no le quedan banes pendientes!",
					Toast.LENGTH_SHORT).show();
		}
	}
}