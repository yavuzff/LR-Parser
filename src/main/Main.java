package main;
import lexer.*;
import parser.*;
import test.*;

import java.sql.SQLOutput;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws InvalidCharacterException, InvalidNumberException, InvalidGrammarException, InvalidSyntaxException {
        String expression = "5.1+cos1 *(.2- 1.)";
        System.out.println("Expression: " + expression);

        Lexer lexer = new Lexer();
        Grammar grammar = Grammar.getDefaultGrammar();
        Parser parser = new Parser(grammar);

        Queue<Token> tokens = lexer.lex(expression);
        System.out.println("Tokens: " + tokens);

        ParseTreeNode parse_tree = parser.parse(tokens);
        System.out.println("Parse Tree: ");
        System.out.println(parse_tree);

        Double result = parse_tree.evaluate();
        System.out.println("Evaluation result: " + result);
    }
}

// convert infix/prefix/postfix to purely postfix notation
// use stack based approach to pop and evaluate easily
// leafs are operands, branches formed by operators