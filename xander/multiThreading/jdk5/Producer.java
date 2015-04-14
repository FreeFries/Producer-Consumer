package xander.multiThreading.jdk5;

//Producer Class in java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/* @author Alexander Pereira, Croydon UK */
class Producer implements Runnable {

 private final BlockingQueue<EventImpl> sharedQueue;
 static AtomicInteger queueLength = new AtomicInteger(0) ;
 

 public Producer(BlockingQueue<EventImpl> sharedQueue, int p_queueLength) {
     this.sharedQueue = sharedQueue;
     
     if (Producer.queueLength.get()==0)
     {
    		 Producer.queueLength.getAndSet(p_queueLength);
    		 ProducerConsumerPattern.thrdSafePrintln("Queue Length set to " + Producer.queueLength.get());
     }
 }
 

 @Override
 public void run() {
     for(int i=0; i < Producer.queueLength.get() ; i++){
         try {
        	 EventImpl tmpE = EventImpl.createNewEvent();
             ProducerConsumerPattern.thrdSafePrintln(Thread.currentThread().getName() +" Produced: " + i + "=>" + tmpE );
             
             sharedQueue.put(tmpE);
         } catch (InterruptedException ex) {
             //Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
        	 ex.printStackTrace();
         }
     }
     
     ProducerConsumerPattern.thrdSafePrintln("Producer Loop is over now ... so it should be stopping");
 }

}
