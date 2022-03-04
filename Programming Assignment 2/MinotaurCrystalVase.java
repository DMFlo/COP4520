// David Florez
// COP4520
// Problem 2

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class MinotaurCrystalVase extends Thread
{
    public static ArrayList<MinotaurVaseViewer> threads = new ArrayList<>();
    // Lock from main thread
    ReentrantLock lock = new ReentrantLock();
    int numOfThreads;

    MinotaurCrystalVase (int numOfThreads)
    {
        this.numOfThreads = numOfThreads;
    }

    int getNumOfThreads()
    {
        return this.numOfThreads;
    }

    // Returns thread at specified index
    MinotaurVaseViewer getThread(int index)
    {
        return threads.get(index);
    }

    void MinotaurVaseRun(MinotaurCrystalVase mainThread) throws InterruptedException
    {
        // Threads are created, added to arraylist, and run
        for (int i = 1; i <= mainThread.numOfThreads; i++)
            threads.add(new MinotaurVaseViewer(i, mainThread));

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).start();

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).join();

        // If all threads have joined, each guest has seen the vase at least once
        System.out.println("All " + mainThread.getNumOfThreads() + " guests have seen the vase at least once!");
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

        final long startTime = System.currentTimeMillis();

        // Instance of mainthread created
        MinotaurCrystalVase mainThread = new MinotaurCrystalVase(numOfGuests);

        mainThread.MinotaurVaseRun(mainThread);

        final long endTime = System.currentTimeMillis();

        final long runTime = endTime - startTime;
        System.out.println("Execution time: " + runTime + " ms");
    }
}

class MinotaurVaseViewer extends Thread
{
    // Atomic boolean for all threads to know if the room is available(true) or busy(false)
    static AtomicBoolean availableBusy = new AtomicBoolean(true);
    static AtomicBoolean stopThreads = new AtomicBoolean(false);
    // non-static boolean for each class to know if it has or hasn't seen the vase
    boolean hasSeen = false;
    int threadNum;
    MinotaurCrystalVase mainThread;

    MinotaurVaseViewer(int threadNum, MinotaurCrystalVase mainThread)
    {
        this.threadNum = threadNum;
        this.mainThread = mainThread;
    }

    boolean hasGuestSeen()
    {
        return hasSeen;
    }

    @Override
    public void run() 
    {
        // Infinite while loop lets the threads requeue indefinitely 
        // until all have sene the vase at least once
        while (true)
        {
            // if stop boolean was triggered all threads leave from the loop
            if (stopThreads.get())
                break;
            // stoploop flag is set to true at the beginning of each loop
            boolean stopLoop = true;

            // if room is available, guest enters. Otherwise the don't
            if (availableBusy.get())
            {
                availableBusy.set(false);
                
                this.hasSeen = true;

                // Sleep to simulate each guest enjoying the vase for a short amount of time
                try 
                {
                    Thread.sleep(10);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                availableBusy.set(true);
            }
            else
                continue;

            // For loop checks to make sure each guest has seen the vase at least once
            for (int i = 0; i < mainThread.getNumOfThreads(); i++)
                if (!mainThread.getThread(i).hasGuestSeen())
                {
                    stopLoop = false;
                    break;
                }

            // Stoploop flag tells all other threads to stop
            if (stopLoop)
            {
                stopThreads.set(true);
                break;
            }
        }
    }
}