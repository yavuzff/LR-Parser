package lexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Lexer {
    String expression;
    HashMap<String, SymbolName> operators;
    HashSet<SymbolName> postfix_tokens;
    Automaton automaton;
    public Lexer (HashMap<String, SymbolName> operators, HashSet<SymbolName> postfix_tokens){
        this.operators = operators;
        this.postfix_tokens = postfix_tokens;
        initialise();
    }

    public Lexer (){ //default setting
        operators = new HashMap<>();
        operators.put("+", SymbolName.ADD);
        operators.put("-", SymbolName.SUB);
        operators.put("*", SymbolName.MUL);
        operators.put("!", SymbolName.FCT);
        operators.put("cos", SymbolName.COS);
        operators.put("(", SymbolName.OPENP);
        operators.put(")", SymbolName.CLOSEP);
        postfix_tokens = new HashSet<>();
        postfix_tokens.add(SymbolName.FCT);
        postfix_tokens.add(SymbolName.NUM);
        postfix_tokens.add(SymbolName.CLOSEP);
        initialise();
    }

    private void initialise(){
        automaton = new Automaton(operators);
        automaton.construct();
    }

    public Queue<Token> lex(String expr) throws InvalidCharacterException, InvalidNumberException {
        expression = expr;
        Queue<Token> res = new LinkedList<>();

        Token last_token = null;
        int start = 0;

        while (start < expression.length()){
            char peek = expression.charAt(start);
            if (peek == ' ') {start ++; continue;}
            if ((peek == '+' || peek == '-') && last_token != null && postfix_tokens.contains(last_token.name)){
                if (peek == '+') last_token = new Token(SymbolName.ADD);
                else {last_token = new Token(SymbolName.SUB);}
                res.add(last_token);
                start ++;
            }
            else{
                AutomatonNode node = automaton.start;
                int index = start;
                while (index < expression.length() && node.children.containsKey(expression.charAt(index))){
                    node = node.children.get(expression.charAt(index));
                    index ++;
                }

                if (node.end_token.isEmpty()) throw new InvalidCharacterException(String.format("Invalid character at index %d", index));
                else if (node.end_token.get() == SymbolName.NUM){
                    String num = expression.substring(start, index);
                    Boolean invalid = true;
                    for(int i=0; i<num.length();i++){
                        if (Character.isDigit(num.charAt(i))){
                            invalid = false;
                            break;
                        }
                        else if (num.charAt(i) == 'E') break;
                    }
                    if (invalid) throw new InvalidNumberException(String.format("Invalid number '%s' at index %d", num, start));
                    else {last_token = new Num(SymbolName.NUM, num.replaceAll("\\s",""));}

                }
                else {last_token = new Token(node.end_token.get());}

                res.add(last_token);
                start = index;

            }
        }
        res.add(new Token(SymbolName.EOF));
        return res;
    }
}
