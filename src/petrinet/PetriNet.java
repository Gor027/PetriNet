package petrinet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class PetriNet<T> {
    private ConcurrentHashMap<T, Integer> initialMarking;
    private boolean fair;

    // Keeping the thread locks in order.
    private ConcurrentLinkedQueue<Object> locksList = new ConcurrentLinkedQueue<>();

    //     Map of locks for threads and transitions lists.
    private LockMap map = new LockMap();

    public PetriNet(Map<T, Integer> initial, boolean fair) {
        this.initialMarking = new ConcurrentHashMap<>();
        this.initialMarking.putAll(initial);
        this.fair = fair;
    }

    public synchronized Map<T, Integer> getInitialMarking() {
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
        synchronized boolean notifyPossibleLock() {
            List<Object> found = new ArrayList<>();

            for (Object currentLock : locksList) {
                for (Transition<T> checkEnabled : locks.get(currentLock)) {
                    if (checkEnabled.isEnabled(initialMarking)) {
                        synchronized (currentLock) {
                            currentLock.notify();
//                          After finishing fire, currentLock will be garbage collected.
//                          Iterator is used because in for each remove is not allowed.
                            found.add(currentLock);
                            break;
                        }
                    }
                }

                if (!found.isEmpty()) {
                    break;
                }
            }

            if (!found.isEmpty()) {
                locksList.removeAll(found);
                return true;
            }

            return false;
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

    private Semaphore entranceLock = new Semaphore(1, true);

    public Transition<T> fire(Collection<Transition<T>> transitions) throws InterruptedException {

        try {

            Object lock = map.createLock(transitions);
            Transition<T> enabled;

            synchronized (lock) {
                while ((enabled = getEnabled(transitions)) == null) {
                    if (!locksList.contains(lock)) {
                        // Add to locks list to be awakened.
                        locksList.add(lock);
                        // Unlock for others and wait.
                    }

                    lock.wait();
                }
            }

            entranceLock.acquire();
            // Critical Section
            enabled.fire(initialMarking);

            // Invoke thread if possible.
            // If possible to notify anyone who was waiting, then notifies, otherwise opens the lock for new comers.
            map.notifyPossibleLock();

            return enabled;

        } finally {
            entranceLock.release();
        }
    }
}
