package com.hrc51.csu.andruino;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Indicators extends ListActivity {
	private ArrayList<AndruinoObj> indicators = null;
	private IOAdapter ctrl_adapter; 
    private SharedPreferences settings;
	private Webduino wc;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indicators);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
		wc = new Webduino(settings);
        indicators = filterControls(wc.read());
        
        ctrl_adapter = new IOAdapter(this, R.layout.indicator_row, indicators);
        setListAdapter(ctrl_adapter);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Toast.makeText(this, "Just a test", Toast.LENGTH_SHORT).show();

		switch (item.getItemId()) {
		// We have only one menu option
		case R.id.settings:
			// Launch Preference activity
			Intent i = new Intent(this, Settings.class);
			startActivity(i);
			// Some feedback to the user
			Toast.makeText(this,
					"Here you can maintain your server settings and user credentials.",
					Toast.LENGTH_LONG).show();
			break;
		case R.id.refresh:

			break;
		}

		return true;
	}
	
	public ArrayList<AndruinoObj> filterControls(ArrayList<AndruinoObj> controls) {
		ArrayList<AndruinoObj> indicators = new ArrayList<AndruinoObj>();
		
		for(AndruinoObj obj : controls)
		{
			if(obj.getDdr() == 1)
				indicators.add(obj);
		}
		return indicators;
	}
}
