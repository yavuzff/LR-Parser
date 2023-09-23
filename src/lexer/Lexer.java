package lexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Lexer {
    String expression;
    HashMap<String, TokenName> operators;
    HashSet<TokenName> postfix_tokens;
    public Lexer (String expression, HashMap<String, TokenName> operators, HashSet<TokenName> postfix_tokens){
        this.expression = expression;
        this.operators = operators;
        this.postfix_tokens = postfix_tokens;
    }

    public Lexer (String expression){
        this.expression = expression;
        operators = new HashMap<>();
        operators.put("+", TokenName.ADD);
        operators.put("-", TokenName.SUB);
        operators.put("*", TokenName.MUL);
        operators.put("!", TokenName.FCT);
        operators.put("cos", TokenName.COS);
        postfix_tokens = new HashSet<>();
        postfix_tokens.add(TokenName.FCT);
        postfix_tokens.add(TokenName.NUM);
    }

    public Queue<Token> lex() throws InvalidCharacterException, InvalidNumberException {
        Queue<Token> res = new LinkedList<>();

        Automaton automaton = new Automaton(operators);
        automaton.construct();
        Token last_token = null;
        int start = 0;

        while (start < expression.length()){
            char peek = expression.charAt(start);
            if (peek == ' ') {start ++; continue;}
            if ((peek == '+' || peek == '-') && last_token != null && postfix_tokens.contains(last_token.name)){
                if (peek == '+') last_token = new Token(TokenName.ADD);
                else {last_token = new Token(TokenName.SUB);}
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
                else if (node.end_token.get() == TokenName.NUM){
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
                    else {last_token = new Num(TokenName.NUM, num.replaceAll("\\s",""));}

                }
                else {last_token = new Token(node.end_token.get());}

                res.add(last_token);
                start = index;

            }
        }


        return res;
    }
}
