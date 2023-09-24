package lexer;

public class Num extends Token{
    String value;

    public Num(SymbolName name, String value){
        super(name);
        this.value = value;
    }

    public String getValue(){return this.value;}

    @Override
    public String toString() {
        return value;
    }
}
