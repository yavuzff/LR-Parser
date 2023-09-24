package main;
import lexer.*;
import test.*;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws InvalidCharacterException, InvalidNumberException {
        LexerTest test = new LexerTest();
        test.runTests();
        Lexer lexer = new Lexer();
        Queue<Token> res = lexer.lex("1+1");
    }
}

// convert infix/prefix/postfix to purely postfix notation
// use stack based approach to pop and evaluate easily
// leafs are operands, branches formed by operators