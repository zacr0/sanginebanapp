package com.example.sanginebapp;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView contador;
	Button btnBanear;
	MediaPlayer sndBoton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		contador = (TextView) findViewById(R.id.texto_contador);
		btnBanear = (Button) findViewById(R.id.btn_banear);
		sndBoton = MediaPlayer.create(this, R.raw.punch);

		btnBanear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sndBoton.start();
				postBans(null);
			}
		});
		
		getBans(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void getBans(View view) {
		new BansActivity(this).execute("sangine");
	}
	
	public void postBans(View view) {
		
	}

}
