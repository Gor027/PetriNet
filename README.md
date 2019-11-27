# PetriNet: Concurrent Programming

A Petri net, also known as a place/transition (PT) net, is one of several mathematical modeling languages for the description of distributed systems.
It is a class of discrete event dynamic system. A Petri net is a directed bipartite graph, in which the nodes represent transitions and places.
The directed arcs describe which places are pre- and/or postconditions for which transitions (signified by arrows).
Arcs run from a place to a transition or vice versa, never between places or between transitions. The places from which an arc runs to a transition are called the input places of the transition;
the places to which arcs run from a transition are called the output places of the transition.
Graphically, places in a Petri net may contain a discrete number of marks called tokens. Any distribution of tokens over the places will represent a configuration of the net called a marking.

There are 4 types of arcs:
-Input arcs: when transition is fired it takes from places as many tokens as the arc between transition and a place has a weight.
-Output arcs: when transition is fired it gives to places as many tokens as the arc between transition and a place has a weight.
-Reset arcs: when transition is fired it consumes all tokens of all places connected to that transition.
-Inhibitor arcs: it is used only to check if transition is enabled or no.

A transition is enabled when:
-For all places that are connected to that transition through input arcs, the number of tokens are bigger than or equal to the weight of the arc between them.
-For all places that are connected to that transition through inhibitor arcs, the number of tokens is 0.

![](file:///home/Gor027/MIM/PW/PetriNet/220px-Animated_Petri_net_commons.gif)


The task is about for a given PetriNet, enable multiple threads to fire transitions on the net with synchronization,
such that two threads cannot make changes on the PetriNet at the same time.
Function reachable(PetriNet petriNet) enables to check for a given PetriNet, all possible states(markings) of the places(Reachability set).
For synchronization monitores and semaphores are used. Additionally, to prevent illegal modifications in some cases, concurrent collections are used.
