package xander.multiThreading.jdk5;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

//Consumer Class in Java
/* @author Alexander Pereira, Croydon UK */
class Consumer implements Runnable{
	
 //private Lock lck = new Lock();

 private final BlockingQueue<EventImpl> sharedQueue;
 private final ConcurrentSkipListMap<String, EventImpl> m ;
 
 private static AtomicInteger cntEvents = new AtomicInteger(0) ; 
 

 public Consumer(BlockingQueue<EventImpl> sharedQueue, ConcurrentSkipListMap<String, EventImpl> mSharedMap) {
     this.sharedQueue = sharedQueue;
     this.m = mSharedMap ;
 }
 
private void putTakenEvntInSortdList(EventImpl tmpE)
{
 EventImpl chkValue = this.m.putIfAbsent(tmpE.toString(), tmpE);
 if (chkValue != null)
 {
	 ProducerConsumerPattern.thrdSafePrintln(Thread.currentThread().getName() +"There was a previous value");
	 System.exit(1);
 }
}

 @Override
 public void run() {
     while( true  ){
         try 
         {
        	 int tstVal = cntEvents.get() ;
        	 int tstQLen = Producer.queueLength.get();
    		 if ( tstVal  >= tstQLen && tstVal > 0 && sharedQueue.isEmpty() ) // If no consumer is started you can have the situation where because 0 >= 0 is TRUE (zero gets = zero Queue Length) - So consumer threads start exiting
    		 {									     // This can happen when the consumers have started but the Producer has not yet manufactured the first event to hit the queue
    			 ProducerConsumerPattern.thrdSafePrintln(Thread.currentThread().getName() +" about to Break Out cntEvents.get()=" + tstVal + " testQLen=" + tstQLen /*+ "m.size()= " + m.size()*/);
    			 
    			 break; // kill this consumer but only for the thread that does the 1000th CLOSED
    		 }
    		 else
    		 {
    			 ProducerConsumerPattern.thrdSafePrintln(Thread.currentThread().getName() +" still Running =" + tstVal);
    		 }
        	 
        	 EventImpl tmpE = (EventImpl)sharedQueue.take(); // from the head - shd be thread-safe
        	 												 // MAJOR PROBLEM: SOMETIMES A THREAD CAN BE WAITING HERE BUT THE QUEUE HAS BEEN EMPTIED 
        	                                                 //                UNFORTUNATELY THE THREAD HAS ALREADY DONE IT'S CHECKING

        	 this.putTakenEvntInSortdList(tmpE);
        	 
        	 EventImpl.EvtState olde = EventImpl.EvtState.valueOf(tmpE.getEventType());
        	 if (olde.ordinal() < EventImpl.EvtStateLASTPOS)
        	 {
        		 	 int nxtpos = olde.ordinal() + 1 ;
	        		 tmpE.setEventType(EventImpl.EvtState.values()[nxtpos]); // synchronized on the taken one + volatile
	        		 if (nxtpos == EventImpl.EvtStateLASTPOS)
	        		 {
	        			 this.putTakenEvntInSortdList(tmpE);

	        			 // let it be taken (aka removed from the head of the queue - CLOSED state achieved
	        			 cntEvents.getAndIncrement();
	        			 ProducerConsumerPattern.thrdSafePrintln("ClosedCounter=" + cntEvents.get());
	        			 
	        			 
	        		 }
	        		 else
	        		 {
	        			 sharedQueue.put(tmpE); // put it back at the tail-end - shd be thread-safe
	        		 }
            		 ProducerConsumerPattern.thrdSafePrintln(Thread.currentThread().getName()+" Consumed: "+ tmpE);
            		 

        		 
        	 }
        	 else if ((olde.ordinal() >= EventImpl.EvtStateLASTPOS))
        	 {
        			 System.err.print("IT SHOULD NEVER REACH HERE - DEBUG STATEMENT!!!!! " + olde);
        			 System.exit(1);
        	 }
     		 
     		 
         } catch (InterruptedException ex) {
             //Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
        	 ex.printStackTrace();
         }
     } // eternal loop
     
     ProducerConsumerPattern.thrdSafePrintln(Thread.currentThread().getName()+ " WILL STOP NOW !!!");
     
 } // end of Consumer Run


}
