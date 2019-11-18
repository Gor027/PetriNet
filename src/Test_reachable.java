import petrinet.*;
import java.util.*;
import java.util.concurrent.*;

public class Test_reachable {

    private static Transition<Integer> create_transition(Integer origin,
                                                         Integer destination) {
        Map<Integer, Integer> input = new HashMap<>();
        Map<Integer, Integer> output = new HashMap<>();

        input.put(origin, 1);
        output.put(destination, 1);
        return new Transition<>(input, new HashSet<>(), new HashSet<>(), output);
    }

    public static class Worker implements Runnable {
        private Collection<Transition<Integer>> tranzycje;
        private PetriNet<Integer> net;

        public Worker(Collection<Transition<Integer>> tranzycje, PetriNet<Integer> net) {
            this.tranzycje = tranzycje;
            this.net = net;
        }

        @Override
        public void run() {
            net.reachable(tranzycje);
        }
    }
    public static void main(String[] args) {
        HashMap<Integer, Integer> initial = new HashMap<>();
        initial.put(1, 1);
        PetriNet<Integer> net = new PetriNet<>(initial, true);

        HashMap<Integer, Integer> output_inflacyjnego = new HashMap<>();
        output_inflacyjnego.put(0, 1);
        Transition<Integer> inflacja = new Transition<>(new HashMap<>(), new HashSet<>(),
                new HashSet<>(), output_inflacyjnego);
        HashSet<Transition<Integer>> do_reachible = new HashSet<>();
        do_reachible.add(inflacja);

        Thread pomocnik = new Thread(new Worker(do_reachible, net));
        pomocnik.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }

        HashSet<Transition<Integer>> do_zwyklego = new HashSet<>();

        do_zwyklego.add(create_transition(1, 2));

        try {
            net.fire(do_zwyklego);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }

        pomocnik.interrupt();

        System.out.println("Przeszlo test");
    }
}