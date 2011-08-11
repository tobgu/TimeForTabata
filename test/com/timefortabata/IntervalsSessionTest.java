package com.timefortabata;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import org.junit.Test;

import com.timefortabata.IntervalListener.IntervalEvent;

public class IntervalsSessionTest {

	@Test
	public void TraverseAllStatesTest()
	{
		int workTime = 2;
		int restTime = 1;
		int intervalNo = 4;
		int setRestTime = 3;
		int setNo = 2;
		IntervalSession is = new IntervalSession(workTime,
												 restTime,
												 intervalNo,
												 setRestTime,
												 setNo, null);
		IntervalListener il = mock(IntervalListener.class);
		is.addListener(il);
		
		for(int i=0; i<100; i++){
			is.handleTimeout();
		}
		
//		verify(il, times(8)).handleIntervalEvent(IntervalEvent.START_INTERVAL, 2);
//		verify(il, times(6)).handleIntervalEvent(IntervalEvent.FINISH_INTERVAL, 1);
//		verify(il, times(1)).handleIntervalEvent(IntervalEvent.FINISH_INTERVAL, 3);
//		verify(il, times(1)).handleIntervalEvent(IntervalEvent.TIMER_UPDATE, 2);
//		verify(il, times(1+8)).handleIntervalEvent(IntervalEvent.TIMER_UPDATE, 1);
//		verify(il, times(1+8+6)).handleIntervalEvent(IntervalEvent.TIMER_UPDATE, 0);
//		verify(il, times(1)).handleIntervalEvent(IntervalEvent.FINISH_SET, 3);
//		verify(il, times(1)).handleIntervalEvent(IntervalEvent.FINISH_SESSION, 0);
	}
	
	@Test
	public void TraverseAllStatesZeroDurationTest()
	{
		int workTime = 0;
		int restTime = 0;
		int intervalNo = 2;
		int setRestTime = 0;
		int setNo = 2;
		IntervalSession is = new IntervalSession(workTime,
												 restTime,
												 intervalNo,
												 setRestTime,
												 setNo, null);
		IntervalListener il = mock(IntervalListener.class);
		is.addListener(il);
		
		for(int i=0; i<100; i++){
			is.handleTimeout();
		}
		
//		verify(il, times(4)).handleIntervalEvent(IntervalEvent.START_INTERVAL, 0);
//		verify(il, times(3)).handleIntervalEvent(IntervalEvent.FINISH_INTERVAL, 0);
//		verify(il, times(1)).handleIntervalEvent(IntervalEvent.FINISH_SET, 0);
//		verify(il, times(1)).handleIntervalEvent(IntervalEvent.FINISH_SESSION, 0);
//		verify(il, times(0)).handleIntervalEvent(IntervalEvent.TIMER_UPDATE, 0);
	}
}
