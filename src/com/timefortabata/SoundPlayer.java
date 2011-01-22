package com.timefortabata;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
//import android.media.MediaPlayer;

public class SoundPlayer implements IntervalListener {
	private SoundPool soundPool;
	private int startIntervalId;
	private int finishIntervalId;
	private int finishSetId;
	private int finishSessionId;
	private int abortSessionId;

	public SoundPlayer(Context context){
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		startIntervalId = soundPool.load(context, R.raw.boxing, 0);
		finishIntervalId = soundPool.load(context, R.raw.buzzer, 0);
		finishSetId = soundPool.load(context, R.raw.air_horn, 0);
		finishSessionId = soundPool.load(context, R.raw.claps, 0);
		abortSessionId = soundPool.load(context, R.raw.boo, 0);
	}

	public void handleIntervalEvent(IntervalEvent iv, IntervalInfo intervalInfo) {
		// TODO: Perhaps replace this with a map instead
		switch(iv){
			case FINISH_SET:
				playSound(finishSetId);
				break;
				
			case FINISH_SESSION:
				playSound(finishSessionId);
				break;
				
			case ABORT_SESSION:
				playSound(abortSessionId);
				break;
				
			case FINISH_INTERVAL:
				playSound(finishIntervalId);
				break;
				
			case START_INTERVAL:
				playSound(startIntervalId);
				break;

			default:
				// Don't care about these events for now
			break;
		}
	}
	
	private void playSound(int soundId){
		soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
	}
}