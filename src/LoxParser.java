import java.util.ArrayList;
import java.util.List;

/*
    program        → statementBlock* EOF ;
    statementBlock → "{" statementBlock+ "}" | statement ;  //Note: Current auto errors if empty block
    statement      → exprStmt | printStmt | varDecl;
    exprStmt       → expression ";" ;
    printStmt      → "print" expression ";" ;
    varDecl        → "var" IDENTIFIER ("=" expression)? ";"

    expression     → assignment ;
    assignment     → IDENTIFIER "=" assignment | comma ;    // assignment is self recursive to support syntax like a = b = c = some_expression;
    comma          → ternary (, ternary)* ;
    ternary        → equality (? equality : equality)* ;
    equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term           → factor ( ( "-" | "+" ) factor )* ;
    factor         → unary ( ( "/" | "*" ) unary )* ;
    unary          → ( "!" | "-" ) unary
                | primary ;
    primary        → IDENTIFIER | NUMBER | STRING | "true" | "false" | "nil"
                | "(" expression ")" ;
 */
public class LoxParser {
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
                case FUN:
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

    private Statement statementBlock() {    //statementBlock → "{" statementBlock+ "}" | statement ;
        if (matchesOne(TokenType.LEFT_BRACE)) {
            Token openingBrace = tokens.get(current++);   //Saving this for easy debug
            List<Statement> statements = new ArrayList<>();
            while (!matchesOne(TokenType.RIGHT_BRACE) && !isAtEnd()) {
                statements.add(statementBlock());
            }
            requireToken(TokenType.RIGHT_BRACE, "Unterminated block - block start at ln " + openingBrace.lineNumber);
            return new Statement.Block(statements);
        }

        return statement();
    }

    private Statement statement() {         //statement        exprStmt | printStmt | varDecl;
        Statement stmt;
        switch (tokens.get(current).type) {
            case PRINT:
                stmt = printStatement();
                break;
            case VAR:
                stmt = varDeclStatement();
                break;
            default:
                stmt = expressionStatement();
                break;
        }
        return stmt;
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

    private Expression expression() { //expression     → assignment ;
        return assignment();
    }

    private Expression assignment() { //assignment       IDENTIFIER "=" assignment | comma ;
        Expression lValue = comma(); //evaluate everything to the left of the = first

        if (matchesOne(TokenType.EQUAL)) { //If the parser returns a Variable (which will auto consume the identifier)
            //proceed with assignment logic
            Token equals = tokens.get(current++);
            if (lValue instanceof Expression.Variable) {
                Expression value = assignment();

                return new Expression.Assignment(((Expression.Variable) lValue).varName, value);
            }

            throw new LoxError.ParserError(equals, "Can't assign to a non-variable");
        }

        return lValue;
    }

    private Expression comma() { //comma            ternary (, comma)* ????????;
        Expression expr = ternary();

        while (matchesOne(TokenType.COMMA)) {
            current++;
            expr = ternary();
        }

        return expr;
    }

    private Expression ternary() { //ternary        → equality (? equality : equality)* ;
        Expression expr = equality();

        while (matchesOne(TokenType.QUESTION)) {
            Token question = tokens.get(current++);
            Expression trueCase = equality();
            requireToken(TokenType.COLON, "Missing ':' in ternary condition");
            Token colon = tokens.get(current - 1);
            Expression falseCase = equality();
            Expression decisions = new Expression.Binary(trueCase, colon, falseCase);
            expr = new Expression.Binary(expr, question, decisions);
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

    private Expression unary() { //unary          → (( "!" | "-" ) unary) | primary;
        if (matchesOne(TokenType.NOT, TokenType.MINUS)) {
            Token operator = tokens.get(current++);
            return new Expression.Unary(operator, unary());
        } else { //primary
            return primary();
        }
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
                throw new LoxError.ParserError(tokens.get(current), "Expected an expression"); //If we fall through the tree and don't find an expression beginner token
        }
    }
}
