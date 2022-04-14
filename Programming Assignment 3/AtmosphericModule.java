// David Florez
// COP 4520

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

public class AtmosphericModule extends Thread
{
    public static ArrayList<Sensor> threads = new ArrayList<>();
    public ArrayList<Integer> recordings = new ArrayList<>();
    // Min and maxheap for min and top 5
    public PriorityQueue<Integer> minFive = new PriorityQueue<>();
    public PriorityQueue<Integer> maxFive = new PriorityQueue<>(Collections.reverseOrder());

    ReentrantLock lock = new ReentrantLock();

    int numOfThreads;
    // hoursToIter converts hours to iteration based on the number of threads and hours in a minute
    int hoursToIter, hours;

    AtmosphericModule(int hoursToIter)
    {
        this.numOfThreads = 8;
        this.hoursToIter = hoursToIter * this.numOfThreads * 60;
        this.hours = hoursToIter;
    }

    Sensor getThread(int index)
    {
        return threads.get(index - 1);
    }

    // Report method prints the report after every hour
    void printReport(int hour)
    {
        System.out.println("\n======== Sensory Report After " + hour + " hours ========\n");
        System.out.println("Top 5 temperatures: " + printMaxFive());
        System.out.println("Min 5 temperatures: " + printMinFive());
        int [] ranges = findMaxRange();
        System.out.println("Largest 10 minute range: " + ranges[2] + " in " + ranges[3] * 10 + " to " + (ranges[3] * 10 + 10));
        this.recordings.clear();
    }

    ArrayList<Integer> printMaxFive()
    {
        ArrayList<Integer> topFive = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            int num = maxFive.poll();
            if (topFive.contains(num))
            {
                i--;
                continue;
            }

            topFive.add(num);
        }

        maxFive.clear();
        return topFive;
    }

    ArrayList<Integer> printMinFive()
    {
        ArrayList<Integer> topFive = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            int num = minFive.poll();
            if (topFive.contains(num))
            {
                i--;
                continue;
            }

            topFive.add(num);
        }

        minFive.clear();
        return topFive;
    }

    int [] findMaxRange()
    {
        int len = recordings.size();
        int [] ranges = new int[4];
        for (int i = 0, j = 1; i < len; i++)
        {
            if (recordings.get(i) >= ranges[0])
                ranges[0] = recordings.get(i);
            
            if (recordings.get(i) <= ranges[1])
                ranges[1] = recordings.get(i);

            if (i == j * 80)
            {
                if ((ranges[0] - ranges[1]) > ranges[2])
                {
                    ranges[2] = ranges[0] - ranges[1];
                    ranges[3] = j++;
                }
            }
        }

        return ranges;
    }

    void runSensors(AtmosphericModule mainThread) throws InterruptedException
    {
        // Threads are created, added to arraylist, and run
        for (int i = 1; i <= this.numOfThreads; i++)
        {
            if (i == 1)
                threads.add(new Sensor(i, mainThread, true));
            else
            threads.add(new Sensor(i, mainThread, false));
        }

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).start();

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).join();
    }

    public static void main(String[] args) throws InterruptedException
    {
        //Scanner object
        Scanner input = new Scanner(System.in);

        System.out.print("Enter how many hours you wish to simulate [MAX 19 hours, more takes longer than 30s]: ");

        while(!input.hasNextInt())
        {
            input.nextLine();
            System.out.print("Please input an integer value");
        }

        int hours = input.nextInt();

        input.close();

        if (hours <= 0 || hours > 19)
        {
            System.out.println("Must be more than 0 hours");
            return;
        }
        // Instance of mainthread created
        AtmosphericModule mainThread = new AtmosphericModule(hours);

        final long startTime = System.currentTimeMillis();

        mainThread.runSensors(mainThread);

        final long endTime = System.currentTimeMillis();
        final long runTime = endTime - startTime;
        System.out.println("Execution time: " + runTime + " ms");
    }
}

class Sensor extends Thread
{
    // Atomic integer keeps track of total iterations based on number of threads
    static AtomicInteger iterations = new AtomicInteger();
    static AtomicBoolean hourPassed = new AtomicBoolean();
    // hour variable keeps track of the current hour
    int threadNum, hour = 1;
    boolean checked;
    // Only reporter thread can submit the report
    boolean isReporter = false;
    AtmosphericModule mainThread;

    // Constructor maintains access to mainthread
    Sensor(int threadNum, AtmosphericModule mainThread, boolean isReporter)
    {
        this.threadNum = threadNum;
        this.mainThread = mainThread;
        this.isReporter = isReporter;
        this.checked = false;
    }

    // Generates random temperature range
    int genRandTemp()
    {
        return (int) (Math.floor(Math.random() * (70 - (-100) + 1) + (-100)));
    }

    // checks what threads have already taken temperature
    boolean checkThreads()
    {
        for (int i = 0; i < 8; i++)
            if (!this.mainThread.getThread(this.threadNum).checked)
                return false;

        return true;
    }

    @Override
    public void run()
    {
        // Goes for the maximum number of iteration hours
        while (iterations.get() < mainThread.hoursToIter)
        {
            while (!checkThreads())
            {
                if (!this.checked) 
                {
                    int rand = genRandTemp();
                    this.mainThread.lock.lock();
                    try 
                    {
                        this.mainThread.recordings.add(rand);
                        this.mainThread.maxFive.add(rand);
                        this.mainThread.minFive.add(rand);
                        this.checked = true;
                    }
                    finally 
                    {
                        this.mainThread.lock.unlock();
                    }
                }
            }

            this.checked = false;
            iterations.getAndIncrement();

            // if statement gets the current hour based on iterations
            if (iterations.get()/this.hour == 480)
                hourPassed.set(true);

            // sleep function allows threads to synchronize after exiting while loop
            try 
            {
                Thread.sleep(10);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            if (this.isReporter && hourPassed.get())
            {
                this.mainThread.lock.lock();
                try
                {
                    this.mainThread.printReport(this.hour++);
                    hourPassed.set(false);
                }
                finally
                {

                    this.mainThread.lock.unlock();
                }   
            }

            try 
            {
                Thread.sleep(10);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}