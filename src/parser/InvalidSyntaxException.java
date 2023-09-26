package parser;

public class InvalidSyntaxException extends Exception{
        public InvalidSyntaxException(String s) {
            super(s);
        }
        public InvalidSyntaxException(){
            super();
    }
}
