package com.hrc51.csu.andruino;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Outputs extends ListActivity {
	private ArrayList<AndruinoObj> allControls;
	private ArrayList<AndruinoObj> deviceOutputs;
	private IOAdapter ctrl_adapter; 
	private SharedPreferences settings;
	private Webduino wc;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outputs);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
		wc = new Webduino(settings);
        allControls = wc.read();
		deviceOutputs = filterControls(allControls);
        
        ctrl_adapter = new IOAdapter(this, R.layout.output_row, deviceOutputs, wc);
        setListAdapter(ctrl_adapter);
        ListView outputList = getListView();

        registerForContextMenu(outputList);   
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
	        allControls = wc.read();
			deviceOutputs = filterControls(allControls);
	        ctrl_adapter = new IOAdapter(this, R.layout.output_row, deviceOutputs, wc);
	        setListAdapter(ctrl_adapter);
	        registerForContextMenu(this.getListView());
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
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
      LinearLayout tView = (LinearLayout)info.targetView;
      TextView outName = (TextView)tView.findViewById(R.id.output_name);
      menu.setHeaderTitle(outName.getText().toString());
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	  
	  return super.onContextItemSelected(item);
	}
		
	public ArrayList<AndruinoObj> filterControls(ArrayList<AndruinoObj> controls) {
		ArrayList<AndruinoObj> deviceOutputs = new ArrayList<AndruinoObj>();
		
		for(int i = 0; i < controls.size(); i++)
		{
			AndruinoObj obj = controls.get(i);
			if (obj.getDdr() == 1) 
				deviceOutputs.add(obj);
		}
		return deviceOutputs;
	}
}