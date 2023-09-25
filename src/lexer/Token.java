package lexer;


public class Token {

    SymbolName name;
    public Token(SymbolName name) {this.name = name;}

    public SymbolName getName(){return name;}

    @Override
    public String toString() {
        return name.toString();
    }
}