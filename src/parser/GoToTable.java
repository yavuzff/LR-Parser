package parser;

import lexer.SymbolName;
import java.util.HashMap;

class GoToTable{

    private HashMap<State, HashMap<SymbolName, State>> table = new HashMap<>();
    State read(State s, SymbolName nonterminal){
        if (!table.get(s).containsKey(nonterminal)){
            throw new RuntimeException("ERROR: NO SYMBOLNAME IN STATE GO TO TABLE");
        }
        return table.get(s).get(nonterminal);
    }

    void add(State s1, SymbolName nonterminal, State s2){
        if (!table.containsKey(s1)) table.put(s1, new HashMap<>());
        table.get(s1).put(nonterminal, s2);
    }

    @Override
    public String toString() {
        return "GoToTable{" + table + "} ";
    }
}