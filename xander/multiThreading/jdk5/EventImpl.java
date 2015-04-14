package xander.multiThreading.jdk5;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/* @author Alexander Pereira, Croydon UK */
public final class EventImpl implements Event {
	
	private final UUID uuid ; // make sure that this immutable
	public static enum EvtState {INITIAL, CREATING, CREATED, CLOSING, CLOSED };
	public static final int EvtStateLASTPOS = EvtState.values().length - 1 ;
	private volatile String CURRSTATE ; // well this will have to be mutable - and will point to a String.intern heap var. which wld itself be immutable
	private final Calendar cal ;
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private final ReentrantLock setevntLck = new ReentrantLock();
	private final ReentrantLock prnevntLck = new ReentrantLock();
	
	
	private EventImpl() {
		this.uuid = UUID.randomUUID();
		this.CURRSTATE = /* String.valueOf(EvtState.INITIAL.ordinal()) +"-"+ */ EvtState.INITIAL.toString() ;
		this.cal = new GregorianCalendar();
		this.cal.setTime(new Date());
	}
	
	public static final synchronized EventImpl createNewEvent()
	{
		return new EventImpl();
	}

	@Override
	public String getEventId() {
		return this.uuid.toString(); // good return the immutable string here
	}	
	

	@Override
	public String getEventType() {
		return this.CURRSTATE; // should I give away a copy ? 
	}
	
	public void setEventType(EvtState p_evtstate)
	{
		
		
		
		synchronized(this)
		{
		//this.setevntLck.lock();
		try
		{
			this.CURRSTATE = /*String.valueOf(p_evtstate.ordinal()) +"-"+ */ p_evtstate.toString() ;
			this.cal.setTime(new Date());
		}
		finally
		{
			//this.setevntLck.unlock();
		}
		}
	}

	@Override
	public Calendar getEventTimestamp() {
		return this.cal ;
	}
	
	@Override
	public String toString()
	{
		synchronized(EventImpl.SDF)
		{
		//this.prnevntLck.lock();
		String etimestmp = null;
		String rtnValue = null ;

		try
		{
		
			etimestmp = EventImpl.SDF.format(this.getEventTimestamp().getTime());
			rtnValue = this.getEventId() + "|" + this.getEventType() + "|" + etimestmp ; 
			
		}
		finally
		{
			//this.prnevntLck.unlock();
		}
		
		return rtnValue ;
		}
		
	}
	
	public static void main(String...args)
	{
		for (EvtState evt : EvtState.values())		{
			System.out.print(evt.ordinal() + "-");
			System.out.println(evt.toString());
		}
		
		EventImpl e = EventImpl.createNewEvent();
		System.out.println(e);
		EventImpl.EvtState olde = EventImpl.EvtState.valueOf(e.getEventType());
		int nxtpos = olde.ordinal() + 1 ;
		e.setEventType(EventImpl.EvtState.values()[nxtpos]);
		System.out.println(e);
	}

}
