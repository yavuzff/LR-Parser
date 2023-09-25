package parser;

import java.util.HashSet;
import java.util.Objects;

class State {
    HashSet<Item> items;
    static int total=0;
    int id;
    State (HashSet<Item> items) {
        this.id = total;
        total ++;
        this.items = items;
    }

    @Override
    public String toString() {
        return "State{" + id + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Objects.equals(items, state.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }


}
