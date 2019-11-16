package petrinet;

import java.util.*;

public class PetriNet<T> {
    private Map<T, Integer> initialMarking;
    private boolean fair;

    // Keeping the thread locks in order.
    private List<Object> locksList = new ArrayList<>();

    //     Map of locks for threads and transitions lists.
    private LockMap map = new LockMap();

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        this.initialMarking = initial;
        this.fair = fair;
    }

    public Map<T, Integer> getInitialMarking() {
        return initialMarking;
    }

    /**
     * Used in method reachable to find all enabled transitions to fire.
     *
     * @param transitions - collection of transitions
     * @param marking     - the actual marking of the places.
     * @return collection of enabled transitions, empty if none found.
     */
    private Collection<Transition<T>> getAllEnabled(Collection<Transition<T>> transitions, Map<T, Integer> marking) {
        Collection<Transition<T>> enabledList = new ArrayList<>();

        for (Transition<T> t : transitions)
            if (t.isEnabled(marking))
                enabledList.add(t);

        return enabledList;
    }

    private void reachAll(Collection<Transition<T>> transitions, Map<T, Integer> marking, Set<Map<T, Integer>> result) {
        Collection<Transition<T>> toFireList = getAllEnabled(transitions, marking);

        if (toFireList.isEmpty()) {
            return;
        }

        Collection<Map<T, Integer>> resultedMarkings = new ArrayList<>();

        for (Transition<T> t : toFireList) {
            Map<T, Integer> copiedMarking = new HashMap<>(marking);
//             Changes the copiedMarking
            t.fire(copiedMarking);

            if (!result.contains(copiedMarking)) {
                resultedMarkings.add(copiedMarking);
                result.add(copiedMarking);
            }
        }

        if (!resultedMarkings.isEmpty())
            for (Map<T, Integer> m : resultedMarkings)
                reachAll(transitions, m, result);
    }

    public Set<Map<T, Integer>> reachable(Collection<Transition<T>> transitions) {
//         Copy locally the state of the net marking and the transitions.
        Map<T, Integer> copiedMarking = new HashMap<>(initialMarking);
        Collection<Transition<T>> copiedTransitions = new ArrayList<>(transitions);

        Set<Map<T, Integer>> result = new HashSet<>();
//        Collection<Transition<T>> toFire = getAllEnabled(copiedTransitions, copiedMarking);

//        if (toFire.isEmpty()) {
//            result.add(copiedMarking);
//            return result;
//        }

        result.add(copiedMarking);

//         Reaches all possible markings and adds them to result.
//         Result does not contain the initial marking.
        reachAll(copiedTransitions, copiedMarking, result);

//        result.add(copiedMarking);

        return result;
    }

    private class LockMap {
        private Map<Object, Collection<Transition<T>>> locks = new HashMap<>();

        /**
         * Awake a thread for which there is an enabled transition.
         */
        synchronized void notifyPossibleLock() {
            for (Object currentLock : locksList) {
                for (Transition<T> checkEnabled : locks.get(currentLock)) {
                    if (checkEnabled.isEnabled(initialMarking)) {
                        currentLock.notify();
//                         After finishing fire, currentLock will be garbage collected.
                        locksList.remove(currentLock);
                        return;
                    }
                }
            }
        }

        /**
         * Create lock for transitions list and add to map(locks) of lock and list.
         */
        synchronized Object createLock(Collection<Transition<T>> transitions) {
            Object lock = new Object();
            locks.put(lock, transitions);

            return lock;
        }
    }

    /**
     * Return first enabled transition in the collection. If none is found,
     * return null.
     *
     * @param transitions - collection of transitions.
     * @return Reference on the first found enabled transition, otherwise null.
     */
    private Transition<T> getEnabled(Collection<Transition<T>> transitions) {
        for (Transition<T> t : transitions)
            if (t.isEnabled(initialMarking))
                return t;

        return null;
    }

//      Initially has value 0.
//    AtomicInteger countCheckers = new AtomicInteger();
//    AtomicBoolean wantFire = new AtomicBoolean(false);
//
//     Preferred over semaphores.
//    ReentrantLock lockToCheck = new ReentrantLock(true);
//    ReentrantLock lockToFire = new ReentrantLock(true);

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {
//         Thread takes the lock.

        Object lock = map.createLock(transitions);

        Transition<T> enabled;

        while ((enabled = getEnabled(transitions)) == null) {
//             Add to locks list to be awakened.
            locksList.add(lock);
//             Unlock for others and wait.
            lock.wait();
        }

//         Critical Section
        enabled.fire(initialMarking);

//      Invoke thread if possible.
        map.notifyPossibleLock();

        return enabled;
    }
}