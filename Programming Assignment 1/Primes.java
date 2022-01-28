// David Florez
// COP4520

import java.util.*;
import java.io.*;

public class Primes extends Thread
{
    // Constants for Range and number of threads
    public final static int max = 100000000;
    public final static int numOfThreads = 8;

    int threadNum;
    public static boolean[] sieve = new boolean[max + 1];
    public static ArrayList<Primes> threads = new ArrayList<>();

    // Each thread has a thread number of 0-7
    Primes(int threadNum)
    {
       this.threadNum = threadNum;
    }

    public void run()
    {
        // Sieve of Eratosthenes algorithm for finding primes in range 0 to N
        // Each thread handles an equal range in increments of all 8 threads
        for (int i = 2 + this.threadNum; i * i <= max; i += numOfThreads)
            if (sieve[i] != true)
                for (int j = i * i; j <= max; j += i)
                    sieve[j] = true;
    }

    public static void printOutputFile(long executionTime, int primeCount, long sumOfPrimes, int top[]) throws IOException
    {
        String outputString = "Execution Time: " + executionTime + " ms\n"
                             + "Total Number of Primes found: " + primeCount + "\n"
                             + "Sum of all Primes Found: " + sumOfPrimes + "\n"
                             + "Ten Largest Primes Found (low to high): " + Arrays.toString(top) + "\n";
        
        System.out.print(outputString);
        try 
        {
            File txtFile = new File("primes.txt");
            if (txtFile.createNewFile())
              System.out.println("File created: " + txtFile.getName());
            else
              System.out.println("File already exists.");
        }
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
        try 
        {
            FileWriter myWriter = new FileWriter("primes.txt");
            myWriter.write(outputString);
            myWriter.close();
        } 
        catch (IOException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void main(String args[]) throws InterruptedException, IOException
    {
        // Timer begins
        final long startTime = System.currentTimeMillis();

        // Create and add threads to arrayList of threads
        for (int i = 0; i < numOfThreads; i++)
            threads.add(new Primes(i));

        // Threads are started
        for (int i = 0; i < numOfThreads; i++)
            threads.get(i).start();

        // All threads wait to end
        for (int i = 0; i < numOfThreads; i++)
            threads.get(i).join();
 
        // Clock ends
        final long endTime = System.currentTimeMillis();

        // Compute number of Primes
        int count = 0;
        long primeSum = 0;
        for (int i = 2; i <= max; i++)
            if (sieve[i] != true)
            {
                primeSum += i;
                count++;
            }
        
        // Get top 10 largest primes
        int topTen[] = new int[10];
        for (int i = max, j = 9; i > 0 && j >= 0; i--)
            if (!sieve[i])
                topTen[j--] = i;

        // Helper function prints to console and to a file primes.txt
        printOutputFile(endTime - startTime, count, primeSum, topTen);
    }
}