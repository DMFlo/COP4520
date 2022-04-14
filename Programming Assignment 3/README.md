# Programming Assignment 3

## How to compile and run:
- On command line do javac BdayPresentsParty.java or javac AtmosphericModule.java
- Then do java BdayPresentsParty or java AtmosphericModule
- You will be prompted on the command line to select N number of hours for problem 2

## BdayPresentsParty

For this program, the Minotaur wants his servants to organize 500,000 gifts. To do this he was them, in no order, take the gifts from an unordered bag, add them to a chain in order, and then remove them giving out thank you notes.

To implement this process we use the textbooks Lock Free Linked list for the best performance. Simulating the unordered bag is a shuffled stack. The stack allows the program to emulate a gift being taken at random from the bag. Like this the servants remove a gift at random and add to the list. The order in which they carry out the tasks is also random as the minotaur did not specify an order, he simply wanted it done fast.The guests gifts are represented themselves by integers between 1 and 500,000. 

The program at the end prints out how many thank you cards were written proving that it is equal to the number of guests. The program, being parallelized to 4 threads, performs very fast for the large number of gifts. It has an average runtime of 2500 ms. 

## AtmosphericModule

For this program, a next generation mars rover needs to take temperature readings every minute across 8 different sensors. The sensors record a temperature and then every hour publish a report. The report include the top five and min temperature readings of that hour. Representing each sensor is a thread for a total of 8. 

The user may select how many hours they wish to run the rover for and see the reports in real time. The runtime as a result varies however it averages around 6000 ms for 5 hours of running. 