package com.zacro.sanginebapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanginebapp.R;

public class MainActivity extends Activity {
	private int time = 1;
	private final int MIN_TIME = 1;
	private final int MAX_TIME = 60;
	private TextView txtCounter, txtTime;
	private SeekBar barTime;
	private Button btnBan;
	private MediaPlayer sndHammer, sndSangine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtCounter = (TextView) findViewById(R.id.texto_contador);
		txtTime = (TextView) findViewById(R.id.texto_tiempo);
		barTime = (SeekBar) findViewById(R.id.barra_tiempo);
		btnBan = (Button) findViewById(R.id.btn_banear);
		sndHammer = MediaPlayer.create(this, R.raw.punch);
		sndSangine = MediaPlayer.create(this, R.raw.sangine2);

		// Configuracion slider de tiempo:
		barTime.setMax(MAX_TIME);
		barTime.setThumbOffset(MIN_TIME);
		txtTime.setText(barTime.getThumbOffset() + " "
				+ getText(R.string.txt_segundos));

		// Escuchadores de eventos para las vistas:
		btnBan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sndHammer.start();
				sndSangine.start();
				BanSangineThread hiloBanear = new BanSangineThread();
				hiloBanear.start();
			}
		});

		barTime
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar arg0, int progreso,
							boolean arg2) {
						// Se evita que el tiempo sea menor que 1
						if (progreso < MIN_TIME) {
							time = MIN_TIME;
						} else {
							time = progreso;
						}

						txtTime.setText(time + " "
								+ getText(R.string.txt_segundos));
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub

					}
				});

		// Inicializacion del contador:
		GetBansThread hiloConexion = new GetBansThread();
		hiloConexion.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			GetBansThread hiloConexion = new GetBansThread();
			hiloConexion.start();
			return true;
		case R.id.menu_item_share:
			shareButtonAction();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * 
	 */
	public void shareButtonAction() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = getString(R.string.msg_share, time);
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BANgine");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent,
				getString(R.string.action_share)));
	}

	class GetBansThread extends Thread {
		private String contador = "";

		@Override
		public void run() {
			try {
				URL url = new URL(
						"http://94.23.205.21/sanginebapp/get_bans.php");
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

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							txtCounter.setText(contador);
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							txtCounter.setText("Sin conexión");
						}
					});
				}
			} catch (MalformedURLException urlexception) {
				urlexception.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class BanSangineThread extends Thread {
		@Override
		public void run() {
			if (!txtCounter.getText().equals("")) {
				try {
					URL url = new URL(
							"http://94.23.205.21/sanginebapp/banear.php?time="
									+ time);

					HttpURLConnection conexion = (HttpURLConnection) url
							.openConnection();
					conexion.setRequestProperty("User-Agent",
							"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");

					if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
						String mensaje = "";
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(conexion.getInputStream()));
						String linea = reader.readLine();

						while (linea != null) {
							mensaje += linea;
							linea = reader.readLine();
						}
						reader.close();

						if (!mensaje.contains("Error")) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(
											MainActivity.this,
											"¡ZAS! Un ban menos para la cuenta",
											Toast.LENGTH_SHORT).show();
								}
							});
						} else {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(MainActivity.this,
											"Sangine no está conectado :(",
											Toast.LENGTH_SHORT).show();
								}
							});
						}

						// Actualizacion del contador:
						GetBansThread hiloConexion = new GetBansThread();
						hiloConexion.start();

					} else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(MainActivity.this,
										"ERROR al conectar", Toast.LENGTH_LONG)
										.show();
							}
						});
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								MainActivity.this,
								"WTF, ¡a Sangine no le quedan banes pendientes!",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

}