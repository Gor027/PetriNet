package arcs;

import java.util.Map;

public class ResetArc<T> extends Arc<T> {

    public ResetArc(T place, Integer weight) {
        super(place, weight);
    }

    @Override
    public boolean canFire(Map<T, Integer> places) {
        return true;
    }

    @Override
    public void fire(Map<T, Integer> places) {
//         Remove all tokens from place.
        places.put(this.place, 0);
        places.remove(this.place);
    }
}
