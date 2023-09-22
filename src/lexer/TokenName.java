package lexer;

public enum TokenName {
    ADD,    // + operator
    SUB,    // - operator
    MULT,   // * operator
    FACT,   // ! operator
    COS,    // cos function
    NUM     // Floats of the form 1.4 or .2 or 4. or +5.45E+19 where E is an exponent
}
