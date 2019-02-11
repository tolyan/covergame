# Commentary to Solution
* Since we can not store all the data into memory, we have to process data sequentially. Thus, lazy reading with Files
.lines fits our needs.
* Disk I/O operations is the main bottleneck and given amount of files can be up to 100 000, so we can set processing 
of one file as unit of parallelisation. Also, this approach allows to abstain from expensive synchronized structures 
and inter-thread synchronization.
* CompletableFuture uses ForkJoinPool.commonPool() by default, so we don't create excessive amount of threads.