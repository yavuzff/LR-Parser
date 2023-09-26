package test;

import lexer.*;
import parser.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;


public class ParserTest {

        public static void main(String[] args) throws InvalidCharacterException, InvalidNumberException, InvalidGrammarException, InvalidSyntaxException {

            Grammar grammar0 = Grammar.getDefaultGrammar();
            Grammar grammar1 = getGrammar1();

            Grammar grammar = grammar0;
            Parser parser = new Parser(grammar);

            run(parser, "1+1*9.9!", "4.1464265445104544");
            run(parser, "1+4.3*.2+2.-81!!", "0.8599999999999999");
            run(parser, "1+4.3*.2+2.-coscos81!!", "3.3113038663969028");
            run(parser, "cos1+4.3*.2+2.-coscos8*1!!", "2.410868774797641");
            run(parser, "cos-+-++1+---4.3*+.2+2.-coscos8*1!!", "0.6908687747976414");
            run(parser, "cos+1+-4.3*+.2+2.-coscos8*1!!", "0.6908687747976414");
            run(parser, "5*25.2-3+4.3-.2*.9", "127.12");
            run(parser, "5", "5");
            run(parser, "5--+3", "8");

            run(parser, "5*25.2-3+!4.3-.2*.9", "InvalidSyntaxException");
            run(parser, "5*25.2-3+*4.3-.2*.9", "InvalidSyntaxException");
            //run(parser, "", -); // empty input returns null tree
            run(parser, "5..3", "InvalidSyntaxException"); // [5., .3] tokens
            run(parser, "5.!.3", "InvalidSyntaxException");
            run(parser, "5.+.3*", "InvalidSyntaxException");
            run(parser, "5.+.3*1cos", "InvalidSyntaxException");
            run(parser, "cos", "InvalidSyntaxException");
            run(parser, "cos2cos", "InvalidSyntaxException");
            run(parser, "5--+3-", "InvalidSyntaxException");

            //Parentheses tests
            run(parser, "1+(4.3)*.2+2.-81!!", "0.8599999999999999");
            run(parser, "1+(((4.3)))*.2+2.-81!!", "0.8599999999999999");
            run(parser, "1+((4.3) - 10*3) + (10.1-1) *.2+2.-81", "-101.88");

            run(parser, "1+((4.3) - 10)*3) + (10.1-1) *.2+2.-81", "InvalidSyntaxException");
            run(parser, "1+((4.3) - 10)*3) + (10.1-1) *.2+)2.-81", "InvalidSyntaxException");
            run(parser, "1+(4.3)*.2+2.-81!!-()", "InvalidSyntaxException");

        }

        static void run(Parser parser, String expression, String expected_output) throws InvalidCharacterException, InvalidNumberException {
            double EPSILON = Math.pow(10,-15);
            System.out.println("Testing: " + expression);
            Lexer lexer = new Lexer();
            Queue<Token> tokens = lexer.lex(expression);

            try{
                ParseTreeNode root = parser.parse(tokens);
                System.out.println("Parse tree: ");
                System.out.println(root);

                Double res = root.evaluate();
                System.out.println("Result: " + res);
                assert !expected_output.equals("InvalidSyntaxException");
                assert Math.abs(Double.parseDouble(expected_output) - res)  <  EPSILON;
            }catch (InvalidSyntaxException e){
                System.out.println("Result: InvalidSyntaxException: "+e.getMessage());
                assert expected_output.equals("InvalidSyntaxException");
            }
            System.out.println("PASSED");
        }

