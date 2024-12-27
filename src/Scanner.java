import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;

public class Scanner {
    public final String source;
    public final ArrayList<Token> tokenList = new ArrayList<>();

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("and", TokenType.AND);
        keywords.put("or", TokenType.OR);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("fun", TokenType.FUN);
        keywords.put("print", TokenType.PRINT);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("class", TokenType.CLASS);
        keywords.put("nil", TokenType.NIL);
        keywords.put("var", TokenType.VAR);
    }

    private int start;
    private int current;
    private char currChar;
    private int currLineNum;

    private boolean fileEnd = false;

    public Scanner(String in) {
        this.source = in;
        this.start = 0;
        this.current = 0;
    }

    public void scanTokens() {
        currLineNum = 1;
        while (!isEndOfFile()) {    //While we're not at the EOF
            start = current;
            scanNextToken();
        }

        //Add EOF token
        tokenList.add(new Token(TokenType.EOF, "", null, ++currLineNum));
        fileEnd = true;
    }

    public void scanNextToken() {
        currChar = advanceCurrent();
        switch (currChar) {
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '!':
                addToken(matchNextChar('=') ? TokenType.NOT_EQUAL : TokenType.NOT);
                break;
            case '>':
                addToken(matchNextChar('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '<':
                addToken(matchNextChar('=') ? TokenType.LESSER_EQUAL : TokenType.LESSER);
                break;
            case '=':
                addToken(matchNextChar('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '/':
                if (matchNextChar('/')) { //Comment
                    //Consume characters till we hit newline
                    while (peekAhead() != '\n' && !isEndOfFile())
                        ++current;
                } else { //Just a SLASH
                    addToken(TokenType.SLASH);
                }
                break;
            case '"':
                while (peekAhead() != '"' && !isEndOfFile()) {
                    if (peekAhead() == '\n')
                        currLineNum++;
                    ++current;
                }
                if (isEndOfFile()) { //If while loop terminated because file ended, throw an error
                    //Throw error
                    System.err.println("Expected \" at line " + currLineNum);
                } else { //Else, add string token
                    addToken(TokenType.STRING, new String(source.substring(start + 1, current++))); //current++ consumes the closing "
                }
                break;
            case '\n':
                currLineNum++;
            case '\r': //Handle carriage return (CR) ASCII 13 for windows written Lox files
            case '\s':
                break;
            default:
                if (isNumeric(currChar)) { //Check if number
                    //Handle number logic
                    while (isNumeric(peekAhead())) ++current;   //Don't need explicit EOF check since peek returns \0 for EOF

                    if (peekAhead() == '.' && isNumeric(peekAhead(1))) {
                        ++current;

                        while (isNumeric(peekAhead())) ++current;   //After decimal, keep looking ahead
                    }
                    addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
                    break;
                }
                if (isAlpha(currChar)) { //Check if identifier - this conditional also goes over reserved keywords
                    char peekedChar = peekAhead();
                    while ((isNumeric(peekedChar) || isAlpha(peekedChar))) {
                        ++current; //Don't need explicit EOF check since peek returns \0 for EOF
                        peekedChar = peekAhead();
                    }
    
                    addIdentifierToken();
                    break;
                }
                
                //No valid token matches found for the next character/lexeme
                System.err.println(
                        "Error character \'" + currChar + "\' (ASCII: " + (int) currChar + ") at line "
                                + currLineNum);
                break;
        }
    }
    
    private boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z') || 
                (c >= 'A' && c <= 'z') ||
                c == '_');
    }
    
    private boolean isNumeric(char c) {
        return (c >= '0' && c <= '9');
    }
    
    private char advanceCurrent() {
        return source.charAt(current++);
    }

    private char peekAhead() {
        return peekAhead(0);
    }

    private char peekAhead(int lookahead) {
        if (current + lookahead >= source.length())
            return '\0';
        return source.charAt(current + lookahead);
    }

    private boolean isEndOfFile() {
        return current >= source.length();
    }

    private boolean matchNextChar(char c) {
        if (peekAhead() == c) {
            ++current;
            return true;
        }
        return false;
    }

    private void addIdentifierToken() {
        String lexeme = source.substring(start, current);

        //Check against keywords
        TokenType type = keywords.get(lexeme);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        //Add to token list
        addToken(type, null);
    }

    public void addToken(TokenType token) {
        addToken(token, null);
    }

    public void addToken(TokenType token, Object literal) {
        //The lexeme is a substring of source, its start and end position as a substring tracked by start and current
        tokenList.add(new Token(token, source.substring(start, current), literal, currLineNum));
    }
    
    public void printTokens() {
        for (Token t : tokenList) {
            System.out.println(t.toString());
        }
    }
}
