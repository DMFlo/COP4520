// David Florez
// COP4520
// Problem 1

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaurBirthdayParty extends Thread
{
    public static ArrayList<MinotaurLabyrinthGuest> threads = new ArrayList<>();
    // Lock from main thread
    ReentrantLock lock = new ReentrantLock();
    int numOfThreads;

    MinotaurBirthdayParty (int numOfThreads)
    {
        this.numOfThreads = numOfThreads;
    }

    int getNumOfThreads()
    {
        return this.numOfThreads;
    }

    // Returns thread at specified index
    MinotaurLabyrinthGuest getThread(int index)
    {
        return threads.get(index);
    }

    void MinotaurPartyRun(MinotaurBirthdayParty mainThread) throws InterruptedException
    {
        // Random guest is assigned role of decider
        // to keep track if each guest has entered the labyrinth at least once
        // with no intercommunication between the threads
        int decider = (int)(Math.random() * mainThread.getNumOfThreads() + 1);
        System.out.println("Guest " + decider + " is the deciding guest!");

        // Threads are created, added to arraylist, and run
        for (int i = 1; i <= mainThread.numOfThreads; i++)
        {
            if (i == decider)
                threads.add(new MinotaurLabyrinthGuest(i, mainThread, true));
            else
                threads.add(new MinotaurLabyrinthGuest(i, mainThread, false));
        }

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).start();

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).join();
    }

    public static void main(String args[]) throws InterruptedException
    {   
        //Scanner object
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Number of Guests: ");

        while(!input.hasNextInt())
        {
            input.nextLine();
            System.out.print("Please input an integer value");
        }

        int numOfGuests = input.nextInt();

        input.close();

        if (numOfGuests <= 0)
        {
            System.out.println("0 Guests came to the party :(");
            return;
        }

        final long startTime = System.currentTimeMillis();

        // Instance of mainthread created
        MinotaurBirthdayParty mainThread = new MinotaurBirthdayParty(numOfGuests);

        mainThread.MinotaurPartyRun(mainThread);

        final long endTime = System.currentTimeMillis();

        final long runTime = endTime - startTime;
        System.out.println("Execution time: " + runTime + " ms");
    }
}

class MinotaurLabyrinthGuest extends Thread
{
    static AtomicBoolean cake = new AtomicBoolean(true);
    // atomic boolean used to stop all threads. Only used at the end by
    // the deciding guest ensuring no intercommunication
    static AtomicBoolean stopThreads = new AtomicBoolean(false);
    // non-static visits variable that only the deciding guest will be manipulating
    int visits;
    // bools for deciding guest role as well for no guest to take more than one cake
    boolean isDecider = false;
    boolean hasTakenCake = false;
    int threadNum;
    MinotaurBirthdayParty mainThread;

    MinotaurLabyrinthGuest (int threadNum, MinotaurBirthdayParty mainThread, boolean isDecider)
    {
        this.threadNum = threadNum;
        this.mainThread = mainThread;
        this.isDecider = isDecider;
    }

    boolean isGuestDecider()
    {
        return this.isDecider;
    }

    void doNothing()
    {
        return;
    }

    @Override
    public void run()
    {   
        // Deciding guest is one visitor guaranteed
        if (isGuestDecider())
                this.visits = 1;
        
        while (true) 
        {   
            // lock to keep only one guest in the labyrinth at a time
            mainThread.lock.lock();

            // If the deciding guest triggered this flag, all threads stop
            if (stopThreads.get())
            {
                mainThread.lock.unlock();
                break;
            }
            
            // Once the visits variable, that is only incrementing for the deciding guest,
            // is equal to the number of guests, the decider announces success
            if (this.visits == mainThread.getNumOfThreads())
            {
                System.out.println("Labyrinth Visits: " + this.visits);
                System.out.println("All guests have entered the Minotaur's Labyrinth! Guest " + this.threadNum + " announced!");
                stopThreads.set(true);
                mainThread.lock.unlock();
                break;
            }
            try
            {
                // If the guest has already taken a cake, it does nothing
                if (this.hasTakenCake)
                    doNothing();
                else if (cake.get() && !isGuestDecider())
                {
                    // A guest takes a cake so long as they are not the decider
                    this.hasTakenCake = true;
                    cake.set(false);
                }
                else
                {   
                    // This area is only for deciding guest
                    // Decider asks for a new cake if there is none
                    if (isGuestDecider())
                    {
                        this.visits++;
                        cake.set(true);
                    }
                }
            } finally {
                mainThread.lock.unlock();
            }
        }
    }
}