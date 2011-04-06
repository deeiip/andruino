package com.hrc51.csu.andruino;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class IOAdapter extends ArrayAdapter<AndruinoObj> {
    private ArrayList<AndruinoObj> controls;
    private Context context;
    private int textViewResourceId;
    private Webduino wc;

    public IOAdapter(Context context, int textViewResourceId, ArrayList<AndruinoObj> controls, Webduino wc) {
            super(context, textViewResourceId, controls);
            this.context = context;
            this.textViewResourceId = textViewResourceId;
            this.controls = controls;
            this.wc = wc;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(textViewResourceId, null);
            }
            
            final AndruinoObj andrObj = controls.get(position);
           
            switch(textViewResourceId)
            {
            case R.layout.indicator_row:
            	if (andrObj != null) {
                    TextView indicatorName = (TextView) v.findViewById(R.id.indicator_name);
                    TextView deviceName = (TextView) v.findViewById(R.id.device_name_ind);
                    CheckBox notify = (CheckBox)v.findViewById(R.id.notify);
                    if (indicatorName != null) 
                          indicatorName.setText(andrObj.getLabel());                            
                    if(deviceName != null)
                          deviceName.setText("Device: " + andrObj.getDevice());
                    if(notify != null)
                    	//notify.setChecked(andrObj.isNotify());
                    	notify.setChecked(true);
	            }
            	break;
            
            case R.layout.output_row:
	            if (andrObj != null) {
                    TextView outputName = (TextView) v.findViewById(R.id.output_name);
                    TextView deviceName = (TextView) v.findViewById(R.id.device_name_out);
                    ToggleButton tb = (ToggleButton)v.findViewById(R.id.change_state);
                    if (outputName != null) 
                          outputName.setText(andrObj.getLabel());                            
                    if(deviceName != null){
                          deviceName.setText("Device: " + andrObj.getDevice());
                    }
                    if(tb != null){
                    	tb.setChecked(andrObj.getValue() == 1 ? true : false);
                    	tb.setOnClickListener(new OnClickListener() {
                    	    public void onClick(View v) {
                    	    	andrObj.setValue(andrObj.getValue()== 1 ? 0 : 1);
                    	    	wc.login();
                    	        wc.write(andrObj.getId(), andrObj.getValue());
                    	        Toast.makeText(context, "You have changed the state of "+andrObj.getLabel()+ " to "+
                    	        		(andrObj.getValue()), Toast.LENGTH_SHORT).show();
                    	    }
                    	});
                    }
	            }
            	break;
            }
	        return v;
    }
}