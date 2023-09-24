package lexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class Automaton { //augmented prefix tree
    AutomatonNode start;
    HashMap<String, SymbolName> operators;
    // operators is a map from operator string to operator TokenName (operator cannot be ., E or [0-9])
    Automaton(HashMap<String, SymbolName> operators){
        this.operators = operators;
        this.start = new AutomatonNode(Optional.empty());
        this.start.children.put(' ', this.start); //ignore spaces at the start of a token
    }

    AutomatonNode construct(){
        addOperators();
        addNum();
        return start;
    }

    private void addOperators(){
        // add the operators (e.g. +, -, *, !, cos) to the automaton, as if it was a prefix tree
        for (Map.Entry<String, SymbolName> entry: operators.entrySet()){
            String operator = entry.getKey();
            AutomatonNode current = start;

            for (int i=0; i<operator.length(); i++){
                char current_char = operator.charAt(i);
                if (!current.children.containsKey(current_char)){
                    AutomatonNode operator_node = new AutomatonNode();
                    current.children.put(current_char, operator_node); // add the current character as a new node in the prefix tree/automaton
                }
                current = current.children.get(current_char);
            }
            current.end_token = Optional.of(entry.getValue()); // set it as final node
        }
    }

    private void addNum(){
        // add floating point numbers to the automation
        // (+/-)* -> digit* -> . -> digit* -> E -> +/- -> digit+
        AutomatonNode whole_node = new AutomatonNode(Optional.of(SymbolName.NUM));
        AutomatonNode point_node = new AutomatonNode(Optional.of(SymbolName.NUM)); // Need to check in lexer that we dont accept '.', '-.' or '+.', or '+.E1', or '.E1', i.e. there must be a digit before E
        AutomatonNode frac_node = new AutomatonNode(Optional.of(SymbolName.NUM));
        AutomatonNode exp_node = new AutomatonNode();
        AutomatonNode exp_sign_node = new AutomatonNode();
        AutomatonNode exp_digit_node = new AutomatonNode(Optional.of(SymbolName.NUM));

        // Note: + and - could be part of a signed float, or an infix operator,
        // so we set the nodes for these as invalid, and deal with this disambiguation in the lexer itself.
        // note that we also accept +-+++ <num> as a valid float, and + -   + <num>
        AutomatonNode plus_node = start.children.get('+');
        AutomatonNode minus_node = start.children.get('-');
        plus_node.end_token = Optional.empty();
        minus_node.end_token = Optional.empty();
        plus_node.children.put('+', plus_node);
        plus_node.children.put('-', minus_node);
        minus_node.children.put('+', plus_node);
        minus_node.children.put('-', minus_node);
        plus_node.children.put(' ', plus_node);
        minus_node.children.put(' ', minus_node);

        //add edges from nodes in a valid float
        start.children.put('.',point_node);
        plus_node.children.put('.',point_node);
        minus_node.children.put('.',point_node);
        whole_node.children.put('.',point_node);
        whole_node.children.put('E', exp_node);
        point_node.children.put('E', exp_node);
        frac_node.children.put('E',exp_node);
        exp_node.children.put('+', exp_sign_node);
        exp_node.children.put('-',exp_sign_node);

        for (int i=0; i<10;i++){
            char digit = Integer.toString(i).charAt(0);
            start.children.put(digit, whole_node);
            start.children.get('+').children.put(digit,whole_node);
            start.children.get('-').children.put(digit,whole_node);
            whole_node.children.put(digit, whole_node);
            point_node.children.put(digit, frac_node);
            frac_node.children.put(digit, frac_node);
            exp_node.children.put(digit, exp_digit_node);
            exp_sign_node.children.put(digit, exp_digit_node);
            exp_digit_node.children.put(digit, exp_digit_node);
        }

    }

}
