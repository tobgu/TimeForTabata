package com.timefortabata;

public interface IntervalListener {
	public enum IntervalEvent { START_INTERVAL,
		                        FINISH_INTERVAL, 
		                        FINISH_SET,
		                        FINISH_SESSION,
		                        TIMER_UPDATE, 
		                        ABORT_SESSION};
	
	void handleIntervalEvent(IntervalEvent iv, IntervalInfo intervalInfo);

}
