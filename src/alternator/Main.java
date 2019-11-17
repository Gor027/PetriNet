package alternator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Main {

    private static class FireThread extends Thread {
        private PetriNet<Place> net;
        private Collection<Transition<Place>> transitions;

        FireThread(PetriNet<Place> net, Collection<Transition<Place>> transitions) {
            this.net = net;
            this.transitions = transitions;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    this.net.fire(this.transitions);
                    if (this.net.getInitialMarking().get(Place.CS) == 1) {
                        System.out.print(Thread.currentThread().getName() + ".");
                    }
                    this.net.fire(this.transitions);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * A, B, C are the threads.
     * S is semaphore.
     * CS is the critical Section.
     * <p>
     * LBC, LAC and LAB used to ensure that n
     * one of the threads will enter twice in a row.
     * Link to Visualization:
     */
    private enum Place {
        A, B, C, CS
    }

    private static <T> void putPositive(Map<T, Integer> map, T key, Integer value) {
        if (value > 0) {
            map.put(key, value);
        }
    }

    // Marking for Alternator
    private static Map<Place, Integer> marking() {
        Map<Place, Integer> result = new HashMap<>();

        putPositive(result, Place.A, 1);
        putPositive(result, Place.B, 1);
        putPositive(result, Place.C, 1);
        putPositive(result, Place.CS, 0);


        return result;
    }

    public static void main(String[] args) {
        Map<Place, Integer> initialMarking = marking();
        PetriNet<Place> alternatorNet = new PetriNet<>(initialMarking, true);

        // ..........................................

        Collection<Transition<Place>> allTransitions = new HashSet<>(Arrays.asList(...));
        Set<Map<Place, Integer>> toCheck = alternatorNet.reachable(allTransitions);

        // Writes count of  reachable markings from initial state.
        System.out.println(toCheck.size());

        System.out.println(toCheck);

        for (Map<Place, Integer> m : toCheck) {
            if (m.containsValue(2)) {
                // Only Critical Section has more than one input arc, so if there is value 2 in map then solution is wrong.
                // Exit status code is chosen randomly.
                System.exit(7);
            }
        }

//        Thread threadA = new FireThread(alternatorNet, threadAtransitions);
//        threadA.setName("A");
//        Thread threadB = new FireThread(alternatorNet, threadBtransitions);
//        threadB.setName("B");
//        Thread threadC = new FireThread(alternatorNet, threadCtransitions);
//        threadC.setName("C");
//
//        threadA.start();
//        threadB.start();
//        threadC.start();
//
//        try {
//            // Main Thread sleeps 30 seconds.
//            Thread.sleep(30000);
//            threadA.interrupt();
//            threadB.interrupt();
//            threadC.interrupt();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
