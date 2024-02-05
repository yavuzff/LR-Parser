# LR-parser

This is a lexer + SLR(1) parser for mathematical expressions. You can define your own operators/grammar or use the default options.
The Lexer class is used to convert a string expression to a list of tokens. This is passed to the Parser class which returns a parse tree. The parse tree can be evaluated to return the result of the expression.
For sample usage, see `Main.main`. Below is an example output using the default grammar, lexer and evaluation settings.

```
Expression: 5.1+cos1 *(.2- 1.)
Tokens: [5.1, ADD, COS, 1, MUL, OPENP, .2, SUB, 1., CLOSEP, EOF]
Parse Tree: 
E0
├── E0
│   └── E1
│       └── E2
│           └── E3
│               └── E4
│                   └── E5
│                       └── NUM 5.1
├── ADD
└── E1
    └── E2
        ├── E3
        │   ├── COS
        │   └── E3
        │       └── E4
        │           └── E5
        │               └── NUM 1
        ├── MUL
        └── E2
            └── E3
                └── E4
                    └── E5
                        ├── OPENP
                        ├── E0
                        │   └── E1
                        │       ├── E1
                        │       │   └── E2
                        │       │       └── E3
                        │       │           └── E4
                        │       │               └── E5
                        │       │                   └── NUM .2
                        │       ├── SUB
                        │       └── E2
                        │           └── E3
                        │               └── E4
                        │                   └── E5
                        │                       └── NUM 1.
                        └── CLOSEP

Evaluation result: 4.667758155305488
```



### Making your own grammar (see `Grammar.getDefaultGrammar` for example):
- Add every terminal and nonterminal in your grammar to lexer.SymbolName enum.
- Initialise a `Grammar` object.
- `addTerminal` for each terminal in your grammar.
- `addNonterminal` for each nonterminal in your grammar.
- `setStart` to define the start symbol of your grammar.
- `addProduction` for each production rule in your grammar.


### Adding more operators (e.g. `sin`):
- Add an entry for it in the SymbolName enum (e.g. `SIN`)
- Put the expected string in operators variable passed to the lexer, (e.g. `operators.put("sin", SymbolName.SIN)`)
- If the operator is post-fix (i.e. no symbols expected afterward), put it in post_fix tokens variable passed to the lexer (e.g. `postfix_tokens.add(SymbolName.CLOSEP)`)
- Ensure the grammar you supply to the parser includes the correct logic for this operator.
- Complete the evaluation logic for the operator in the evaluate() method of ParseTreeNode.
