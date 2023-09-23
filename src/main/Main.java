package main;
import lexer.*;
import test.*;

public class Main {
    public static void main(String[] args) throws InvalidCharacterException, InvalidNumberException {
        LexerTest test = new LexerTest();
        test.runTests();
    }
}

// convert infix/prefix/postfix to purely postfix notation
// use stack based approach to pop and evaluate easily
// leafs are operands, branches formed by operators