        static Grammar getGrammar1(){
            Grammar grammar = new Grammar();
            grammar.addTerminal(SymbolName.OPENP);
            grammar.addTerminal(SymbolName.CLOSEP);
            grammar.addTerminal(SymbolName.ADD);
            grammar.addTerminal(SymbolName.NUM);
            grammar.addTerminal(SymbolName.MUL);

            grammar.addNonterminal(SymbolName.E0);
            grammar.addNonterminal(SymbolName.E1);
            grammar.addNonterminal(SymbolName.E2);

            grammar.addRule(new Production(SymbolName.E0, new ArrayList<>(Arrays.asList(SymbolName.E0, SymbolName.ADD, SymbolName.E1))));
            grammar.addRule(new Production(SymbolName.E0, new ArrayList<>(Arrays.asList(SymbolName.E1))));
            grammar.addRule(new Production(SymbolName.E1, new ArrayList<>(Arrays.asList(SymbolName.E1, SymbolName.MUL, SymbolName.E2))));
            grammar.addRule(new Production(SymbolName.E1, new ArrayList<>(Arrays.asList(SymbolName.E2))));
            grammar.addRule(new Production(SymbolName.E2, new ArrayList<>(Arrays.asList(SymbolName.OPENP, SymbolName.E0, SymbolName.CLOSEP))));
            grammar.addRule(new Production(SymbolName.E2, new ArrayList<>(Arrays.asList(SymbolName.NUM))));

            grammar.setStart(SymbolName.E0);

            // states
            // {[Item{dot=0}, Item{dot=0}, Item{dot=2}]=State{0}, [Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=2}]=State{1}, [Item{dot=1}, Item{dot=1}]=State{2}, [Item{dot=1}]=State{3}, [Item{dot=1}, Item{dot=3}]=State{4}, [Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=1}]=State{5}, [Item{dot=1}]=State{6}, [Item{dot=3}]=State{7}, [Item{dot=1}, Item{dot=2}]=State{8}, [Item{dot=3}]=State{9}, [Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}]=State{10}, [Item{dot=1}, Item{dot=1}]=State{11}}
            // {[Item{dot=0}, Item{dot=0}, Item{dot=2}]=State{8}, [Item{dot=1}, Item{dot=1}]=State{4}, [Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=2}]=State{6}, [Item{dot=1}]=State{5}, [Item{dot=1}, Item{dot=3}]=State{10}, [Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}]=State{0}, [Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=0}, Item{dot=1}]=State{3}, [Item{dot=1}]=State{1}, [Item{dot=3}]=State{9}, [Item{dot=1}, Item{dot=2}]=State{7}, [Item{dot=3}]=State{11}, [Item{dot=1}, Item{dot=1}]=State{2}}
            //GoTo table
            //GoToTable{{State{0}={E2=State{8}}, State{2}={E1=State{4}, E2=State{3}}, State{6}={E1=State{11}, E0=State{9}, E2=State{3}}, State{5}={E1=State{11}, E0=State{1}, E2=State{3}}}}
            //GoToTable{{State{8}={E2=State{9}}, State{6}={E1=State{10}, E2=State{5}}, State{3}={E1=State{2}, E0=State{7}, E2=State{5}}, State{0}={E1=State{2}, E0=State{4}, E2=State{5}}}}
            //ActionTable
            //ActionTable{{State{11}={EOF=Reduce{Production{E0-->[E1]}} , CLOSEP=Reduce{Production{E0-->[E1]}} , MUL=Shift{State{0}} , ADD=Reduce{Production{E0-->[E1]}} }, State{1}={EOF=Accept{} , ADD=Shift{State{2}} }, State{0}={NUM=Shift{State{7}} , OPENP=Shift{State{6}} }, State{4}={MUL=Shift{State{0}} , EOF=Reduce{Production{E0-->[E0, ADD, E1]}} , CLOSEP=Reduce{Production{E0-->[E0, ADD, E1]}} , ADD=Reduce{Production{E0-->[E0, ADD, E1]}} }, State{2}={NUM=Shift{State{7}} , OPENP=Shift{State{6}} }, State{6}={NUM=Shift{State{7}} , OPENP=Shift{State{6}} }, State{3}={EOF=Reduce{Production{E1-->[E2]}} , MUL=Reduce{Production{E1-->[E2]}} , CLOSEP=Reduce{Production{E1-->[E2]}} , ADD=Reduce{Production{E1-->[E2]}} }, State{5}={NUM=Shift{State{7}} , OPENP=Shift{State{6}} }, State{7}={EOF=Reduce{Production{E2-->[NUM]}} , MUL=Reduce{Production{E2-->[NUM]}} , CLOSEP=Reduce{Production{E2-->[NUM]}} , ADD=Reduce{Production{E2-->[NUM]}} }, State{8}={EOF=Reduce{Production{E1-->[E1, MUL, E2]}} , MUL=Reduce{Production{E1-->[E1, MUL, E2]}} , CLOSEP=Reduce{Production{E1-->[E1, MUL, E2]}} , ADD=Reduce{Production{E1-->[E1, MUL, E2]}} }, State{10}={EOF=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} , MUL=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} , CLOSEP=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} , ADD=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} }, State{9}={CLOSEP=Shift{State{10}} , ADD=Shift{State{2}} }}}
            //ActionTable{{State{2}={EOF=Reduce{Production{E0-->[E1]}} , CLOSEP=Reduce{Production{E0-->[E1]}} , MUL=Shift{State{8}} , ADD=Reduce{Production{E0-->[E1]}} }, State{4}={EOF=Accept{} , ADD=Shift{State{6}} }, State{8}={NUM=Shift{State{1}} , OPENP=Shift{State{3}} }, State{10}={MUL=Shift{State{8}} , EOF=Reduce{Production{E0-->[E0, ADD, E1]}} , CLOSEP=Reduce{Production{E0-->[E0, ADD, E1]}} , ADD=Reduce{Production{E0-->[E0, ADD, E1]}} }, State{6}={NUM=Shift{State{1}} , OPENP=Shift{State{3}} }, State{3}={NUM=Shift{State{1}} , OPENP=Shift{State{3}} }, State{5}={EOF=Reduce{Production{E1-->[E2]}} , MUL=Reduce{Production{E1-->[E2]}} , CLOSEP=Reduce{Production{E1-->[E2]}} , ADD=Reduce{Production{E1-->[E2]}} }, State{0}={NUM=Shift{State{1}} , OPENP=Shift{State{3}} }, State{1}={EOF=Reduce{Production{E2-->[NUM]}} , MUL=Reduce{Production{E2-->[NUM]}} , CLOSEP=Reduce{Production{E2-->[NUM]}} , ADD=Reduce{Production{E2-->[NUM]}} }, State{9}={EOF=Reduce{Production{E1-->[E1, MUL, E2]}} , MUL=Reduce{Production{E1-->[E1, MUL, E2]}} , CLOSEP=Reduce{Production{E1-->[E1, MUL, E2]}} , ADD=Reduce{Production{E1-->[E1, MUL, E2]}} }, State{11}={EOF=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} , MUL=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} , CLOSEP=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} , ADD=Reduce{Production{E2-->[OPENP, E0, CLOSEP]}} }, State{7}={CLOSEP=Shift{State{11}} , ADD=Shift{State{6}} }}}
            return grammar;
        }
}
