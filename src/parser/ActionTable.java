package parser;

import lexer.SymbolName;
import java.util.HashMap;

class ActionTable{

    private HashMap<State, HashMap<SymbolName, Action>> table = new HashMap<>();

    Action read(State s, SymbolName a) throws InvalidSyntaxException {
        if (!table.get(s).containsKey(a)){
            throw new InvalidSyntaxException();
        }
        return table.get(s).get(a);
    }

    void add(State s, SymbolName terminal, Action a) throws InvalidGrammarException {
        if (!table.containsKey(s)) table.put(s, new HashMap<>());
        if (table.get(s).containsKey(terminal)) throw new InvalidGrammarException("Grammar is not SLR(1)");
        table.get(s).put(terminal, a);
    }
    @Override
    public String toString() {
        return "ActionTable{"+ table + "} ";
    }
}
