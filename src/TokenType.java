public enum TokenType {
    //Single character
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, STAR, SLASH,
    SEMICOLON,
                            
    //One or two characters
    NOT, NOT_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, LESSER, GREATER_EQUAL, LESSER_EQUAL,
                    
    //Literals
    IDENTIFIER, STRING, NUMBER,

    //Keywords
    AND, OR, TRUE, FALSE,
    FOR, WHILE, IF, ELSE, RETURN, FUN,
    PRINT, SUPER, THIS,
    CLASS, NIL, VAR,
                    
    EOF,
}
