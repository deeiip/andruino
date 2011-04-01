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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Indicators extends ListActivity {
	private ArrayList<AndruinoObj> allControls;
	private ArrayList<AndruinoObj> deviceIndicators;
	private ArrayList<String> deviceNames;
	private String selectedDevice;
	private IOAdapter ctrl_adapter; 
    private SharedPreferences settings;
	private Webduino wc;
	private Spinner selectDevice;
	private float initialX, deltaX;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indicators);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
		wc = new Webduino(settings);
		allControls = wc.read();
		deviceNames = getDeviceNames(allControls);
		
        selectDevice = (Spinner)findViewById(R.id.selectDevice);
        selectDevice.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, deviceNames));
        selectDevice.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> arg0, 
            View arg1, int arg2, long arg3) 
            {
                int index = selectDevice.getSelectedItemPosition();
                selectedDevice = deviceNames.get(index).toString();
//                Toast.makeText(getBaseContext(), 
//                        "You have selected item: " + selectedDevice, 
//                        Toast.LENGTH_SHORT).show();
            }
 
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        
        
        deviceIndicators = filterControls(allControls, selectedDevice);
        ctrl_adapter = new IOAdapter(this, R.layout.indicator_row, deviceIndicators);
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
  
        		//swiped from left to right  
        		if(deltaX < 0)   
        			startActivity(new Intent(this, Outputs.class)); 
  
        		return true;  
                }      
        }  
		return true;
	}
	
	public ArrayList<AndruinoObj> filterControls(ArrayList<AndruinoObj> controls, String selectedDevice) {
		ArrayList<AndruinoObj> deviceIndicators = new ArrayList<AndruinoObj>();
		
		for(AndruinoObj obj : controls)
		{
			if(obj.getDdr() == 1)
				deviceIndicators.add(obj);         
		}
		return deviceIndicators;
	}
	
	public ArrayList<String> getDeviceNames(ArrayList<AndruinoObj> allControls) {
		ArrayList<String> deviceNames = new ArrayList<String>();
		
		for(AndruinoObj obj: allControls) /* app is crashing here */
		{
			String device = obj.getDevice();
			if(!deviceNames.contains(device))
				deviceNames.add(obj.getDevice());
		}
		return deviceNames;
	}
}
