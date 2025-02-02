import java.util.ArrayList;
import java.util.List;
/*
    program             → statementBlock* EOF ;
    statementBlock      → "{" statementBlock+ "}" | statement ;  //Note: Currently auto errors if empty block
    statement           → assignStmt | printStmt | varDecl | ifStmt | funcStmt ;
    ifStmt              → "if" "(" expression ")" statementBlock  ("else" statementBlock)? ;
    funcStmt            → "func" IDENTIFIER "(" params ")" statementBlock ; 
    exprStmt            → expression ";" ;
    assignStmt          → (assignment | call) ";" ;
    printStmt           → "print" expression ";" ;
    varDecl             → "var" IDENTIFIER ("=" expression)? ";"

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
    params              → (IDENTIFIER (, IDENTIFIER)*)? ;
 */

public class LoxParser {
    //Note - statement (in the syntactic/semantic sense) cannot contain one another - but statementBlocks can
    //Expressions can contain one another (semantically, evident from the production rules above)

    private final List<Token> tokens;
    private int current = 0;

    LoxParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Statement> parseProgram() {
        List<Statement> statementList = new ArrayList<>();
        try {
            while (!isAtEnd()) {
                statementList.add(statementBlock());
            }
        } catch (LoxError.ParserError err) {
            LoxError.printError(LoxError.Module.PARSER, err.token.lineNumber, err.getMessage());
        }
        return statementList;
    }

    private boolean isAtEnd() {
        return tokens.get(current).type == TokenType.EOF;
    }

    private boolean matchesOne(TokenType... typesOfToken) {
        for (TokenType type : typesOfToken) {
            if (compareCurrent(type))
                return true;
        }
        return false;
    }

    private boolean compareCurrent(TokenType type) {
        if (isAtEnd())
            return false;
        else
            return tokens.get(current).type == type;
    }

    private Token requireToken(TokenType type, String message) {
        if (tokens.get(current).type == type) {
            current++;
            return tokens.get(current - 1);
        }

        throw new LoxError.ParserError(tokens.get(current), message);
    }

    //This function attempts to advance from the current token until the beginning of the next statement is found
    //To be used when a Parser error occurs to allow the parser to continue parsing past the error
    private void synchronize() {
        current++;

        while (!isAtEnd()) {
            if (tokens.get(current - 1).type == TokenType.SEMICOLON) //If the previous token was semicolon, we are probably at a new statement
                return;

            switch (tokens.get(current).type) { //Else if current token is a statement beginner token, we are at a new statement
                case RETURN:
                case FUNC:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                    return;
                default:
                    break;
            }

            current++; //Else we are not at the next statement, so keep advancing still we find the boundary
        }
    }

