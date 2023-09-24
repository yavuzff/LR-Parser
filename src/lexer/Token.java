package lexer;


import parser.Symbol;

public class Token extends Symbol {

    public Token(SymbolName name) { super.name = name;
    }

    public SymbolName getName(){return name;}

    @Override
    public String toString() {
        return name.toString();
    }
}