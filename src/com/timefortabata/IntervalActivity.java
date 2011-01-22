package com.timefortabata;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntervalActivity extends Activity implements IntervalListener,
                                                          OnClickListener {
	// GUI update handler
    final Handler guiHandler = new Handler();
    private Button buttonStop;
	private boolean onDestroyCalled;
	private boolean sessionDone;
	private TextView measureTv;
	private static IntervalSession intervalSession = null;    
	public static void setIntervalSession(IntervalSession is){
		intervalSession = is;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interval);
        updateBackgroundInThread(R.color.working);
        
        // Used to measure size of text before displaying
        // it on screen
        measureTv = new TextView(getApplicationContext());
        
        // These variables are part of a bit of an ugly hack to
        // keep track of how the interval session should be stopped
        onDestroyCalled = false;
        sessionDone = false;
        
    	if(intervalSession != null){
    		buttonStop = (Button)findViewById(R.id.tabata_stop);
    		buttonStop.setOnClickListener(this);
        
    		intervalSession.addListener(this);
    	}
    	else{
    		finish();
    	}
    }

	public void handleIntervalEvent(IntervalEvent iv, IntervalInfo intervalInfo) {
		switch(iv){
		case ABORT_SESSION:
		case FINISH_SESSION:
			sessionDone = true;
			intervalSession = null;
			if(!onDestroyCalled){
				finish();
			}
			break;
			
		case FINISH_INTERVAL:
		case FINISH_SET:
			setBackground(R.color.resting);
			break;
		
		case START_INTERVAL:
			setBackground(R.color.working);
			break;
		
		default:
			// Do special nothing right now
			break;
		}
		
		updateIntervalInformation(intervalInfo);
	}
	
	private void updateIntervalInformation(final IntervalInfo intervalInfo){
		guiHandler.post(new Runnable(){
			public void run(){
				TextView tv = (TextView) findViewById(R.id.time_left_text);
				setTextInTextView(tv,
								  Integer.toString(intervalInfo.timeLeft),
								  400, 40);
								
				tv = (TextView) findViewById(R.id.intervals_left);
				setTextInTextView(tv,
						  		  Integer.toString(intervalInfo.intervalsInSetLeft),
						  		  160, 20);

				tv = (TextView) findViewById(R.id.sets_left);
				setTextInTextView(tv,
				  		  		  Integer.toString(intervalInfo.setsLeft),
				  		  		  160, 20);
			}
		});		
	}
	
	private void setTextInTextView(TextView tv,
								   String text,
								   int textSize, 
								   int padding){	
		// Take the maximum width string for the number of characters
		// to present to avoid jumping back and forth between text
		// sizes depending on the width of the characters 
		// (for example 112 -> 111 -> 110)
		String maxText = fillString('0', text.length());
		
		// Measure and adjust text size if needed
		Rect r = new Rect();
		while(true){
			measureTv.setTextSize(textSize);
			measureTv.getPaint().getTextBounds(maxText, 0, maxText.length(), r);
			if((r.width() > (tv.getWidth() - padding))
				|| (r.height() > tv.getHeight())){
				textSize /= 2;
			}
			else{
				break;
			}
		}
		
		// Finally set the text with the calculated size
		tv.setTextSize(textSize);
		tv.setText(text);
	}
	
    public static String fillString(char fillChar, int count){
        // creates a string of 'x' repeating characters
        char[] chars = new char[count];
        while (count>0) chars[--count] = fillChar;
        return new String(chars);
    }

	
	private void setBackground(final int colorResource){
		guiHandler.post(new Runnable(){
			public void run(){
				updateBackgroundInThread(colorResource);
			}
		});
	}
	
	private void updateBackgroundInThread(final int colorResource){
		LinearLayout l = 
			   (LinearLayout) findViewById(R.id.interval_layout);
		l.setBackgroundResource(colorResource);
		l.invalidate();		
	}

	public void onClick(View v) {
		if(v == buttonStop){
            intervalSession.abort();
		}			
	}
	
	public void onDestroy(){
		super.onDestroy();
		onDestroyCalled = true;
		if(!sessionDone){
			intervalSession.abort();
		}
	}
}
