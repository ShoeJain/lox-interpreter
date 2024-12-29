import java.util.List;

public class LoxParser {
    private final List<Token> tokens;
    private int current = 0;

    private static class ParserError extends RuntimeException {}

    LoxParser(List<Token> tokens) {
        this.tokens = tokens;
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
    
    private void requireToken(TokenType type, String message) {
        if (tokens.get(current).type == type)
            current++;

        throw error(tokens.get(current), message);
    }

    private ParserError error(Token token, String message) {
        LoxError.printError(LoxError.Module.PARSER, token.lineNumber, message);
        return new ParserError();
    }

    //This function attempts to advance from the current token until the beginning of the next statement is found
    //To be used when a Parser error occurs to allow the parser to continue parsing past the error
    private void synchronize() {
        current++;

        while (!isAtEnd()) {
            if (tokens.get(current - 1).type == TokenType.SEMICOLON)    //If the previous token was semicolon, we are probably at a new statement
                return;

            switch (tokens.get(current).type) {     //Else if current token is a statement beginner token, we are at a new statement
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

            current++;  //Else we are not at the next statement, so keep advancing still we find the boundary
        }
    }

    private Expression expression() {
        return equality();
    }

    private Expression equality() {
        Expression expr = comparision();

        while (matchesOne(TokenType.NOT_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token operator = tokens.get(current++);
            Expression right = comparision();

            expr = new Expression.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expression comparision() {
        Expression expr = term();

        while (matchesOne(TokenType.GREATER, TokenType.LESSER, TokenType.GREATER_EQUAL, TokenType.LESSER_EQUAL)) {
            Token operator = tokens.get(current++);
            Expression right = term();

            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }
    
    private Expression term() {
        Expression expr = factor();

        while (matchesOne(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = tokens.get(current++);
            Expression right = factor();

            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression factor() {
        Expression expr = unary();

        while (matchesOne(TokenType.SLASH, TokenType.STAR)) {
            Token operator = tokens.get(current++);
            Expression right = unary();

            expr = new Expression.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expression unary() {
        if (matchesOne(TokenType.NOT, TokenType.MINUS)) {
            Token operator = tokens.get(current++);
            return new Expression.Unary(operator, unary());
        } else { //primary
            return primary();
        }
    }
    
    private Expression primary() {
        Token currToken = tokens.get(current++);
        switch (currToken.type) {
            case NUMBER:
            case STRING:
            case NIL:   //for NIL, the literal will already be null
                return new Expression.Literal(currToken.literal);
            case TRUE:
                return new Expression.Literal(true);
            case FALSE:
                return new Expression.Literal(false);
            case LEFT_PAREN:
                Expression expr = expression();
                requireToken(TokenType.RIGHT_PAREN, "Unterminated grouping, expected ')'");
                return new Expression.Grouping(expr);
            default:
                return null;
        }
    }
}
