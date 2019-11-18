package arcs;

import java.util.Map;

public class InputArc<T> extends Arc<T> {

    public InputArc(T place, Integer weight) {
        super(place, weight);
    }

    @Override
    public boolean canFire(Map<T, Integer> places) {
        if (places.get(this.place) == null) {
            return false;
        }

        Integer tokens = (places.get(this.place) == null ? 0 : places.get(this.place));
        return tokens >= this.weight;
    }

    @Override
    public void fire(Map<T, Integer> places) {
//           Remove from place as many tokens as weight of input arc.
        if ((places.get(this.place) - this.weight) > 0)
            places.put(this.place, places.get(this.place) - this.weight);
        else if ((places.get(this.place) - this.weight) == 0) {
            places.put(this.place, 0);
            places.remove(this.place);
        }

    }
}
