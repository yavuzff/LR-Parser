package parser;

import lexer.SymbolName;

import java.util.Objects;
import java.util.Optional;

class Item {
    int dot;
    Production p;
    Item (Production p){
        this.p = p;
        dot = 0;
    }
    Item (Production p, int dot_index){
        this.p = p;
        dot = dot_index;
    }

    Optional<SymbolName> getCurrent(){
        if (dot < p.body.size()){
            return Optional.of(p.body.get(dot));
        } else return Optional.empty();
    }

    @Override
    public String toString() {
        return "Item{" + "dot=" + dot+'}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return dot == item.dot && Objects.equals(p, item.p);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dot, p);
    }
}
