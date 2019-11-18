package alternator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;

public class Main {

    /**
     * Solution is based on inheriting the critical section.
     * In the beginning only Thread A can fire, then it gives the
     * Critical Section to B, which gives it to C, afterwards.
     * On ending protocol, the critical section is transferred between two threads.
     */

    private static class FireThread extends Thread {
        private PetriNet<Place> net;
        private Collection<Transition<Place>> transitions;

        FireThread(PetriNet<Place> net, Collection<Transition<Place>> transitions) {
            this.net = net;
            this.transitions = transitions;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    this.net.fire(this.transitions);

                    if (this.net.getInitialMarking().get(Place.CS) == 1) {
                        System.out.print(Thread.currentThread().getName() + ".");
                    }

                    this.net.fire(this.transitions);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }

    private enum Place {
        A, B, C, CS, ACS, BCS, CCS
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
        putPositive(result, Place.ACS, 0);
        putPositive(result, Place.BCS, 0);
        putPositive(result, Place.CCS, 0);

        return result;
    }

    public static void main(String[] args) {
        Map<Place, Integer> initialMarking = marking();
        PetriNet<Place> alternatorNet = new PetriNet<>(initialMarking, true);

        //--------------------------------------------------------------------------

        Map<Place, Integer> trAinInput = Map.of(Place.A, 1);
        Set<Place> trAinInhibitor = Set.of(Place.CS);
        Map<Place, Integer> trAinOutput = Map.of(Place.CS, 1, Place.ACS, 1);
        Transition<Place> trAin = new Transition<>(trAinInput, null, trAinInhibitor, trAinOutput);

        Map<Place, Integer> trAoutInput = Map.of(Place.ACS, 1, Place.CS, 1);
        Set<Place> trAoutReset = Set.of(Place.B, Place.C);
        Map<Place, Integer> trAoutOutput = Map.of(Place.B, 1, Place.C, 1);
        Transition<Place> trAout = new Transition<>(trAoutInput, trAoutReset, null, trAoutOutput);

        //--------------------------------------------------------------------------

        Map<Place, Integer> trBinInput = Map.of(Place.B, 1);
        Set<Place> trBinInhibitor = Set.of(Place.CS);
        Map<Place, Integer> trBinOutput = Map.of(Place.CS, 1, Place.BCS, 1);
        Transition<Place> trBin = new Transition<>(trBinInput, null, trBinInhibitor, trBinOutput);

        Map<Place, Integer> trBoutInput = Map.of(Place.BCS, 1, Place.CS, 1);
        Set<Place> trBoutReset = Set.of(Place.A, Place.C);
        Map<Place, Integer> trBoutOutput = Map.of(Place.A, 1, Place.C, 1);
        Transition<Place> trBout = new Transition<>(trBoutInput, trBoutReset, null, trBoutOutput);

        //--------------------------------------------------------------------------

        Map<Place, Integer> trCinInput = Map.of(Place.C, 1);
        Set<Place> trCinInhibitor = Set.of(Place.CS);
        Map<Place, Integer> trCinOutput = Map.of(Place.CS, 1, Place.CCS, 1);
        Transition<Place> trCin = new Transition<>(trCinInput, null, trCinInhibitor, trCinOutput);

        Map<Place, Integer> trCoutInput = Map.of(Place.CCS, 1, Place.CS, 1);
        Set<Place> trCoutReset = Set.of(Place.A, Place.B);
        Map<Place, Integer> trCoutOutput = Map.of(Place.A, 1, Place.B, 1);
        Transition<Place> trCout = new Transition<>(trCoutInput, trCoutReset, null, trCoutOutput);

        //--------------------------------------------------------------------------

        // Collection of transitions for each thread.
        Collection<Transition<Place>> threadATransitions = new HashSet<>(Arrays.asList(trAin, trAout));
        Collection<Transition<Place>> threadBTransitions = new HashSet<>(Arrays.asList(trBin, trBout));
        Collection<Transition<Place>> threadCTransitions = new HashSet<>(Arrays.asList(trCin, trCout));

        Collection<Transition<Place>> allTransitions = new HashSet<>();
        allTransitions.addAll(threadATransitions);
        allTransitions.addAll(threadBTransitions);
        allTransitions.addAll(threadCTransitions);

        Set<Map<Place, Integer>> toCheck = alternatorNet.reachable(allTransitions);

        // Writes count of  reachable markings from initial state.
        System.out.println(toCheck.size());

        /**
         * Checks for mutual exclusion. If not true returns with exit code 7.
         */
        for (Map<Place, Integer> m : toCheck) {
            if (m.containsValue(2)) {
                // Only Critical Section has more than one input arc, so if there is value 2 in map then solution is wrong.
                // Exit status code is chosen randomly.
                System.exit(7);
            }
        }

        Thread threadA = new FireThread(alternatorNet, threadATransitions);
        threadA.setName("A");
        Thread threadB = new FireThread(alternatorNet, threadBTransitions);
        threadB.setName("B");
        Thread threadC = new FireThread(alternatorNet, threadCTransitions);
        threadC.setName("C");

        threadA.start();
        threadB.start();
        threadC.start();

        try {
            // Main Thread sleeps 30 seconds.
            Thread.sleep(30000);
            threadA.interrupt();
            threadB.interrupt();
            threadC.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
