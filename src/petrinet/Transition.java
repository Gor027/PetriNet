package petrinet;

import arcs.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Transition<T> {

    private List<Arc<T>> incoming = new ArrayList<>();
    private List<Arc<T>> outgoing = new ArrayList<>();
    private List<Arc<T>> inReset = new ArrayList<>();
    private List<Arc<T>> inInhibitor = new ArrayList<>();

    public Transition(Map<T, Integer> input, Collection<T> reset, Collection<T> inhibitor, Map<T, Integer> output) {
        addInputArcs(input);
        addResetArcs(reset);
        addInhibitorArcs(inhibitor);
        addOutputArcs(output);
    }

    private void addInputArcs(Map<T, Integer> input) {
        if (input == null || input.isEmpty())
            return;

        for (Map.Entry<T, Integer> params : input.entrySet()) {
//             params.getValue() returns the weight of the input arc.
            Arc<T> arc = new InputArc<>(params.getKey(), params.getValue());
            this.incoming.add(arc);
        }
    }

    private void addResetArcs(Collection<T> reset) {
        if (reset == null || reset.isEmpty())
            return;

        for (T params : reset) {
            Arc<T> arc = new ResetArc<>(params, 0);
            this.inReset.add(arc);
        }
    }

    private void addInhibitorArcs(Collection<T> inhibitor) {
        if (inhibitor == null || inhibitor.isEmpty())
            return;

        for (T params : inhibitor) {
            Arc<T> arc = new InhibitorArc<>(params, 0);
            this.inInhibitor.add(arc);
        }
    }

    private void addOutputArcs(Map<T, Integer> output) {
        if (output == null || output.isEmpty())
            return;

        for (Map.Entry<T, Integer> params : output.entrySet()) {
//             params.getValue() returns the weight of the output arc.
            Arc<T> arc = new OutputArc<>(params.getKey(), params.getValue());
            this.outgoing.add(arc);
        }
    }

    boolean isEnabled(Map<T, Integer> places) {
        if (!isConnected())
            return false;

        boolean enabled = true;

        if (!this.incoming.isEmpty())
            for (Arc<T> arc : this.incoming)
                enabled = enabled && arc.canFire(places);

        if (!this.inInhibitor.isEmpty())
            for (Arc<T> arc : this.inInhibitor)
                enabled = enabled && arc.canFire(places);

        return enabled;
    }

    void fire(Map<T, Integer> places) {
        if (!this.incoming.isEmpty())
            for (Arc<T> arc : this.incoming)
                arc.fire(places);

        if (!this.inReset.isEmpty())
            for (Arc<T> arc : this.inReset)
                arc.fire(places);

        if (!this.outgoing.isEmpty())
            for (Arc<T> arc : this.outgoing)
                arc.fire(places);
    }

    private boolean isConnected() {
        return !(this.incoming.isEmpty() && this.outgoing.isEmpty()
                && this.inReset.isEmpty() && this.inInhibitor.isEmpty());
    }
}
