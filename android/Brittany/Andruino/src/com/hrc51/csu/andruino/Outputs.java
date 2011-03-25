package com.hrc51.csu.andruino;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Outputs extends ListActivity {
	private ArrayList<AndruinoObj> controls = null;
    private ControlAdapter ctrl_adapter; 
    private SharedPreferences settings;
	private Webduino wc;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outputs);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
		wc = new Webduino(settings);
        controls = wc.read();
        
        ctrl_adapter = new ControlAdapter(this, R.layout.output_row, controls);
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
    
    private class ControlAdapter extends ArrayAdapter<AndruinoObj> {
        private ArrayList<AndruinoObj> controls;

        public ControlAdapter(Context context, int textViewResourceId, ArrayList<AndruinoObj> controls) {
                super(context, textViewResourceId, controls);
                this.controls = controls;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.output_row, null);
                }
                AndruinoObj a = controls.get(position);
                if (a != null) {
                        TextView tt = (TextView) v.findViewById(R.id.output_name);
                        TextView bt = (TextView) v.findViewById(R.id.device_name_out);
                        if (tt != null) 
                              tt.setText(a.getLabel());                            
                        if(bt != null){
                              bt.setText("Device: ");
                        }
                }
                return v;
        }
    }
}