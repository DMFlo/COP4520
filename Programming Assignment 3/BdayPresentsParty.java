// David Florez
// COP 4520

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.io.*;

public class BdayPresentsParty extends Thread
{
    public static ArrayList<Servant> threads = new ArrayList<>();
    public AtomicInteger cards= new AtomicInteger();
    // Lock free linked list implemented form the book
    public LockFreeLL chain = new LockFreeLL();
    public Stack<Integer> gifts = new Stack<>();
    public ArrayList<Integer> chainGifts = new ArrayList<>();
    
    ReentrantLock lock = new ReentrantLock();
    int presents;
    int numOfThreads;

    BdayPresentsParty()
    {
        this.presents = 500_000;
        this.numOfThreads = 4;
    }

    void runServants(BdayPresentsParty mainThread) throws InterruptedException
    {
        // Threads are created, added to arraylist, and run
        for (int i = 1; i <= this.numOfThreads; i++)
            threads.add(new Servant(i, mainThread));

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).start();

        for (int i = 0; i < mainThread.numOfThreads; i++)
            threads.get(i).join();

        System.out.println(cards + " Thank you notes were written!");
    }

    public static void main(String args[]) throws InterruptedException
    {
        // Instance of mainthread created
        BdayPresentsParty mainThread = new BdayPresentsParty();

        for (int i = 1; i <= mainThread.presents; i++)
            mainThread.gifts.push(i);

        // Shuffled stack simulates unordered bag
        Collections.shuffle(mainThread.gifts);

        final long startTime = System.currentTimeMillis();

        mainThread.runServants(mainThread);

        final long endTime = System.currentTimeMillis();
        final long runTime = endTime - startTime;
        System.out.println("Execution time: " + runTime + " ms");
    }
}

class Servant extends Thread
{
    int threadNum;
    BdayPresentsParty mainThread;

    Servant(int threadNum, BdayPresentsParty mainThread)
    {
        this.threadNum = threadNum;
        this.mainThread = mainThread;
    }

    boolean bagIsEmpty()
    {
        return this.mainThread.gifts.empty();
    }

    boolean chainIsEmpty()
    {
        return this.mainThread.chainGifts.size() == 0 ? true : false;
    }

    @Override
    public void run()
    {
        while (mainThread.cards.get() < mainThread.presents)
        {
            int task = (int) (Math.random() * 3 + 1);

            int gift;

            switch (task)
            {
                case 1:
                    // remove from bag and into list
                    // lock
                    // remove from arraylist
                    mainThread.lock.lock();
                    try {
                        if (bagIsEmpty())
                            break;
                        gift = mainThread.gifts.pop();
                        mainThread.chainGifts.add(gift);
                    } finally {
                        mainThread.lock.unlock();
                    }

                    mainThread.chain.add(gift);
                    break;
                case 2:
                    // Write thank you and remove from list
                    mainThread.lock.lock();
                    try {
                        if (chainIsEmpty())
                            break;
                        int randIndex = (int)(Math.random() * mainThread.chainGifts.size());
                        gift = mainThread.chainGifts.get(randIndex);
                        mainThread.chainGifts.remove(randIndex);
                    } finally {
                        mainThread.lock.unlock();
                    }
                    mainThread.chain.remove(gift);
                    mainThread.cards.getAndIncrement();
                    break;
                case 3:
                    // check is gift is on chain
                    int randGift = (int)(Math.random() * mainThread.presents + 1);

                    mainThread.chain.contains(randGift);
            }
        }
    }
}