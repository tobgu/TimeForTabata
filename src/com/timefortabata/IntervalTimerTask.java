package com.timefortabata;

import java.util.TimerTask;

public class IntervalTimerTask extends TimerTask {
	private IntervalTimerHandler timerHandler;
	
	public IntervalTimerTask(IntervalTimerHandler t){
		timerHandler = t;
	}
	
	@Override
	public void run() {
		timerHandler.handleTimeout();
	}

}
