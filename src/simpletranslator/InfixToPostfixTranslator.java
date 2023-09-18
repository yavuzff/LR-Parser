package simpletranslator;

import java.io.IOException;

//converts infix expressions containing single-digit numbers, +, - to postfix
public class InfixToPostfixTranslator {
    public static void main (String[] args) throws IOException{
        System.out.println(translate(""));
    }

    public static String translate(String infix){
        SimpleParser simple = new SimpleParser(infix);
        try {
            String postfix = simple.parse();
            return postfix;
        }
        catch(SyntaxError s){
            System.out.println("Syntax ERROR!");
            return "";
        }
    }
}
