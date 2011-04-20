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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
        ctrl_adapter = new IOAdapter(this, R.layout.indicator_row, deviceIndicators, wc);
        setListAdapter(ctrl_adapter);
        ListView indicatorList = getListView();

        registerForContextMenu(indicatorList);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.settings:
			// Launch Preference activity
			startActivity(new Intent(this, Settings.class));
			// Some feedback to the user
			Toast.makeText(this,
					"Here you can maintain your server settings and user credentials.",
					Toast.LENGTH_LONG).show();
			break;
			
		case R.id.refresh:
	        deviceIndicators = filterControls(wc.read(), selectedDevice);
	        ctrl_adapter = new IOAdapter(this, R.layout.indicator_row, deviceIndicators, wc);
	        setListAdapter(ctrl_adapter);
	        registerForContextMenu(this.getListView());
			break;
			
		case R.id.help:
			//startActivity(new Intent(this, Help.class));
			Toast.makeText(this,
					"Server:" +settings.getString("serverurl", "csu.hrc51.com"),
					Toast.LENGTH_LONG).show();
			break;
		}

		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  
	  return super.onContextItemSelected(item);
	}
	
	public ArrayList<AndruinoObj> filterControls(ArrayList<AndruinoObj> controls, String selectedDevice) {
		ArrayList<AndruinoObj> deviceIndicators = new ArrayList<AndruinoObj>();
		
		for(int i = 0; i < controls.size(); i++)
		{
			AndruinoObj obj = controls.get(i);
			if(obj.getDdr() == 0 && obj.getDevice().equals(selectedDevice))
				deviceIndicators.add(obj);
		}
		return deviceIndicators;
	}
	
	public ArrayList<String> getDeviceNames(ArrayList<AndruinoObj> allControls) {
		ArrayList<String> deviceNames = new ArrayList<String>();
		
		for(int i = 0; i < allControls.size(); i++)
		{
			AndruinoObj obj = allControls.get(i);
			String device = obj.getDevice();
			if(!deviceNames.contains(device))
				deviceNames.add(device);
		}
		return deviceNames;
	}
}
