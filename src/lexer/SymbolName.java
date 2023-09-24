package lexer;
public enum SymbolName {
    //terminals/tokens
    ADD,    // + operator
    SUB,    // - operator
    MUL,   // * operator
    FCT,   // ! operator
    COS,    // cos function
    NUM,     // Floats of the form 1.4 or .2 or 4. or +5.45E+19 where E is an exponent
    EOF,

    //non-terminals
    S,      //special start non-terminal
    E0,
    E1,
    E2,
    E3,
    E4
}
