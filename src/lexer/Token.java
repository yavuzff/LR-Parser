package lexer;


public class Token {
    TokenName name;

    public Token(TokenName name) {
        this.name = name;
    }

    public TokenName getName(){return name;}

    @Override
    public String toString() {
        return name.toString();
    }
}