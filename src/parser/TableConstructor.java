package parser;

import lexer.*;

import java.util.*;

public class TableConstructor {
    static State start_state;
    static HashSet<Item> start_items;

    public static void main(String[] args) throws InvalidCharacterException, InvalidNumberException {
        Lexer lexer = new Lexer();
        Queue<Token> tokens = lexer.lex("1+1");
        //Grammar grammar = Grammar.getDefaultGrammar();
        Grammar grammar = new Grammar();

        ArrayList<Table> tables = constructTables(grammar.toAugmentedGrammar());
        ActionTable action = (ActionTable) tables.get(0);
        GoToTable go_to = (GoToTable) tables.get(1);

        //Parser parser = new Parser(grammar);
        //parser.parse(tokens, action, go_to, start_state);
    }

     static HashMap<SymbolName, HashSet<SymbolName>> constructFirst (Grammar grammar){ // handles only grammars with no epsilon
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

    static HashMap<SymbolName, HashSet<SymbolName>> constructFollow (Grammar grammar, HashMap<SymbolName, HashSet<SymbolName>> first){
        // follow is from nonterminal to set of terminals
        HashMap<SymbolName, HashSet<SymbolName>>  follow = new HashMap<>();
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
        return follow;
    }

    static HashSet<Item> getClosure(HashSet<Item> items, Grammar grammar){
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

    static HashSet<Item> goTo(HashSet<Item> items, SymbolName symbol, Grammar grammar){
        HashSet<Item> progressed = new HashSet<>();
        for (Item i: items){
            if (i.getCurrent().isPresent() && i.getCurrent().get() == symbol){

                progressed.add(new Item(i.p, i.dot + 1));
            }
        }
        return getClosure(progressed, grammar);
    }

    // make this void return type?
    static HashSet<HashSet<Item>> constructCanonicalCollection(Grammar.AugmentedGrammar grammar){
        HashSet<HashSet<Item>> canonical_collection = new HashSet<>();
        HashSet<Item> initial = new HashSet<>();
        initial.add(new Item(grammar.start_p));

        start_items = getClosure(initial, grammar);
        canonical_collection.add(start_items); //initial state of parser

        boolean repeat = true;
        while (repeat){
            repeat = false;
            HashSet<HashSet<Item>> next = new HashSet<>();
            for (HashSet<Item> set: canonical_collection){
                for (SymbolName symbol: grammar.symbols){
                    HashSet<Item> go_to = goTo(set, symbol, grammar);
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


    static ArrayList<Table> constructTables(Grammar.AugmentedGrammar grammar){
        HashSet<HashSet<Item>> C = constructCanonicalCollection(grammar);
        //ArrayList<State> states = new ArrayList<>(C.size());
        HashMap<HashSet<Item>, State> states = new HashMap<>();

        HashMap<SymbolName, HashSet<SymbolName>> first = constructFirst(grammar);
        HashMap<SymbolName, HashSet<SymbolName>> follow = constructFollow(grammar, first);

        for (HashSet<Item> items: C){
            states.put(items, new State(items));
        }

        start_state = states.get(start_items);


        // dont recalculate go here, when calculating in goTo, store in this table, then re-use that
        GoToTable go_to = new GoToTable();

        for (State s: states.values()){
            for (SymbolName nonterminal: grammar.nonterminals){
                HashSet<Item> res = goTo(s.items, nonterminal, grammar);
                if (states.containsKey(res)){
                    go_to.add(s, nonterminal, states.get(res));
                }
            }
        }

        ActionTable action = new ActionTable();

        for (State s: states.values()){
            for(Item i: s.items){
                if (i.getCurrent().isPresent() && grammar.terminals.contains(i.getCurrent().get())){
                    // don't recalculate go here
                    State nextState = states.get(goTo(s.items, i.getCurrent().get(), grammar));
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
        System.out.println(go_to.table);

        System.out.println("Action table");
        System.out.println(action.table);


        return new ArrayList<>(Arrays.asList(action, go_to));
    }
}
