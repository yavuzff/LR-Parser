package simpletranslator;
import java.io.*;
import java.util.ArrayList;

public class SimpleParser {
    private int lookahead;
    private StringBuilder builder;
    private String tokens;

    public SimpleParser(String s){
        tokens = s;
        lookahead = 0;
        builder = new StringBuilder(tokens.length());
    }

    public String parse() throws SyntaxError{

        if (tokens.length() > 0) expr();
        return builder.toString();

    }

    private void expr() throws SyntaxError{
        term();
        while (lookahead < tokens.length()){

            if (tokens.charAt(lookahead) == '+'){
                match('+');
                term();
                builder.append('+');
            }
            else if (tokens.charAt(lookahead) == '-'){
                match('-');
                term();
                builder.append('-');
            }
            else{
                throw new SyntaxError("Syntax Error: Invalid character!");
            }
        }
    }

    private void term() throws SyntaxError{
        if (Character.isDigit(tokens.charAt(lookahead))){
            builder.append(tokens.charAt(lookahead));
            match(tokens.charAt(lookahead));
        }
        else throw new SyntaxError("Syntax Error!");
    }

    private void match(int t) throws SyntaxError{
        if (tokens.charAt(lookahead) == t) lookahead ++;
        else throw new SyntaxError("Syntax Error!");
    }
}
