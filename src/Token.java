public class Token {
    final TokenType type;   //Represents the type of the token (i.e. var, equal, so on)
    final String lexeme;    //Represents the string of characters that was scanned
    final Object literal;   //Represents the literal type contained within the lexeme

    final int lineNumber;   //Line number it was scanned at

    public Token(TokenType type, String lexeme, Object literal, int lineNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.lineNumber = lineNumber;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal + " " + lineNumber;
    }

}
