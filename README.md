# croydon_code - Producer Consumer Pattern
Producer/Consumer, Events Consumed by various threads and then collated in order of occurrence. Event uses <b>UUID</b>. Events are INITIAL, CREATING, CREATED, CLOSING &amp; CLOSED. Makes use of ENUM. Makes use of <b>Blocking Queue</b> &amp; ConcurrentSkipList collections for thread safe operations. Follows JDK5 Concurrent API. New multi-threaded techniques are being used.

<br />In order to run this example use the following arguments to get started
<br />Use the ProducerConsumerPattern class main method to start this example.
<br />Argument 0 - if true then uses vanilla Consumer Threads to do it's job, if false uses Executors instead
<br />Argument 1 - Say how many events you would like the Producer to generate, 1000 for example
<br />Argument 2 - Relevant only for Executors, tells the ExecutorService how big the thread pool is going to be, 10 for example.
<br />Argument 3 - Says how many Consumer(s) you would like to rollover the events produced to the next event state, 5 for example.
<br />Argument 4 - Tell the main thread to wait for say 3 seconds before attempting to terminate all the Consumer Executors or Threads
