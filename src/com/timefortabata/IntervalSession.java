package com.timefortabata;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class IntervalSession implements IntervalTimerHandler{

	private int workPeriod;
	
	private int intervalNo;
	private int intervalRestPeriod;
	private int intervalsLeftInSet;
	
	private int setNo;
	private int setRestPeriod;
	private int setsLeftInSession;
		                         
	private IntervalSessionState sessionState;
	private List<IntervalListener> intervalListeners;
	private Timer intervalTimer = null;
	
	// Session states
	private abstract class IntervalSessionState
	{
		protected int timeLeftInState;
		
		public IntervalSessionState(int statePeriod){
			timeLeftInState = statePeriod;
		}		
		
		public final void tickTimer(){
			if(timeLeftInState > 0){
				timeLeftInState--;
				notifyListeners(IntervalListener.IntervalEvent.TIMER_UPDATE,
						        timeLeftInState);
			}
		}
		
		public final IntervalSessionState updateStateIfTimeout(){
			if(timeLeftInState == 0){
				// Some special handling needed here for states
				// that have  duration of zero timer ticks to
				// allow that they are passed immediately
				IntervalSessionState newState = this.nextState();
				if(newState != this){
					return newState.updateStateIfTimeout();
				}
				else{
					return newState;
				}
			}
			else{
				return this;
			}
		}
		
		abstract protected IntervalSessionState nextState();
	}
	
	private class WorkState extends IntervalSessionState{
		public WorkState(){
			super(workPeriod);
			notifyListeners(IntervalListener.IntervalEvent.START_INTERVAL,
					        workPeriod);
		}

		protected IntervalSessionState nextState(){
			intervalsLeftInSet--;
			if(intervalsLeftInSet == 0){
				setsLeftInSession--;
				if(setsLeftInSession == 0){
					return new FinishState();
				}
				else{
					return new SetRestState();
				}
			}
			else{
				return new IntervalRestState();
			}			
		}
	}

	private class FinishState extends IntervalSessionState{
		public FinishState(){
			super(0);
			notifyListeners(IntervalListener.IntervalEvent.FINISH_SESSION,
					        0);
		}

		public IntervalSessionState nextState(){
			return this;
		}
	}

	private class SetRestState extends IntervalSessionState
	{
		public SetRestState(){
			super(setRestPeriod);
			notifyListeners(IntervalListener.IntervalEvent.FINISH_SET,
					        setRestPeriod);
		}

		public IntervalSessionState nextState(){
			intervalsLeftInSet = intervalNo;
			return new WorkState();
		}
	}

	private class IntervalRestState extends IntervalSessionState
	{
		public IntervalRestState(){
			super(intervalRestPeriod);
			notifyListeners(IntervalListener.IntervalEvent.FINISH_INTERVAL,
					        intervalRestPeriod);
		}

		public IntervalSessionState nextState(){
			return new WorkState();
		}
	}
	
	private class StartState extends IntervalSessionState{
	
		public StartState(){
			super(0);
		}
	
		public IntervalSessionState nextState(){
			if((intervalNo > 0) && (setNo > 0)){
				intervalsLeftInSet = intervalNo;
				setsLeftInSession = setNo;
				return new WorkState();
			}
			else{
				return new FinishState();
			}
		}
	}
	
	public IntervalSession(IntervalSessionDescription isd){
		workPeriod = isd.workPeriod;
		intervalRestPeriod = isd.interIntervalRestPeriod;
		intervalNo = isd.intervalNo;
		
		setRestPeriod = isd.interSetRestPeriod;
		setNo = isd.setNo;
		
		intervalListeners = new ArrayList<IntervalListener>();
		sessionState = new StartState();
	}
	
	public void addListener(IntervalListener il){
		intervalListeners.add(il);
	}

	public void start(){
		// Session can only be started once
		if(intervalTimer == null){
		  intervalTimer = new Timer();
		  intervalTimer.scheduleAtFixedRate(new IntervalTimerTask(this),
				  							0,
				  							1000);
		}
	}
	
	public void abort(){
		stopTimer();
		notifyListeners(IntervalListener.IntervalEvent.ABORT_SESSION, 0);
	}
	
	public void stopTimer(){
		if(intervalTimer != null){
			intervalTimer.cancel();
			intervalTimer.purge();
		}
	}
	
	public void handleTimeout() {
		sessionState.tickTimer();
		sessionState = sessionState.updateStateIfTimeout();		
	}
		
	private void notifyListeners(IntervalListener.IntervalEvent ie,
			                     int timeLeftInState){
		IntervalSessionInfo ii = new IntervalSessionInfo();
		ii.intervalsInSetLeft = intervalsLeftInSet;
		ii.setsLeft = setsLeftInSession;
		ii.timeLeft = timeLeftInState;
		for(IntervalListener il : intervalListeners){
			il.handleIntervalEvent(ie, ii);
		}
	}
}
