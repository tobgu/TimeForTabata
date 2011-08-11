package com.timefortabata;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.openintents.intents.FileManagerIntents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

public class TimeForTabata extends Activity {
	
	private SoundPlayer soundPlayer;
	private EditText rEditText;
	private EditText wEditText;
	protected static final int SELECT_REST_MUSIC = 1;
	protected static final int SELECT_WORK_MUSIC = 2;
	protected static final int START_MARKET = 2;
	private EditText wtet;
	private EditText rtet;
	private EditText inet;
	private EditText srtet;
	private EditText snet;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_for_tabata_main);
        
        ScrollView sView = 
        	(ScrollView)findViewById(R.id.time_for_tabata_scroll_view);
        sView.setVerticalScrollBarEnabled(false);
        sView.setHorizontalScrollBarEnabled(false);
		
        wtet = (EditText)findViewById(R.id.work_time);
		rtet = (EditText)findViewById(R.id.rest_time);
		inet = (EditText)findViewById(R.id.repetition_no);
		srtet = (EditText)findViewById(R.id.tabata_rest_time);
		snet = (EditText)findViewById(R.id.tabata_no);
		
        Button buttonStart = (Button)findViewById(R.id.tabata_start);
        buttonStart.setOnClickListener(new OnClickListener()
                {
        			public void onClick(View v) { startIntervals(); }
                });
        
		rEditText = (EditText)findViewById(R.id.rest_music);
        ImageButton buttonRestMusic = 
        	(ImageButton)findViewById(R.id.rest_music_choose);
        buttonRestMusic.setOnClickListener(new OnClickListener() {
        			public void onClick(View v) { 
        				selectMusic(rEditText, SELECT_REST_MUSIC);
        				}
                });
		
		wEditText = (EditText)findViewById(R.id.work_music);
        ImageButton buttonWorkMusic = 
        	(ImageButton)findViewById(R.id.work_music_choose);
        buttonWorkMusic.setOnClickListener(new OnClickListener() {
			public void onClick(View v) { 
				selectMusic(wEditText, SELECT_WORK_MUSIC);
				}
        });        
        
        soundPlayer = new SoundPlayer(getApplicationContext());
    }

    private void selectMusic(EditText et, int orderCode){
		String fileName = et.getText().toString();

    	Intent intent = new Intent(FileManagerIntents.ACTION_PICK_FILE);

    	// Construct URI from file name.
    	intent.setData(Uri.parse("file://" + fileName));

    	// Set fancy title and button (optional)
    	intent.putExtra(FileManagerIntents.EXTRA_TITLE, "Choose MP3, M3U or directory");
    	intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, "Select");

    	try {
    		startActivityForResult(intent, orderCode);
    	} catch (ActivityNotFoundException e) {
    		showFileManageInstallAlert();
    	}
    }
    
    private void showFileManageInstallAlert(){
    	Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("No compatible file manager found")
    			.setMessage("No compatible file manager was found. " +
				"Do you want to go to Market to install the Open Intent " +
				" File Manager?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=org.openintents.filemanager"));
						try {
							startActivityForResult(intent, START_MARKET);
						} catch (ActivityNotFoundException e) {
							errorAlert("Unable to start Market", "Could not start Android Market");
						}
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;
					} });
    	AlertDialog alertDialog = builder.create();
		alertDialog.show();    	
    }
    
	private void startIntervals() {
		String workTimeText = wtet.getText().toString();
		String restTimeText = rtet.getText().toString();
		String intervaNoText = inet.getText().toString();
		String setRestTimeText = srtet.getText().toString();
		String setNoText = snet.getText().toString();

		// Input check
		if(workTimeText.equals("")){
			errorAlert("Field error", "Please enter work time");
			return;
		}
		if(restTimeText.equals("")){
			errorAlert("Field error", "Please enter rest time");
			return;
		}
		if(intervaNoText.equals("")){
			errorAlert("Field error", "Please enter number of repetitions");
			return;
		}
		if(setRestTimeText.equals("")){
			errorAlert("Field error", "Please enter rest time between sets");
			return;
		}
		if(setNoText.equals("")){
			errorAlert("Field error", "Please enter number of sets");
			return;
		}

		int workTime = Integer.parseInt(workTimeText);
		int restTime = Integer.parseInt(restTimeText);
		int intervalNo = Integer.parseInt(intervaNoText);
		int setRestTime = Integer.parseInt(setRestTimeText);
		int setNo = Integer.parseInt(setNoText);

		toast("Starting");

		try{
			List<File> intervalList =
				PlaylistBuilder.buildPlayList(wEditText.getText().toString());
			List<File> restList = 
				PlaylistBuilder.buildPlayList(rEditText.getText().toString());

			MusicPlayer musicPlayer = new MusicPlayer(intervalList,
					restList);

			IntervalSessionDescription isd = new IntervalSessionDescription();
			isd.workPeriod = workTime;
			isd.interIntervalRestPeriod = restTime;
			isd.intervalNo = intervalNo;
			isd.interSetRestPeriod = setRestTime;
			isd.setNo = setNo;
			IntervalSession intervalSession = new IntervalSession(isd);

			// Create new activity to display interval information
			IntervalActivity.setIntervalSession(intervalSession);
			Intent displayIntervalIntent = new Intent(this, IntervalActivity.class);
			this.startActivity(displayIntervalIntent);

			intervalSession.addListener(musicPlayer);

			CheckBox playFxBox = (CheckBox)findViewById(R.id.play_sound_fx);
			if(playFxBox.isChecked()){
				intervalSession.addListener(soundPlayer);
			}

			intervalSession.start();
		}
		catch(FileNotFoundException fe){
			errorAlert("File not found",
					"File not found: " + fe.getMessage());
		}
		catch(IOException ie){
			errorAlert("Unsupported file type",
					"Unsupported file type: " + ie.getMessage());
		}
	}	

	private void errorAlert(String title, String message){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
			} }); 
		alertDialog.show();
	}
	
	private void toast(String msg) {
		Context context = getApplicationContext();
		Toast toast = 
			Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SELECT_WORK_MUSIC:
		case SELECT_REST_MUSIC:
			if ((resultCode == RESULT_OK) && (data != null)) {
				// obtain the filename
				String filename = data.getDataString();
				if (filename != null) {
					// Get rid of URI prefix:
					if (filename.startsWith("file://")) {
						filename = filename.substring(7);
					}
					
					if(requestCode == SELECT_WORK_MUSIC){
						wEditText.setText(Uri.decode(filename));
					}
					else{
						rEditText.setText(Uri.decode(filename));
					}
				}				
			}
			break;
			
		default:
			// Do nothing
			break;
		}
	}
}