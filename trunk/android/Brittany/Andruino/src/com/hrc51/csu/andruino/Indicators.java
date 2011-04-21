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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
	private String newLabel;
	private AndruinoObj selectedObj;
	
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
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        LinearLayout tView = (LinearLayout)info.targetView;
        TextView indName = (TextView)tView.findViewById(R.id.indicator_name);
        menu.setHeaderTitle(indName.getText().toString());
    }

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  LinearLayout tView = (LinearLayout)info.targetView;
	  ImageView indState = (ImageView)tView.findViewById(R.id.indicate_state);
      TextView indName = (TextView)tView.findViewById(R.id.indicator_name);
      TextView devNameInd = (TextView)tView.findViewById(R.id.device_name_ind);
      String pinName = indName.getText().toString();
      selectedObj = getObjByName(pinName);
	  
	  switch(item.getItemId())
	  {
	  case R.id.edit_name:
		  // allow user to edit name of pin
		  Intent intent = new Intent(this, EditPin.class);
		  Bundle bundleData = new Bundle();
	      bundleData.putString("pinName", pinName);
	      intent.putExtras(bundleData);
		  this.startActivityForResult(intent, 0);
		  indName.setText(newLabel);
//		  wc.setLabel(selectedObj.getId(), newLabel);
		  break;
	  case R.id.disable_enable:
		  // grey out menu item if it's initially enabled
		  if(selectedObj.getEnabled() == 1)
		  {
//			  indState.setImageResource(R.drawable.control_disable);
//			  indName.setTextColor(Color.GRAY);
//			  devNameInd.setTextColor(Color.GRAY);
			  wc.config("disable", selectedObj.getId());
		  }
		  // else restore menu item if it's initially disabled
		  else
		  {
			  wc.config("enable", selectedObj.getId());
		  }
		  break;
		  
	  }
	  // refresh
	  deviceIndicators = filterControls(wc.read(), selectedDevice);
      ctrl_adapter = new IOAdapter(this, R.layout.indicator_row, deviceIndicators, wc);
      setListAdapter(ctrl_adapter);
      registerForContextMenu(this.getListView());
	 
	  return super.onContextItemSelected(item);
	}
	
	public ArrayList<AndruinoObj> filterControls(ArrayList<AndruinoObj> controls, String selectedDevice) {
		ArrayList<AndruinoObj> deviceIndicators = new ArrayList<AndruinoObj>();
		
		for(AndruinoObj obj : controls)
		{
			//AndruinoObj obj = controls.get(i);
			if(obj.getDdr() == 0 && obj.getDevice().equals(selectedDevice))
				deviceIndicators.add(obj);
		}
		return deviceIndicators;
	}
	
	public ArrayList<String> getDeviceNames(ArrayList<AndruinoObj> allControls) {
		ArrayList<String> deviceNames = new ArrayList<String>();
		
		for(AndruinoObj obj : allControls)
		{
			//AndruinoObj obj = allControls.get(i);
			String device = obj.getDevice();
			if(!deviceNames.contains(device))
				deviceNames.add(device);
		}
		return deviceNames;
	}
	
	public AndruinoObj getObjByName(String name) {
		AndruinoObj andrObj = new AndruinoObj();
		for(AndruinoObj obj : deviceIndicators)
		{
			if(obj.getLabel().equals(name))
				andrObj = obj;
		}
		
		return andrObj;
	}
	
	//@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//if (data.endsWith(EditPin.DIALOG_NAME)) {
		if(intent.getExtras().getString("new") != null && !intent.getExtras().getString("new").equals(""))
		{
			this.newLabel = intent.getExtras().getString("new");
			wc.setLabel(selectedObj.getId(), this.newLabel);
		}
		//}
	}
}
