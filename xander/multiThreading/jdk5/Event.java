package xander.multiThreading.jdk5;



import java.util.*;



/**
 *
 *
 * 
**/
public interface Event {
	
	public String getEventId();
	
	public String getEventType();
	
	public Calendar getEventTimestamp();

}
