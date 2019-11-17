package multiplicator;

import petrinet.PetriNet;
import petrinet.Transition;

import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Main {

    private static class FireThread extends Thread {
        private PetriNet<Place> net;
        private Collection<Transition<Place>> transitions;
        private Integer id;

        FireThread(PetriNet<Place> net, Collection<Transition<Place>> transitions, Integer id) {
            this.net = net;
            this.transitions = transitions;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    this.net.fire(this.transitions);
                    // Count won't be incremented if in transitions none of them is enabled.
                    countFired.getAndIncrement(this.id);
                    // After firing waiting 1 second allows others to enter critical section and fire.
                    // This helps to have normal distribution between 4 threads to equally fire.
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(Thread.currentThread().getName() + " has fired: " + countFired.get(this.id));
            }

        }
    }

    private static Scanner in = new Scanner(System.in);
    private static final Integer nrThreads = 4;
    /**
     * Keeps the count of fired transitions for each thread.
     */
    private static AtomicIntegerArray countFired = new AtomicIntegerArray(nrThreads);

    /**
     * A, B is the places with A and B tokens.
     * In Aux multiplication will be stored.
     * After firing the last transition the result will be in RES.
     * Link to Visualization:
     */
    private enum Place {
        A, B, AUX, RES
    }

    private static <T> void putPositive(Map<T, Integer> map, T key, Integer value) {
        if (value > 0) {
            map.put(key, value);
        }
    }

    private static Map<Place, Integer> marking(int a, int b) {
        Map<Place, Integer> result = new HashMap<>();

        putPositive(result, Place.A, a);
        putPositive(result, Place.B, b);
        putPositive(result, Place.AUX, 0);
        putPositive(result, Place.RES, 0);

        return result;
    }

    public static void main(String[] args) {
        int A = in.nextInt();
        int B = in.nextInt();

        Map<Place, Integer> initialMarking = marking(A, B);
        PetriNet<Place> multiplicatorNet = new PetriNet<>(initialMarking, true);

        Map<Place, Integer> firstTrInput = new HashMap<>();
        firstTrInput.put(Place.B, 1);
        firstTrInput.put(Place.A, A);
        Map<Place, Integer> firstTrOutput = new HashMap<>();
        firstTrOutput.put(Place.A, A);
        firstTrOutput.put(Place.AUX, A);

        Map<Place, Integer> mainTrInput = Collections.singletonMap(Place.AUX, A * B);
        Map<Place, Integer> mainTrOutput = Collections.singletonMap(Place.RES, A * B);

        // The solution contains only two transitions to ensure the multiplication of A and B.
        Transition<Place> firstTr = new Transition<>(firstTrInput, null, null, firstTrOutput);
        Transition<Place> mainTr = new Transition<>(mainTrInput, null, null, mainTrOutput);

        List<Thread> threads = new ArrayList<>(nrThreads);
        for (int i = 0; i < nrThreads; i++) {
            threads.add(new FireThread(multiplicatorNet, Collections.singleton(firstTr), i));
        }

        for (Thread t : threads)
            t.start();

        // If one of the numbers is 0 then none of the transitions will be fired.
        if (A == 0 || B == 0) {
            // Print tokens in RES place which is 0.
            System.out.println(0);

            for (Thread t : threads)
                t.interrupt();

            return;
        }

        try {
            // When mainTr is enabled then in AUX place there A*B tokens.
            multiplicatorNet.fire(Collections.singleton(mainTr));
            System.out.println(initialMarking.get(Place.RES));

            // Other 4 threads are finished if mainTr is enabled.
            for (Thread t : threads)
                t.interrupt();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
