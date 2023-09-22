package lexer;


import java.util.HashMap;
import java.util.Optional;

class AutomatonNode { //prefix-tree like automaton
    HashMap<Character, AutomatonNode> children = new HashMap<>();
    Optional<TokenName> end_token;

    AutomatonNode(){
        this.end_token = Optional.empty();
    }

    AutomatonNode(Optional<TokenName> end_token){
        this.end_token = end_token;
    }

}
