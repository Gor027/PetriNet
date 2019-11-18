package arcs;

import java.util.Map;

public class InhibitorArc<T> extends Arc<T> {

    public InhibitorArc(T place, Integer weight) {
        super(place, weight);
    }

    @Override
    public boolean canFire(Map<T, Integer> places) {
//         If place is in the map then its value is not 0.
//        return !places.containsKey(this.place);
        return !places.containsKey(this.place);
    }

    @Override
    public void fire(Map<T, Integer> places) {
//         Nothing with inhibitor arc when firing transition.
    }
}
