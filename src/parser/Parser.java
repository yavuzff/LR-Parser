package parser;
import lexer.Token;

import java.util.Queue;
import java.util.Stack;

public class Parser {

    Grammar grammar;
    ActionTable action;
    GoToTable go_to;
    public Parser(Grammar grammar){
        this.grammar = grammar;
        initialise();
    }

    public Parser(){
        this.grammar = Grammar.getDefaultGrammar();
        initialise();
    }

    private void initialise(){
        Grammar.AugmentedGrammar augmented_grammar = grammar.toAugmentedGrammar();
        //action = new ActionTable();
        //go_to = new GoToTable();
    }


    public ParseTree parse(Queue<Token> tokens, ActionTable action, GoToTable go_to, State start_state){ //remove tables
        //if (tokens.isEmpty()){return null;} // Note: every token queue ends with EOF

        System.out.println("Start parse.");

        Stack<State> stack = new Stack<>();
        stack.add(start_state); // starting state

        // also store tokens stack.
        //when popping, construct tree

        Token token = tokens.remove();
        while (true){

            System.out.println("Iteration - Stack");
            System.out.println(stack);


            State top = stack.peek();
            Action response = action.read(top, token.name);
            if (response instanceof Action.Shift){
                stack.push( ((Action.Shift) response).state );
                token = tokens.remove();
            } else if (response instanceof Action.Reduce){
                Production prod = ((Action.Reduce) response).production;
                for (int i=0; i<prod.body.size();i++){
                    stack.pop();
                }
                top = stack.peek();
                stack.push(go_to.read(top, prod.head));
                System.out.println(prod);
            } else if (response instanceof Action.Accept){
                System.out.println("Successful parse!");
                break;
            } else { throw new RuntimeException("Syntax Error!");} // error.
        }

        return new ParseTree();
    }

}
