package com.hrc51.csu.andruino;

import java.util.ArrayList;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

public class Outputs extends ListActivity {
	private ArrayList<AndruinoObj> allControls;
	private ArrayList<AndruinoObj> deviceOutputs;
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
        allControls = wc.read();
		deviceOutputs = filterControls(allControls);
        
        ctrl_adapter = new IOAdapter(this, R.layout.output_row, deviceOutputs);
        setListAdapter(ctrl_adapter);
        registerForContextMenu(this.getListView());
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
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
			
		case R.id.help:
			startActivity(new Intent(this, Help.class));
			break;
		}

		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.context_menu, menu);
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  
	  return super.onContextItemSelected(item);
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
		ArrayList<AndruinoObj> deviceOutputs = new ArrayList<AndruinoObj>();
		
		for(int i = 0; i < controls.size(); i++)
		{
			AndruinoObj obj = controls.get(i);
			if (obj.getDdr() == 0) 
				deviceOutputs.add(obj);
		}
		return deviceOutputs;
	}
}