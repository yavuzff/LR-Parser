package parser;

import lexer.Num;
import lexer.SymbolName;
import lexer.Token;
import java.util.*;

public class Parser {
    private Grammar.AugmentedGrammar grammar;
    private ActionTable action;
    private GoToTable go_to;
    private HashMap<HashSet<Item>, State> states; //canonical collection
    private State start_state; //starting state of the parser
    private HashMap<SymbolName, HashSet<SymbolName>> follow;

    public Parser(Grammar grammar){
        if (grammar.nonterminals == null || grammar.terminals == null || grammar.rules == null || grammar.start == null){
            throw new RuntimeException("Error: Ensure grammar nonterminals, terminals, rules and start symbol is defined.");
        }
        initialise(grammar);
    }

    public Parser(){
        initialise(Grammar.getDefaultGrammar());
    }

    private void initialise(Grammar grammar){
        this.grammar = grammar.toAugmentedGrammar();
        constructFollow();
        constructCanonicalCollection();
        constructTables();
    }


    public ParseTreeNode parse(Queue<Token> tokens){ //remove tables
        Stack<State> stack = new Stack<>();
        stack.add(start_state); // starting state
        Stack<ParseTreeNode> node_stack = new Stack<>();

        Token token = tokens.remove(); // Note: every token queue ends with EOF
        if (tokens.isEmpty()){return null;}

        while (true){
            System.out.println("Iteration - Stack");
            System.out.println(stack);

            State top = stack.peek();
            Action response = action.read(top, token.getName());
            if (response instanceof Action.Shift){
                stack.push( ((Action.Shift) response).state );
                ParseTreeNode new_node;
                if (token.getName() == SymbolName.NUM) new_node = new ParseTreeNode(SymbolName.NUM, ((Num) token).getValue());
                else new_node = new ParseTreeNode(token.getName());
                node_stack.push(new_node);
                token = tokens.remove();
            } else if (response instanceof Action.Reduce){
                Production prod = ((Action.Reduce) response).production;
                // Deal with state
                for (int i=0; i<prod.body.size();i++){
                    stack.pop();
                }
                top = stack.peek();
                stack.push(go_to.read(top, prod.head));

                //Deal with building parse tree with this reduction
                ParseTreeNode new_node = new ParseTreeNode(prod.head);
                ArrayList<ParseTreeNode> children = new ArrayList<>();
                for (int i=0; i<prod.body.size();i++){
                    children.add(node_stack.pop());
                }
                Collections.reverse(children);
                new_node.setChildren(children);
                node_stack.push(new_node);
                System.out.println(prod);
            } else if (response instanceof Action.Accept){
                System.out.println("Successful parse!");
                assert node_stack.size() == 1;
                return node_stack.pop();
            } //else we get an Action Exception - syntax error
        }
    }

    private HashMap<SymbolName, HashSet<SymbolName>> constructFirst (){ // handles only grammars with no epsilon
        HashMap<SymbolName, HashSet<SymbolName>> first = new HashMap<>(); // so first is from symbol (not string) to set of terminal
        for (SymbolName s: grammar.terminals){
            first.put(s, new HashSet<>(Arrays.asList(s)));
        }
        for (SymbolName s: grammar.nonterminals){
            first.put(s, new HashSet<>());
        }
        boolean repeat = true;
        while (repeat){
            repeat = false;
            for (Production p: grammar.rules){
                for (SymbolName symbol: first.get(p.body.get(0))){
                    if (!first.get(p.head).contains(symbol)){
                        repeat = true;
                        first.get(p.head).add(symbol);
                    }
                }
            }
        }
        return first;
    }

