package com.timefortabata;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.media.MediaPlayer;
import android.util.Log;

public class MusicPlayer implements IntervalListener {
    
    private class ListPlayer implements MediaPlayer.OnCompletionListener {
        private Iterator<File> playListIterator;
    	private List<File> playList;
        private MediaPlayer player; 

    	public ListPlayer(List<File> pL) {
    		playList = pL;
    		player = createPlayer();
    	}
    	
    	private MediaPlayer createPlayer()
    	{
    		if(!playList.isEmpty()){
    			player = new MediaPlayer();
    			player.setOnCompletionListener(this);
    			startPlayList();
    			return player;
    		}
    		else
    		{
    			// TODO: Perhaps replace player with a null
    			//       object in this case
    			return null;
    		}
    	}
    	
    	private void startPlayList(){
    		playListIterator = playList.listIterator();
    		continuePlayList();
    	}
    	
    	private void continuePlayList(){
    		File nextSong = playListIterator.next();
    		try{
    			player.setDataSource(nextSong.getCanonicalPath());
    			player.prepare();
    		}
    		catch(IOException ie){
    			Log.d("DEBUG", "Error trying to open file: " + nextSong);
    		}
    	}
    	
    	public void play(){
    		if(player != null){
    			player.start();
    		}
    	}
    	
    	public void pause(){
    		if(player != null){
    			if(player.isPlaying()){
    				player.pause();
    			}
    		}    		
    	}
    	
    	public void release(){
    		if(player != null){
    			player.release();
    		}
    	}
    	
    	public void onCompletion(MediaPlayer mp) {
			player.reset();
    		
			if(playListIterator.hasNext()){
    			continuePlayList();
    		}
    		else{
    			startPlayList();
    		}
    		
			play();
    	}
    }

	private ListPlayer intervalMp;
	private ListPlayer restMp;
    
	public MusicPlayer(List<File> iPL,
			           List<File> rPL){
		
		intervalMp = new ListPlayer(iPL);
		restMp = new ListPlayer(rPL);
	}
	
	public void handleIntervalEvent(IntervalEvent iv,
									IntervalSessionInfo intervalInfo) {
		switch(iv){
		case START_INTERVAL:
			playInterval();
			break;

		case FINISH_INTERVAL:
		case FINISH_SET:
			playRest();
			break;

		case FINISH_SESSION:
		case ABORT_SESSION:
			stopAll();
			break;
			
		default:
			// Don't care about these events for now
			break;
		}		
	}
	
	public void stopAll(){
		intervalMp.release();
		restMp.release();
	}
	
	public void playRest(){
		intervalMp.pause();
		restMp.play();
	}
	
	public void playInterval(){
		restMp.pause();
		intervalMp.play();
	}

//    startIntervalMp = MediaPlayer.create(context, R.raw.car_horn);
//    startIntervalMp.setOnCompletionListener(this);
//	intervalFinishedMp = MediaPlayer.create(context, R.raw.cymbal);
//	intervalFinishedMp.setOnCompletionListener(this);
//	setFinishedMp = MediaPlayer.create(context, R.raw.drum_solo);
//	setFinishedMp.setOnCompletionListener(this);
//	sessionFinishedMp = MediaPlayer.create(context, R.raw.claps);
//	sessionFinishedMp.setOnCompletionListener(this);
//	sessionAbortedMp = MediaPlayer.create(context, R.raw.boo);
//	sessionAbortedMp.setOnCompletionListener(this);

}
