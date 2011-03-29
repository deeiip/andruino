package com.hrc51.csu.andruino;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IOAdapter extends ArrayAdapter<String> {
    private ArrayList<String> controls;
    private Context context;

    public IOAdapter(Context context, int textViewResourceId, ArrayList<String> controls) {
            super(context, textViewResourceId, controls);
            this.context = context;
            this.controls = controls;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.output_row, null);
            }
            String a = controls.get(position);
            if (a != null) {
                    TextView tt = (TextView) v.findViewById(R.id.output_name);
                    TextView bt = (TextView) v.findViewById(R.id.device_name_out);
                    if (tt != null) 
                          tt.setText(a);                            
                    if(bt != null){
                          bt.setText("Device: ");
                    }
            }
            return v;
    }
}