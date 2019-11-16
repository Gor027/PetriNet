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
        A, B, C, CS, S, ACS, BCS, CCS, LBC, LAC, LAB, LBCcheckB, LBCcheckC,
        LACcheckA, LACcheckC, LABcheckA, LABcheckB
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
        putPositive(result, Place.S, 1);
        putPositive(result, Place.ACS, 0);
        putPositive(result, Place.BCS, 0);
        putPositive(result, Place.CCS, 0);
        putPositive(result, Place.LBC, 0);
        putPositive(result, Place.LAC, 0);
        putPositive(result, Place.LAB, 0);
        putPositive(result, Place.LBCcheckB, 0);
        putPositive(result, Place.LBCcheckC, 0);
        putPositive(result, Place.LACcheckA, 0);
        putPositive(result, Place.LACcheckC, 0);
        putPositive(result, Place.LABcheckA, 0);
        putPositive(result, Place.LABcheckB, 0);

        return result;
    }

    public static void main(String[] args) {
        Map<Place, Integer> initialMarking = marking();
        PetriNet<Place> alternatorNet = new PetriNet<>(initialMarking, true);

        /***************************Thread A*********************************/
        // Transition trA connections.
        Map<Place, Integer> trAinput = new HashMap<>();
        trAinput.put(Place.A, 1);
        trAinput.put(Place.S, 1);
        Map<Place, Integer> trAoutput = new HashMap<>();
        trAoutput.put(Place.ACS, 1);
        trAoutput.put(Place.CS, 1);

        // Transition trLetBC connections.
        Map<Place, Integer> trletBCinput = new HashMap<>();
        trletBCinput.put(Place.ACS, 1);
        trletBCinput.put(Place.CS, 1);
        Map<Place, Integer> trletBCoutput = new HashMap<>();
        trletBCoutput.put(Place.S, 1);
        trletBCoutput.put(Place.LBC, 1);

        // Transition trLBCaux connections.
        Map<Place ,Integer>

        // Transition trAcheckB connections.
        Map<Place, Integer> trAcheckBinput = Collections.singletonMap(Place.LBC, 1);
        Map<Place, Integer> trAcheckBoutput = Collections.singletonMap(Place.B, 1);
        Collection<Place> trAcheckBinhibitor = Collections.singletonList(Place.B);

        // Transition trAcheckC connections.
        Map<Place, Integer> trAcheckCinput = Collections.singletonMap(Place.LBC, 1);
        Map<Place, Integer> trAcheckCoutput = Collections.singletonMap(Place.C, 1);
        Collection<Place> trAcheckCinhibitor = Collections.singletonList(Place.C);
        /********************************************************************/

        /***************************Thread B*********************************/
        // Transition trB connections.
        Map<Place, Integer> trBinput = new HashMap<>();
        trBinput.put(Place.B, 1);
        trBinput.put(Place.S, 1);
        Map<Place, Integer> trBoutput = new HashMap<>();
        trBoutput.put(Place.BCS, 1);
        trBoutput.put(Place.CS, 1);

        // Transition trLetAC connections.
        Map<Place, Integer> trletACinput = new HashMap<>();
        trletACinput.put(Place.BCS, 1);
        trletACinput.put(Place.CS, 1);
        Map<Place, Integer> trletACoutput = new HashMap<>();
        trletACoutput.put(Place.S, 1);
        trletACoutput.put(Place.LAC, 1);

        // Transition trBcheckA connections.
        Map<Place, Integer> trBcheckAinput = Collections.singletonMap(Place.LAC, 1);
        Map<Place, Integer> trBcheckAoutput = Collections.singletonMap(Place.A, 1);
        Collection<Place> trBcheckAinhibitor = Collections.singletonList(Place.A);

        // Transition trBcheckC connections.
        Map<Place, Integer> trBcheckCinput = Collections.singletonMap(Place.LAC, 1);
        Map<Place, Integer> trBcheckCoutput = Collections.singletonMap(Place.C, 1);
        Collection<Place> trBcheckCinhibitor = Collections.singletonList(Place.C);
        /********************************************************************/

        /***************************Thread C*********************************/
        // Transition trC connections.
        Map<Place, Integer> trCinput = new HashMap<>();
        trCinput.put(Place.C, 1);
        trCinput.put(Place.S, 1);
        Map<Place, Integer> trCoutput = new HashMap<>();
        trCoutput.put(Place.CCS, 1);
        trCoutput.put(Place.CS, 1);

        // Transition trLetAB connections.
        Map<Place, Integer> trletABinput = new HashMap<>();
        trletABinput.put(Place.CCS, 1);
        trletACinput.put(Place.CS, 1);
        Map<Place, Integer> trletABoutput = new HashMap<>();
        trletABoutput.put(Place.S, 1);
        trletABoutput.put(Place.LAB, 1);

        // Transition trCcheckA connections.
        Map<Place, Integer> trCcheckAinput = Collections.singletonMap(Place.LAB, 1);
        Map<Place, Integer> trCcheckAoutput = Collections.singletonMap(Place.A, 1);
        Collection<Place> trCcheckAinhibitor = Collections.singletonList(Place.A);

        // Transition trCcheckB connections.
        Map<Place, Integer> trCcheckBinput = Collections.singletonMap(Place.LAB, 1);
        Map<Place, Integer> trCcheckBoutput = Collections.singletonMap(Place.B, 1);
        Collection<Place> trCcheckBinhibitor = Collections.singletonList(Place.B);
        /********************************************************************/

        Transition<Place> trA = new Transition<>(trAinput, null, null, trAoutput);
        Transition<Place> trLetBC = new Transition<>(trletBCinput, null, null, trletBCoutput);
        Transition<Place> trAcheckB = new Transition<>(trAcheckBinput, null, trAcheckBinhibitor, trAcheckBoutput);
        Transition<Place> trAcheckC = new Transition<>(trAcheckCinput, null, trAcheckCinhibitor, trAcheckCoutput);

        Transition<Place> trB = new Transition<>(trBinput, null, null, trBoutput);
        Transition<Place> trLetAC = new Transition<>(trletACinput, null, null, trletACoutput);
        Transition<Place> trBcheckA = new Transition<>(trBcheckAinput, null, trBcheckAinhibitor, trBcheckAoutput);
        Transition<Place> trBcheckC = new Transition<>(trBcheckCinput, null, trBcheckCinhibitor, trBcheckCoutput);

        Transition<Place> trC = new Transition<>(trCinput, null, null, trCoutput);
        Transition<Place> trLetAB = new Transition<>(trletABinput, null, null, trletABoutput);
        Transition<Place> trCcheckA = new Transition<>(trCcheckAinput, null, trCcheckAinhibitor, trCcheckAoutput);
        Transition<Place> trCcheckB = new Transition<>(trCcheckBinput, null, trCcheckBinhibitor, trCcheckBoutput);

        Collection<Transition<Place>> threadAtransitions = new HashSet<>(Arrays.asList(trA, trLetBC, trAcheckB, trAcheckC));
        Collection<Transition<Place>> threadBtransitions = new HashSet<>(Arrays.asList(trB, trLetAC, trBcheckA, trBcheckC));
        Collection<Transition<Place>> threadCtransitions = new HashSet<>(Arrays.asList(trC, trLetAB, trCcheckA, trCcheckB));

        Collection<Transition<Place>> allTransitions = new HashSet<>(Arrays.asList(trA, trB, trC, trLetBC, trLetAC, trLetAB, trAcheckB, trAcheckC, trBcheckA, trBcheckC, trCcheckA, trCcheckB));
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