    private void constructFollow (){
        HashMap<SymbolName, HashSet<SymbolName>> first = constructFirst();
        // follow is from nonterminal to set of terminals
        follow = new HashMap<>();
        for (SymbolName s: grammar.nonterminals){
            follow.put(s, new HashSet<>());
        }
        follow.get(grammar.start).add(SymbolName.EOF); // special end of file token is the only thing that can follow start symbol
        boolean repeat = true;
        while(repeat){
            repeat = false;
            for (Production p: grammar.rules){
                for (int i=0; i<p.body.size()-1;i++){ // assumes p.body[last_index] is not epsilon
                    for (SymbolName terminal: first.get(p.body.get(i+1))){  //for each terminal in first(next symbol)
                        SymbolName current_symbol = p.body.get(i);
                        if (grammar.nonterminals.contains(current_symbol)){
                            if (!follow.get(current_symbol).contains(terminal)){ // add it to follow(current symbol)
                                repeat = true;
                                follow.get(current_symbol).add(terminal);
                            }
                        }
                    }
                }
                // add everything in follow(p.head) to follow(p.body(last_index)), if it is a nonterminal
                SymbolName last_symbol = p.body.get(p.body.size()-1);
                if (grammar.nonterminals.contains(last_symbol)){
                    for (SymbolName terminal: follow.get(p.head)){
                        if (!follow.get(last_symbol).contains(terminal)){
                            repeat = true;
                            follow.get(last_symbol).add(terminal);
                        }
                    }
                }
            }
        }
    }

    private HashSet<Item> getClosure(HashSet<Item> items){
        HashSet<Item> closure = new HashSet<>(items);
        boolean repeat = true;
        while (repeat){
            repeat = false;
            HashSet<Item> next = new HashSet<>();
            for (Item i: closure){
                for (Production p: grammar.rules){
                    if (i.getCurrent().isPresent() && i.getCurrent().get() == p.head){
                        Item new_item = new Item(p);
                        if(!closure.contains(new_item)){
                            repeat = true;
                            next.add(new_item);
                        }
                    }
                }
            }
            closure.addAll(next);
        }
        return closure;
    }

    private HashSet<Item> goTo(HashSet<Item> items, SymbolName symbol){
        HashSet<Item> progressed = new HashSet<>();
        for (Item i: items){
            if (i.getCurrent().isPresent() && i.getCurrent().get() == symbol){
                progressed.add(new Item(i.p, i.dot + 1));
            }
        }
        return getClosure(progressed);
    }

    private void constructCanonicalCollection(){ //construct canonical collection in the form of set of states
        states = new HashMap<>();
        HashSet<Item> initial = new HashSet<>();
        initial.add(new Item(grammar.start_p));
        HashSet<Item> start_items = getClosure(initial);
        start_state = new State(start_items);
        states.put(start_items, start_state);

        boolean repeat = true;
        while (repeat){
            repeat = false;
            HashMap<HashSet<Item>, State> next = new HashMap<>();
            for (HashSet<Item> set: states.keySet()){
                for (SymbolName symbol: grammar.symbols){
                    HashSet<Item> go_to = goTo(set, symbol);
                    if (!go_to.isEmpty() && !states.containsKey(go_to)){
                        repeat = true;
                        next.put(go_to, new State(go_to));
                    }
                }
            }
            states.putAll(next);
        }
    }

    private void constructTables(){
        go_to = new GoToTable();
        for (State s: states.values()){
            for (SymbolName nonterminal: grammar.nonterminals){
                HashSet<Item> res = goTo(s.items, nonterminal);
                if (states.containsKey(res)){
                    go_to.add(s, nonterminal, states.get(res));
                }
            }
        }

        action = new ActionTable();
        for (State s: states.values()){
            for(Item i: s.items){
                if (i.getCurrent().isPresent() && grammar.terminals.contains(i.getCurrent().get())){
                    // don't recalculate go here
                    State nextState = states.get(goTo(s.items, i.getCurrent().get()));
                    action.add(s, i.getCurrent().get(), new Action.Shift(nextState));
                } else if (i.getCurrent().isEmpty()){ // dot is at the end of production so we reduce/accept
                    if (i.p.head != SymbolName.S){ // we reduce
                        for (SymbolName terminal: follow.get(i.p.head)){
                            action.add(s, terminal, new Action.Reduce(i.p));
                        }
                    } else action.add(s,SymbolName.EOF,new Action.Accept()); //we accept.
                }
            }
        }
    }

}
