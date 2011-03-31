package com.hrc51.csu.andruino;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Help extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        
        // defining views for this activity
        TextView helpIntro = (TextView)findViewById(R.id.helpIntro);
        TextView checkState = (TextView)findViewById(R.id.checkState);
        TextView enableNotification = (TextView)findViewById(R.id.enableNotification);
        TextView configureIndicator = (TextView)findViewById(R.id.configureIndicator);
        TextView addIndicator = (TextView)findViewById(R.id.addIndicator);
        TextView deleteIndicator = (TextView)findViewById(R.id.deleteIndicator);
        TextView changeState = (TextView)findViewById(R.id.changeState);
        TextView configureControl = (TextView)findViewById(R.id.configureControl);
        TextView addControl = (TextView)findViewById(R.id.addControl);
        TextView deleteControl = (TextView)findViewById(R.id.deleteControl);
        
        // defining information to be displayed in the TextViews
        String intro = "";
        String check = "";
        String enable = "";
        String configureI = "";
        String addI = "";
        String deleteI = "";
        String change = "";
        String configureC = "";
        String addC = "";
        String deleteC = "";
        
        // updating TextViews with information
        helpIntro.setText(intro);
        checkState.setText(check);
        enableNotification.setText(enable);
        configureIndicator.setText(configureI);
        addIndicator.setText(addI);
        deleteIndicator.setText(deleteI);
        changeState.setText(change);
        configureControl.setText(configureC);
        addControl.setText(addC);
        deleteControl.setText(deleteC);
    }
}
