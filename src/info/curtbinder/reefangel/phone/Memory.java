package info.curtbinder.reefangel.phone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class Memory extends Activity {
	
	final static int LOCATION_START = 800;
	final static int TYPE_BYTE = 0;
	final static int TYPE_INT = 1;
	
	private Spinner locationSpinner;
	private EditText locationText;
	private EditText valueText;
	private Button readButton;
	private Button writeButton;
	private RadioButton byteButton;
	private RadioButton intButton;
	private int [] memoryLocations;
	private int [] memoryLocationsTypes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.memory);
		
		findViews();
		setAdapters();
		setOnClickListeners();
		
		memoryLocations = getResources().getIntArray(R.array.memoryLocations);
		memoryLocationsTypes = getResources().getIntArray(R.array.memoryLocationsTypes);
		setInitialValues();
	}
	
	private void findViews() {
		locationSpinner = (Spinner) findViewById(R.id.spinMemoryLocation);
		locationText = (EditText) findViewById(R.id.locationText);
		valueText = (EditText) findViewById(R.id.valueText);
		readButton = (Button) findViewById(R.id.buttonRead);
		writeButton = (Button) findViewById(R.id.buttonWrite);
		byteButton = (RadioButton) findViewById(R.id.radioButtonByte);
		intButton = (RadioButton) findViewById(R.id.radioButtonInt);
	}
	
	private void setAdapters() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.memoryLocationsNames,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		locationSpinner.setAdapter(adapter);
	}
	
	private void setOnClickListeners() {
		// create on click listeners
		readButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});
		writeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});
		locationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				setItemSelected((int)id);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	private void setItemSelected(int id) {
		boolean enable = false;
		if ( id > 0 ) {
			String s = new String(String.format("%d", LOCATION_START + memoryLocations[id]));
			locationText.setText(s);
		} else {
			locationText.setText("");
		}
		// update the radio button
		if ( memoryLocationsTypes[id] == TYPE_BYTE ) {
			byteButton.setChecked(true);
			intButton.setChecked(false);
		} else {
			byteButton.setChecked(false);
			intButton.setChecked(true);
		}
		
		if ( id == 0 ) {
			enable = true;
		}
		updateLocationEditability(enable);	
	}
	
	private void setInitialValues() {
		locationSpinner.setSelection(1);
		setItemSelected(1);
	}
	
	private void updateLocationEditability(boolean enable) {
		// updates the enabling/disabling of the location & radio buttons based
		// on the location drop down menu
		byteButton.setEnabled(enable);
		intButton.setEnabled(enable);
		locationText.setEnabled(enable);
		if ( enable ) {
			locationText.requestFocus();
		} else {
			valueText.requestFocus();
		}
	}
}
