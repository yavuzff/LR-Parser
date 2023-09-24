package test;
import lexer.*;

import java.util.Queue;

public class LexerTest {
    public void runTests() {
        // Valid tests
        run("-2*3+-1!+cos5", "[-2, MUL, 3, ADD, -1, FCT, ADD, COS, 5, EOF]");
        run("1+.2-1.", "[1, ADD, .2, SUB, 1., EOF]");
        run(".1+.2E2cos1E1", "[.1, ADD, .2E2, COS, 1E1, EOF]");
        run(".3E53.2*3", "[.3E53, .2, MUL, 3, EOF]"); //parser must handle this
        run("", "[EOF]");
        run("2", "[2, EOF]");
        run("coscos4.!!", "[COS, COS, 4., FCT, FCT, EOF]");
        run("1E3+!2E4", "[1E3, ADD, FCT, 2E4, EOF]");
        //InvalidCharacterException tests
        run("1+2-3+a", "InvalidCharacterException");
        run("1+2-3++", "InvalidCharacterException");
        run(".1+.2E2co1E1", "InvalidCharacterException");
        run("cossin4.!!", "InvalidCharacterException");
        run("a", "InvalidCharacterException");
        run("1E3E4", "InvalidCharacterException");

        //InvalidNumberException tests
        run("1+.-3", "InvalidNumberException");
        run(".", "InvalidNumberException");
        run("4!*cos3-+.E-2", "InvalidNumberException");

        //multiple unary operator tests
        run("+++1*4", "[+++1, MUL, 4, EOF]");
        run("+++*4", "InvalidCharacterException");
        run("+++1E3+2!*--+-2E4", "[+++1E3, ADD, 2, FCT, MUL, --+-2E4, EOF]");

        //Spacing tests
        run("  3 +  10  * cos 5", "[3, ADD, 10, MUL, COS, 5, EOF]");
        run("  3 +  10  * co s 5", "InvalidCharacterException");
        run("  3 + + - + - 10  * cos 5  ! ", "[3, ADD, +-+-10, MUL, COS, 5, FCT, EOF]");
    }

    void run(String expression, String expected_output){
        Lexer lexer = new Lexer();
        System.out.println("Testing: " + expression);
        try{
            Queue<Token> res = lexer.lex(expression);
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
