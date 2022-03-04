# Programming Assignment 2

## How to compile and run:
- On command line do javac MinotaurBirthdayParty.java or javac MinotaurCrystalVase
- Then do java MinotaurBirthdayParty or java MinotaurCrystalVase
- You will be prompted on the command line to select N number of guests

## MinotaurBirthdayParty

For this program, a Minotaur invites N guests to his birthday and has them enter a labyrinth with a cake inside they may or may not take. If there is no cake, they may ask for one to be brought. He wants all guests to enter at least once, but does not want them to discuss their labyrinth experience during or after entering. The guests may come up with a strategy beforehand, but there must be no intercommunication once they enter the labyrinth. 

To solve this problem my code will have a guest at the beginning randomly assigned the role of "Decider". This deciding guest will be the only guest allowed to request a new cake should there be one missing. The other guests will take a cake if they have not previously taken one. The deciding guest will then keep track of this number until the number of times they have asked for a new cake equals the total number of guests.

With each guest represented by a thread, this program will follow the rules of no intercommunication among guests by having the only atomic variable be for the cake being there or not being there. All other variables will be non static and specific to each thread. A non-static visits variable will be available to all guests however only the deciding guest will increment this variable, the other guests won't touch it. Finally, the deciding guest will have tell all other threads to stop once this visits number is equal to the total number of guests.

Parallelized with locks, this algorithm is very efficient with an average runtime of 125 ms for 500 guests. 

## MinotaurCrystalVase

For this program, a Minotaur wants to show his guests his favorite crystal vase, but does not want them crowding the room so restricts access to one guest at a time. 

I am given the option of three possible strategies for the guests. I decided to go with strategy 2 where a sign indicating whether the showroom is available with either an "AVAILABLE" or "BUSY". The guests set the sign to BUSY when using and AVAILABLE when not so other guests don't even bother going to the showroom until it is available. 

My algorithm had once again each guest represented by a thread. Here all threads run concurrently and check if the availableBusy boolean is true(available) or false(busy). If busy they simply continue on their way, however if available they switch the boolean and enjoy the vase for a few milliseconds. The threads(guests) may re-queue in random order and the program will stop once all guests have seen the vase at least once.

Parallelized with each thread running concurrently, the algorithm is fairly efficient with an average runtime of 210 ms for 500 guests. 
