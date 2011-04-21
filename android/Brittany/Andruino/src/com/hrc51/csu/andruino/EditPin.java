package com.hrc51.csu.andruino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class EditPin extends Activity {
	public static final String DIALOG_NAME="EditPin";
//	
//	public static final String KEY_LABEL="title";
//	public static final String KEY_PLAY_MUSIC="play_music";
//	public static final String KEY_MEMO="memo";
//	
	private EditText newLabel;
//	private static EditText mTextMemo;
//	private static CheckBox mCheckMusic;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_pin);
		
	    newLabel = (EditText) findViewById(R.id.new_label);
	    newLabel.setSelectAllOnFocus(true);
	    newLabel.setText(getIntent().getExtras().getString("pinName"));
	//    mTextMemo = (EditText) findViewById(R.id.memo);
	//    mCheckMusic = (CheckBox) findViewById(R.id.play_music);
	
	    findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
	      public void onClick(View view) {
	        Bundle bundleData = new Bundle();
	        String newText = newLabel.getText().toString();
	        
	        bundleData.putString("new", newText);
			Intent intent = new Intent();
	        intent.putExtras(bundleData);
			
	        if (getParent() == null) {
	            setResult(Activity.RESULT_OK, intent);
	        } else {
	            getParent().setResult(Activity.RESULT_OK, intent);
	        }
	        setResult(RESULT_OK, intent);
	        finish();
      }
    });
	    
	    findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
		      public void onClick(View view) {
		        finish();
	      }
	    });

  }
}
