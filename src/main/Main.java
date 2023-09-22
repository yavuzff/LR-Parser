package main;
import lexer.*;

import java.util.Queue;

public class Main {
    public static void main(String[] args) throws InvalidCharacterException, InvalidNumberException {
        Lexer lexer = new Lexer("+3!+-.E34!+3");
        Queue<Token> tokens = lexer.lex();
        System.out.println(tokens);
    }
}

// convert infix/prefix/postfix to purely postfix notation
// use stack based approach to pop and evaluate easily
// leafs are operands, branches formed by operators