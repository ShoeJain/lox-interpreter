# Lox Interpreter

An interpreter written in Java for modified-Lox (the programming language specified in Robert Nystrom's [Crafting Interpreters](https://craftinginterpreters.com/) book. I followed the book for a handful of chapters before branching out and making my own additions to the grammar/implementation.

I made this because I took a Compilers class at university and walked away feeling like I didn't really gain an intuitive understanding of some aspects of compiler/interpreter design, and the practical considerations to make when _designing_ the semantics of a language and defining compiler behavior. I would highly recommend the [Crafting Interpreters](https://craftinginterpreters.com/) book to anyone who's interested in this.

## Modified Lox

This interpreter implements the following production rules for the syntax:

```python
# Statements. A program is a list of statements. Statements do not return anything (i.e. they are pure logic)
    program             → statement* EOF ;
    statementBlock      → "{" statement+ "}" ;
    statement           → assignStmt | printStmt | varDecl | ifStmt | funcStmt | statementBlock ;
    ifStmt              → "if" "(" expression ")" statement  ("else" statement)? ;
    funcStmt            → "func" IDENTIFIER "(" params ")" statementBlock ; 
    exprStmt            → expression ";" ;
    assignStmt          → (assignment | call) ";" ;
    printStmt           → "print" expression ";" ;
    varDecl             → "var" IDENTIFIER ("=" expression)? ";"

# Expressions. A statement is a sequence of expressions. Expressions always return some value (i.e. they are data and logic)
    expression          → comma | assignment ;
    assignment          → IDENTIFIER "=" expression ;   
    comma               → ternary (, ternary)* ;
    ternary             → logic_or (? equality : logic_or)* ;
    logic_or            → logic_and ("or" logic_and)* ;
    logic_and           → equality ("and" equality)*;
    equality            → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison          → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term                → factor ( ( "-" | "+" ) factor )* ;
    factor              → unary ( ( "/" | "*" ) unary )* ;
    unary               → ( "!" | "-" ) unary | call ;
    call                → primary ( "(" ternary? ")" )*
    primary             → IDENTIFIER | NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;

# Miscellaneous. To simplify and modularize common grammar rules that are neither Statements nor Expressions
    params              → (IDENTIFIER (, IDENTIFIER)*)? ;
```

Almost everything works as you'd normally expect it to. Functions are considered first class citizens, and my version also doesn't allow you to use undefined variables - the original Lox was written to partly be usable via an interactive prompt, which I'm not interested in doing.

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
