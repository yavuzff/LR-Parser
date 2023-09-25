package parser;
import lexer.SymbolName;
import lexer.Token;

import java.util.*;

public class Parser {

    private Grammar.AugmentedGrammar grammar;
    private ActionTable action;
    private GoToTable go_to;
    private State start_state;
    private HashSet<Item> start_items;
    private HashMap<SymbolName, HashSet<SymbolName>> first;
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
        constructFirst();
        constructFollow();
        constructCanonicalCollection();
        constructTables();
    }


    public ParseTree parse(Queue<Token> tokens){ //remove tables
        //if (tokens.isEmpty()){return null;} // Note: every token queue ends with EOF

        System.out.println("Start parse.");

        Stack<State> stack = new Stack<>();
        stack.add(start_state); // starting state

        // also store tokens stack.
        //when popping, construct tree

        Token token = tokens.remove();
        while (true){

            System.out.println("Iteration - Stack");
            System.out.println(stack);


            State top = stack.peek();
            Action response = action.read(top, token.name);
            if (response instanceof Action.Shift){
                stack.push( ((Action.Shift) response).state );
                token = tokens.remove();
            } else if (response instanceof Action.Reduce){
                Production prod = ((Action.Reduce) response).production;
                for (int i=0; i<prod.body.size();i++){
                    stack.pop();
                }
                top = stack.peek();
                stack.push(go_to.read(top, prod.head));
                System.out.println(prod);
            } else if (response instanceof Action.Accept){
                System.out.println("Successful parse!");
                break;
            } else { throw new RuntimeException("Syntax Error!");} // error.
        }
        return new ParseTree();
    }

    private void constructFirst (){ // handles only grammars with no epsilon
        first = new HashMap<>(); // so first is from symbol (not string) to set of terminal
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
    }

    private void constructFollow (){
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

    // make this void return type?
    private HashSet<HashSet<Item>> constructCanonicalCollection(){
        HashSet<HashSet<Item>> canonical_collection = new HashSet<>();
        HashSet<Item> initial = new HashSet<>();
        initial.add(new Item(grammar.start_p));

        start_items = getClosure(initial);
        canonical_collection.add(start_items); //initial state of parser

        boolean repeat = true;
        while (repeat){
            repeat = false;
            HashSet<HashSet<Item>> next = new HashSet<>();
            for (HashSet<Item> set: canonical_collection){
                for (SymbolName symbol: grammar.symbols){
                    HashSet<Item> go_to = goTo(set, symbol);
                    if (!go_to.isEmpty() && !canonical_collection.contains(go_to)){
                        repeat = true;
                        next.add(go_to);
                    }
                }
            }
            canonical_collection.addAll(next);
        }
        return canonical_collection;
    }


    private ArrayList<Table> constructTables(){
        HashSet<HashSet<Item>> C = constructCanonicalCollection();
        //ArrayList<State> states = new ArrayList<>(C.size());
        HashMap<HashSet<Item>, State> states = new HashMap<>();

        for (HashSet<Item> items: C){
            states.put(items, new State(items));
        }

        start_state = states.get(start_items);


        // dont recalculate go here, when calculating in goTo, store in this table, then re-use that
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
                } else if (i.getCurrent().isEmpty()){// dot is at the end of production so we reduce/accept
                    if (i.p.head != SymbolName.S){ // we reduce
                        for (SymbolName terminal: follow.get(i.p.head)){
                            action.add(s, terminal, new Action.Reduce(i.p));
                        }
                    } else action.add(s,SymbolName.EOF,new Action.Accept()); //we accept.
                }
            }
        }


        System.out.println("STATES");
        System.out.println(states);

        System.out.println("GoTo table");
        System.out.println(go_to);

        System.out.println("Action table");
        System.out.println(action);


        return new ArrayList<>(Arrays.asList(action, go_to));
    }

}
