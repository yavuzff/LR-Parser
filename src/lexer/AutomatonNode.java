package lexer;


import java.util.HashMap;
import java.util.Optional;

class AutomatonNode { //prefix-tree like automaton
    HashMap<Character, AutomatonNode> children = new HashMap<>();
    Optional<SymbolName> end_token;

    AutomatonNode(){
        this.end_token = Optional.empty();
    }

    AutomatonNode(Optional<SymbolName> end_token){
        this.end_token = end_token;
    }

}
