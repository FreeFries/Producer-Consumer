package xander.multiThreading.jdk5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* @author Alexander Pereira, Croydon UK */
public class ProducerConsumerPattern {
	
   private static final ReentrantLock lck = new ReentrantLock(true); 	
	
   public static void thrdSafePrintln(final Object f)  // ProducerConsumerPattern.thrdSafePrintln
   {
	   synchronized(System.out)
	   {
		   try
		   {
			   System.out.println(f);
		   }
		   finally
		   {
		   }
	   }
   }

   public static void main(String args[]){
	long start = System.currentTimeMillis() ;   
	try
	{
 
		boolean useThreads = Boolean.parseBoolean(args[0]); // true for threads, false for Executor Service
		int howManyEvnts = Integer.parseInt(args[1]);		
		int executorsThrdPool = Integer.parseInt(args[2]);
		int howManyConsumers = Integer.parseInt(args[3]);
		int terminateAfter = (Integer.parseInt(args[4]) * 1000) ; // Convert the seconds into it's milliseconds value
		
		ProducerConsumerPattern.thrdSafePrintln(useThreads?"Using Threads":"Using ExecutorService");
		ProducerConsumerPattern.thrdSafePrintln("Producing Events=" + howManyEvnts);
		ProducerConsumerPattern.thrdSafePrintln("ExecutorsThreadPool=" + executorsThrdPool);
		ProducerConsumerPattern.thrdSafePrintln("Consumers = " + howManyConsumers);
		ProducerConsumerPattern.thrdSafePrintln("Terminate After " + (terminateAfter/1000) + " secs");
		
	    //Creating shared object
	    BlockingQueue<EventImpl> sharedQueue = new LinkedBlockingQueue<EventImpl>();
	    ConcurrentSkipListMap<String, EventImpl> sharedMap = new ConcurrentSkipListMap<String, EventImpl>();
	
	    //Creating Producer and Consumer Thread
	    if (useThreads)
	    {
	    	List<Thread> lt = new ArrayList<Thread>();
	   	    Thread prodThread = new Thread(new Producer(sharedQueue,howManyEvnts)); // you see interleaving with a higher queue length
		    for (int zi = 1; zi <=  howManyConsumers; zi++)
		    {
		    	Thread consumthrd = new Thread(new Consumer(sharedQueue,sharedMap));
		    	lt.add(consumthrd);
		    	consumthrd.start();
		    }
		    lt.add(prodThread);
		    prodThread.start();
		    
		    
		    ProducerConsumerPattern.thrdSafePrintln("Launched all threads and about to tell the main thread that in can only proceed after the worker threads have finished doing what they do and JOINed it");
		    
		    //prodThread.join(); // not needed it is added to the array
		    //ProducerConsumerPattern.thrdSafePrintln("Producer thread joined");
		    /*
		    for (Thread ti : lt)
		    {
		    	ti.join(); 
		    	ProducerConsumerPattern.thrdSafePrintln("Waiting for the worker thread " + ti.getName() + " to join main thread");
		    	// it does not seem to go any further !!!
		    }
		    */
		    
		    Thread.currentThread().sleep(terminateAfter);
		    
		    for (Thread ti : lt)
		    {
		    	
		    	if (ti.getState() == Thread.State.WAITING)
		    	{
		    		ProducerConsumerPattern.thrdSafePrintln("Thread " + ti.getName() + " state=" + ti.getState() + " About to be INTERRUPTED");
		    		ti.interrupt(); // does not work
		    	}
		    	
		    	ProducerConsumerPattern.thrdSafePrintln("Thread " + ti.getName() + " is now left in state=" + ti.getState());
		    }
		    System.out.println("Rentrant Lock Q of waiting threads =" + lck.getQueueLength());
		    
		    
	    }
	    else
	    {
		    //Using an Executor Service instead of vanilla threads
		    ExecutorService es = Executors.newFixedThreadPool(executorsThrdPool);
		    for (int zi = 1; zi <=  howManyConsumers; zi++)
		    {
		    	es.submit(new Consumer(sharedQueue,sharedMap));
		    }

		    
		    es.submit(new Producer(sharedQueue,howManyEvnts));
		    
		    
		    es.shutdown(); // telling ExecutorService - no more Runnable Tasks will be submitted
		    if (es.awaitTermination(terminateAfter, TimeUnit.MILLISECONDS))
		    {
		    	
		    	ProducerConsumerPattern.thrdSafePrintln("Terminated naturally");
		    }
		    else
		    {
		    	ProducerConsumerPattern.thrdSafePrintln("Timed Out BUT the whole eventlist will have been processed - look @ output " + "Rentrant Lock Q of waiting threads =" + lck.getQueueLength());
		    	List<Runnable> tWaiting= es.shutdownNow();
		    	
		    	ProducerConsumerPattern.thrdSafePrintln( "List of Runnables awaiting Execution = " + tWaiting.size());
		    	for (Runnable r : tWaiting)
		    	{
		    		ProducerConsumerPattern.thrdSafePrintln(r);
		    	}
		    }
	    }
	    
	    
	    Thread.currentThread().sleep(100);
	    
	    /* Commented out because enhanced for loop is more intuitive */
	    /*
	    Iterator it = sharedMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	    }
	    */
	    
	    for (Entry<String,EventImpl> e : sharedMap.entrySet())
	    {
	    	System.out.println( e.getKey() + " = " + e.getValue() );
	    }
	    
	}
	catch (Exception ee)
	{
		ee.printStackTrace();
	}
	finally
	{
		
		
		ProducerConsumerPattern.thrdSafePrintln(Thread.currentThread().getName()+" Finished in " + (System.currentTimeMillis() - start));
	}
   }

}




