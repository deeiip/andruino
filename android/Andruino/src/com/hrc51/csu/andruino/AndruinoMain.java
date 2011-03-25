package com.hrc51.csu.andruino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AndruinoMain extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this, Outputs.class));
	}
}