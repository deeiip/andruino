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
import android.view.MotionEvent;
import android.widget.Toast;

public class Outputs extends ListActivity {
	private ArrayList<AndruinoObj> outputs;
	private IOAdapter ctrl_adapter; 
    private SharedPreferences settings;
	private Webduino wc;
	private float initialX, deltaX;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outputs);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
		wc = new Webduino(settings);
        outputs = filterControls(wc.read());
        
        ctrl_adapter = new IOAdapter(this, R.layout.output_row, outputs);
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
	
	public boolean onTouchEvent(MotionEvent event) {
        synchronized (event)  
        {   
        	//when user touches the screen  
            if(event.getAction() == MotionEvent.ACTION_DOWN)  
            {  
            	deltaX = 0;  
                initialX = event.getRawX();  
            }  
  
            //when screen is released  
            if(event.getAction() == MotionEvent.ACTION_UP)  
            {  
            	deltaX = event.getRawX() - initialX;  
  
            	//swiped from right to left
            	if(deltaX > 0)  
            		startActivity(new Intent(this, Indicators.class));  
            	return true;  
            }      
        }  
		return true;
	}
	
	public ArrayList<AndruinoObj> filterControls(ArrayList<AndruinoObj> controls) {
		ArrayList<AndruinoObj> outputs = new ArrayList<AndruinoObj>();
		
		for(AndruinoObj obj : controls)  /* app is crashing here */
		{
			if(obj.getDdr() == 0)
				outputs.add(obj);
		}
		return outputs;
	}
}