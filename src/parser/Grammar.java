package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import lexer.SymbolName;

public class Grammar {

    HashSet<SymbolName> nonterminals;
    HashSet<SymbolName> terminals;
    HashSet<SymbolName> symbols;
    SymbolName start;
    ArrayList<Production> rules = new ArrayList<>();

    public class AugmentedGrammar extends Grammar{
        Production start_p;
        SymbolName start_s;
        AugmentedGrammar(Grammar grammar){
            this.terminals = grammar.terminals;
            this.nonterminals = grammar.nonterminals;
            this.symbols = grammar.symbols;
            this.start = grammar.start;
            this.rules = grammar.rules;
            this.start_p = new Production(SymbolName.S, new ArrayList<>(Arrays.asList(grammar.start)));
            this.start_s = SymbolName.S;

        }
    }
    public static Grammar getDefaultGrammar(){
        Grammar grammar = new Grammar();
        grammar.addTerminal(SymbolName.ADD);
        grammar.addTerminal(SymbolName.SUB);
        grammar.addTerminal(SymbolName.MUL);
        grammar.addTerminal(SymbolName.COS);
        grammar.addTerminal(SymbolName.FCT);
        grammar.addTerminal(SymbolName.NUM);
        grammar.addTerminal(SymbolName.OPENP);
        grammar.addTerminal(SymbolName.CLOSEP);

        grammar.addNonterminal(SymbolName.E0);
        grammar.addNonterminal(SymbolName.E1);
        grammar.addNonterminal(SymbolName.E2);
        grammar.addNonterminal(SymbolName.E3);
        grammar.addNonterminal(SymbolName.E4);
        grammar.addNonterminal(SymbolName.E5);
        grammar.start = SymbolName.E0;

        //Production rules
        grammar.addRule(new Production(SymbolName.E0, new ArrayList<>(Arrays.asList(SymbolName.E0, SymbolName.ADD, SymbolName.E1))));
        grammar.addRule(new Production(SymbolName.E0, new ArrayList<>(Arrays.asList(SymbolName.E1))));
        grammar.addRule(new Production(SymbolName.E1, new ArrayList<>(Arrays.asList(SymbolName.E1, SymbolName.SUB, SymbolName.E2))));
        grammar.addRule(new Production(SymbolName.E1, new ArrayList<>(Arrays.asList(SymbolName.E2))));
        grammar.addRule(new Production(SymbolName.E2, new ArrayList<>(Arrays.asList(SymbolName.E3, SymbolName.MUL, SymbolName.E2))));
        grammar.addRule(new Production(SymbolName.E2, new ArrayList<>(Arrays.asList(SymbolName.E3))));
        grammar.addRule(new Production(SymbolName.E3, new ArrayList<>(Arrays.asList(SymbolName.COS, SymbolName.E3))));
        grammar.addRule(new Production(SymbolName.E3, new ArrayList<>(Arrays.asList(SymbolName.E4))));
        grammar.addRule(new Production(SymbolName.E4, new ArrayList<>(Arrays.asList(SymbolName.E4, SymbolName.FCT))));
        grammar.addRule(new Production(SymbolName.E4, new ArrayList<>(Arrays.asList(SymbolName.E5))));
        grammar.addRule(new Production(SymbolName.E5, new ArrayList<>(Arrays.asList(SymbolName.OPENP, SymbolName.E0, SymbolName.CLOSEP))));
        grammar.addRule(new Production(SymbolName.E5, new ArrayList<>(Arrays.asList(SymbolName.NUM))));


        return grammar;
    }
    public Grammar (){
        this.rules = new ArrayList<>();
        this.nonterminals = new HashSet<>();
        this.terminals = new HashSet<>();
        this.symbols = new HashSet<>();
    }

    public AugmentedGrammar toAugmentedGrammar(){
        return new AugmentedGrammar(this);
    }

    public void addRule(Production rule){
        rules.add(rule);
    }

    public void addNonterminal(SymbolName nonterminal){
        nonterminals.add(nonterminal);
        symbols.add(nonterminal);
    }

    public void addTerminal(SymbolName terminal){
        terminals.add(terminal);
        symbols.add(terminal);
    }

    public void setStart(SymbolName start){
        this.start = start;
    }

}
