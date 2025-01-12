# Lox Interpreter

An interpreter written in Java for modified-Lox (the programming language specified in Robert Nystrom's [Crafting Interpreters](https://craftinginterpreters.com/) book. I followed the book for a handful of chapters before branching out and making my own additions to the grammar/implementation.

I made this because I took a Compilers class at university and walked away feeling like I didn't really gain an intuitive understanding of some aspects of compiler/interpreter design, and the practical considerations to make when _designing_ the semantics of a language and defining compiler behavior. I would highly recommend the [Crafting Interpreters](https://craftinginterpreters.com/) book to anyone who's interested in this.

## Modified Lox

This interpreter implements the following production rules for the syntax:

```python
    program        → statementBlock* EOF ;
    statementBlock → "{" statementBlock+ "}" | statement ;  //Note: Currently auto errors if empty block
    statement      → exprStmt | printStmt | varDecl | ifStmt ;
    ifStmt         → "if" "(" expression ")" statementBlock  ("else" statementBlock)? ;
    exprStmt       → expression ";" ;
    printStmt      → "print" expression ";" ;
    varDecl        → "var" IDENTIFIER ("=" expression)? ";"

    expression     → assignment ;
    assignment     → IDENTIFIER "=" assignment | comma ;    // assignment is self recursive to support syntax like a = b = c = some_expression;
    comma          → ternary (, ternary)* ;
    ternary        → logic_or (? equality : logic_or)* ;
    logic_or       → logic_and ("or" logic_and)* ;
    logic_and      → equality ("and" equality)*;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term           → factor ( ( "-" | "+" ) factor )* ;
    factor         → unary ( ( "/" | "*" ) unary )* ;
    unary          → ( "!" | "-" ) unary
                | primary ;
    primary        → IDENTIFIER | NUMBER | STRING | "true" | "false" | "nil"
                | "(" expression ")" ;
```

## Build
Use the compile.sh script to build the interpreter

## Usage
For an interactive terminal
```
./run.sh
```

To run a .lox file (which you can find several examples of inside the tests folder)
```
./run.sh program.lox
```

## Credits
Robert Nystrom, obviously

All the test cases inside tests/ are taken from CodeCrafter's [interpreter-tester](https://github.com/codecrafters-io/interpreter-tester) repository, which is a tester for an interpreter more faithful to the book than mine. I've changed the tests wherever my implementation of the language semantics/logic diverges from the book.
