package arcs;

import java.util.Map;

public class OutputArc<T> extends Arc<T> {

    public OutputArc(T place, Integer weight) {
        super(place, weight);
    }

    @Override
    public boolean canFire(Map<T, Integer> places) {
        return true;
    }

    @Override
    public void fire(Map<T, Integer> places) {
//         Add as many tokens as weight of the output arc.
//         Merge adds the key value pair in the map if absent.
//         If exists the pair then updates the value.
        places.merge(this.place, this.weight, Integer::sum);
    }
}
