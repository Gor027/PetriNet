package arcs;

import java.util.Map;

public abstract class Arc<T> {
    T place;
    Integer weight;

    public abstract boolean canFire(Map<T, Integer> places);

    public abstract void fire(Map<T, Integer> places);

    Arc(T place, Integer weight) {
        this.place = place;
        this.weight = weight;
    }
}