    private Statement statementBlock() { //statementBlock → "{" statementBlock+ "}" | statement ;
        List<Statement> statements = new ArrayList<>();
        if (matchesOne(TokenType.LEFT_BRACE)) {
            Token openingBrace = tokens.get(current++); //Saving this for easy debug
            statements = new ArrayList<>();
            while (!matchesOne(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                statements.add(statementBlock());
            }
            requireToken(TokenType.RIGHT_BRACE, "Unterminated block - block start at ln " + openingBrace.lineNumber);
            return new Statement.Block(statements);
        }

        //statements.add(statement());
        //return new Statement.Block(statements); 
        return statement();
    }

    private Statement statement() { //statement        exprStmt | printStmt | varDecl;
        Statement stmt;
        switch (tokens.get(current).type) {
            case PRINT:
                stmt = printStatement();
                break;
            case VAR:
                stmt = varDeclStatement();
                break;
            case IF:
                stmt = ifStatement();
                break;
            case FUNC:
                stmt = funcStatement();
                break;
            default:
                stmt = assignStatement();
                break;
        }
        return stmt;
    }
    
    /* checks for both assignments and function-calls */
    private Statement assignStatement() {   //assignStmt     → (assignment | call) ";" ;
        Expression expr = expression();
        if (expr instanceof Expression.Assignment || expr instanceof Expression.FunctionCall) {
            requireToken(TokenType.SEMICOLON, "Missing ';'");
            return new Statement.ExpressionStmt(expr);
        }
        throw new LoxError.ParserError(tokens.get(current - 1), "Expecting an assignment");
    }

    private Statement expressionStatement() { //exprStmt         expression ";" ;
        Expression expr = expression();
        requireToken(TokenType.SEMICOLON, "Missing ';'");
        return new Statement.ExpressionStmt(expr);
    }

    private Statement.Print printStatement() { //printStmt        "print" expression ";" ;
        current++; //Increment to skip the "print" that brought us here
        Expression expr = expression();
        requireToken(TokenType.SEMICOLON, "Missing ';'");
        return new Statement.Print(expr);
    }

    private Statement.VarDecl varDeclStatement() { //varDecl          "var" IDENTIFIER ("=" expression)? ";"
        current++; //Increment to skip the "var" that brought us here
        Token varToken = requireToken(TokenType.IDENTIFIER, "Missing identifier in variable declaration"); //Grab variable token
        Expression initializeValue = null;
        if (matchesOne(TokenType.EQUAL)) {
            current++;
            initializeValue = expression();
        }
        requireToken(TokenType.SEMICOLON, "Missing ';'");
        return new Statement.VarDecl(varToken, initializeValue);
    }

    private Statement.FuncStatement funcStatement() { //funcStmt            → "func" IDENTIFIER "(" params ")" statementBlock ; 
        current++;
        Token funcName = requireToken(TokenType.IDENTIFIER, "Func definition missing identifier");
        requireToken(TokenType.LEFT_PAREN, "Func missing opening paren for arg list");
        List<Token> params = params();
        requireToken(TokenType.RIGHT_PAREN, "Func missing closing paren for arg list");
        Statement funcStmts = statementBlock();

        return new Statement.FuncStatement(funcName, params, funcStmts);
    }
    
    private List<Token> params() {  //params              → (IDENTIFIER (, IDENTIFIER)*)? ;
        List<Token> paramList = new ArrayList<>();
        
        if (!matchesOne(TokenType.RIGHT_PAREN)) {
            int numParams = 0;
            do {
                if (++numParams > 255)
                    throw new LoxError.ParserError(tokens.get(current),
                            "This version of lox only supports 256 parameters for functions");
                //System.out.println("Tk: " + tokens.get(current - 1).type + " " + tokens.get(current - 1).lexeme);
                paramList.add(requireToken(TokenType.IDENTIFIER, "Func params must be valid identifiers"));
            } while (tokens.get(current++).type == TokenType.COMMA);
        }
        current--;
        return paramList;
    }

    private Statement.IfSequence ifStatement() {   //ifStmt         → "if" "(" expression ")" statementBlock  ("else" statementBlock)? ;
        current++;
        requireToken(TokenType.LEFT_PAREN, "If statement missing condition opening parenthesis");
        Expression conditional = expression();
        requireToken(TokenType.RIGHT_PAREN, "If statement missing condition closing parenthesis");
        Statement thenStmt = statementBlock();
        Statement elseStmt = null;
        if (matchesOne(TokenType.ELSE)) {
            current++;
            elseStmt = statementBlock();
        }

        return new Statement.IfSequence(conditional, thenStmt, elseStmt);
    }

    private Expression expression() { //expression     → comma | assignment ;
        Expression lValue = comma();

        if (matchesOne(TokenType.EQUAL)) {  //Check if assignment
            lValue = assignment(lValue);
        }
        return lValue;
    }

    private Expression assignment(Expression lValue) { //assignment     → IDENTIFIER "=" expression;
        Token equals = tokens.get(current++);   
        if (lValue instanceof Expression.Variable) {    //If the parser returns a Variable (which will auto consume the identifier)
            Expression value = expression();

            return new Expression.Assignment(((Expression.Variable) lValue).varName, value);
        }
        throw new LoxError.ParserError(equals, "Can't assign to a non-variable");
    }

    private Expression comma() { //comma          → ternary (, ternary)* ;
        List<Expression> listExpr = new ArrayList<>();
        listExpr.add(ternary());

        while (matchesOne(TokenType.COMMA)) {
            current++;
            listExpr.add(ternary());
        } 
        if (listExpr.size() == 1)   //If there's no comma delimited list of expressions
            return listExpr.get(0);
        else
        return new Expression.Comma(listExpr);
    }

    private Expression ternary() { //ternary        → logic_or (? logic_or : logic_or)* ;
        Expression expr = logic_or();

        while (matchesOne(TokenType.QUESTION)) {    //Test this with interp functionality
            Token question = tokens.get(current++);
            Expression trueCase = logic_or();
            requireToken(TokenType.COLON, "Missing ':' in ternary condition");
            Token colon = tokens.get(current - 1);
            Expression falseCase = logic_or();
            Expression decisions = new Expression.Binary(trueCase, colon, falseCase);
            expr = new Expression.Binary(expr, question, decisions);
        }

        return expr;
    }

    private Expression logic_or() {     //logic_or       → logic_and ("or" logic_and)* ;
        Expression expr = logic_and();

        while (matchesOne(TokenType.OR)) {
            Token operator = tokens.get(current++);
            Expression right = logic_and();

            expr = new Expression.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expression logic_and() {    //logic_and      → equality ("and" equality)*;
        Expression expr = equality();

        while (matchesOne(TokenType.AND)) {
            Token operator = tokens.get(current++);
            Expression right = equality();

            expr = new Expression.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expression equality() { //equality       → comparison ( ( "!=" | "==" ) comparison )* ;
        Expression expr = comparision();

        while (matchesOne(TokenType.NOT_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = tokens.get(current++);
            Expression right = comparision();

            expr = new Expression.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expression comparision() { //comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
        Expression expr = term();

        while (matchesOne(TokenType.GREATER, TokenType.LESSER, TokenType.GREATER_EQUAL, TokenType.LESSER_EQUAL)) {
            Token operator = tokens.get(current++);
            Expression right = term();

            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression term() { //term           → factor ( ( "-" | "+" ) factor )* ;
        Expression expr = factor();

        while (matchesOne(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = tokens.get(current++);
            Expression right = factor();

            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() { //factor         → unary ( ( "/" | "*" ) unary )* ;
        Expression expr = unary();

        while (matchesOne(TokenType.SLASH, TokenType.STAR)) {
            Token operator = tokens.get(current++);
            Expression right = unary();

            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {    //unary          → ( "!" | "-" ) unary | call ;
        if (matchesOne(TokenType.NOT, TokenType.MINUS)) {
            Token operator = tokens.get(current++);
            return new Expression.Unary(operator, unary());
        } else { //primary
            return call();
        }
    }
    
    private Expression call() {     //call           → primary ( "(" ternary? ")" )* ;
        //System.out.println("here");
        Expression primary = primary();
        if (primary instanceof Expression.Variable && matchesOne(TokenType.LEFT_PAREN)) {
            //System.out.println("here1");
            //Token leftParen = tokens.get(current);    //don't actually need to discard this...
            ArrayList<Expression> args = new ArrayList<>();
            if (!matchesOne(TokenType.RIGHT_PAREN)) {
                int num_args = 0;
                do {
                    current++;
                    if (++num_args > 255)
                        throw new LoxError.ParserError(tokens.get(current),
                                "This version of lox only supports 256 arguments to functions");
                    
                    //TODO: Using ternary() here because expecting conflicts with comma(), potentially. But this excludes assignments from being passed, which should be valid!
                    args.add(ternary());    
                } while (matchesOne(TokenType.COMMA));
            }
            requireToken(TokenType.RIGHT_PAREN, "Incomplete argument list for function");
            return new Expression.FunctionCall(((Expression.Variable) primary).varName, args);
        }
        return primary;
    }

    private Expression primary() { //primary        → NUMBER | STRING | TRUE | FALSE | NIL | "(" expression ")" 
        Token currToken = tokens.get(current++);
        switch (currToken.type) {
            case NUMBER:
            case STRING:
            case NIL: //for NIL, the literal will already be null
                return new Expression.Literal(currToken.literal);
            case TRUE:
                return new Expression.Literal(true);
            case FALSE:
                return new Expression.Literal(false);
            case LEFT_PAREN:
                Expression expr = expression();
                requireToken(TokenType.RIGHT_PAREN, "Unterminated grouping, expected ')'");
                return new Expression.Grouping(expr);
            case IDENTIFIER:
                return new Expression.Variable(currToken);
            default:
                //System.out.println(currToken.type);
                throw new LoxError.ParserError(tokens.get(current), "Expected an expression: " + currToken.type + " " + currToken.lexeme); //If we fall through the tree and don't find an expression beginner token
        }
    }
}
