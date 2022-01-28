#Programming Assignment 1

==================================================
How to compile and run:
- On command line do javac Primes.java
- Then do Java Primes
A file will then be created in current directory
==================================================

This program finds the number of primes between 0 and 10^8, finds the sum of all those primes, and then also returns an array with the ten largest primes found in order of lowest to greatest. 

The program utilizes the Sieve of Eratosthenes algorithm with the work evenly spread across 8 threads. It does this by designating a thread number to each thread and having each thread to a portion of the sieve in increments of 8 from that specific thread number. Through this method, each thread is handling an equal amount of work in the increasing squares of the algorithm. 

Prior to threading, for the given range, the algorithm completes in an average time of 900 ms. By making the algorithm run in parallel with 8 threads, the execution time has been reduced to an average of 500 ms for about a 45% improvement to runtime. 
