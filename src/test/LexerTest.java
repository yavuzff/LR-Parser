package test;
import lexer.*;

import java.util.Queue;

public class LexerTest {
    public void runTests() {
        run("-2*3+-1!+cos5", "[-2, MUL, 3, ADD, -1, FCT, ADD, COS, 5]");
        run("1+2-3+a", "InvalidCharacterException");
        run("1+2-3++", "InvalidCharacterException");
        run("1+.-3", "InvalidNumberException");
        run("1+.2-1.", "[1, ADD, .2, SUB, 1.]");
        run(".1+.2E2cos1E1", "[.1, ADD, .2E2, COS, 1E1]");
        run(".1+.2E2co1E1", "InvalidCharacterException");
        run(".3E53.2*3", "[.3E53, .2, MUL, 3]"); //parser must handle this
        run("", "[]");
        run("2", "[2]");
        run("coscos4.!!", "[COS, COS, 4., FCT, FCT]");
        run("cossin4.!!", "InvalidCharacterException");
        run(".", "InvalidNumberException");
        run("a", "InvalidCharacterException");
        run("1E3E4", "InvalidCharacterException");
        run("1E3+!2E4", "[1E3, ADD, FCT, 2E4]");
    }

    void run(String expression, String expected_output){
        Lexer lexer = new Lexer(expression);
        System.out.println("Testing: " + expression);
        try{
            Queue<Token> res = lexer.lex();
            System.out.println("Result: " + res);
            assert !expected_output.equals("InvalidCharacterException");
            assert !expected_output.equals("InvalidNumberException");
            assert res.toString().equals(expected_output);
            System.out.println("PASSED ");
        }catch(InvalidCharacterException e){
            System.out.println("Result: InvalidCharacterException: "+e.getMessage());
            assert expected_output.equals("InvalidCharacterException");
            System.out.println("PASSED");
        }catch(InvalidNumberException e){
            System.out.println("Result: InvalidNumberException: "+e.getMessage());
            assert expected_output.equals("InvalidNumberException");
            System.out.println("PASSED");
        }
    }
}
