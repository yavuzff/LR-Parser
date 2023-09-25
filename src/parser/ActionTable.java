package parser;
import lexer.SymbolName;
import lexer.Token;

import java.util.HashMap;

class ActionTable extends Table{

    private HashMap<State, HashMap<SymbolName, Action>> table = new HashMap<>();

    Action read(State s, SymbolName a){
        if (!table.get(s).containsKey(a)){
            throw new RuntimeException("ERROR ACTION");
        }
        return table.get(s).get(a);
    }

    void add(State s, SymbolName terminal, Action a){
        if (!table.containsKey(s)) table.put(s, new HashMap<>());
        if (table.get(s).containsKey(terminal)) throw new RuntimeException("Grammar is not SLR(1)");
        table.get(s).put(terminal, a);
    }
    @Override
    public String toString() {
        return "ActionTable{"+ table + "} ";
    }
}
