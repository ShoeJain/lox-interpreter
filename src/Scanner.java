import java.util.ArrayList;
import java.io.IOException;

public class Scanner {
    public final String source;
    public final ArrayList<Token> tokenList = new ArrayList<>();

    private int start;
    private int current;
    private char currChar;
    private int currLineNum;

    private boolean fileEnd = false;

    public Scanner(String source) {
        this.source = source;
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
        char currChar = advanceCurrent();
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
                }
                else { //Just a SLASH
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
                }
                else { //Else, add string token
                    addToken(TokenType.STRING, new String(source.substring(start + 1, current++))); //current++ consumes the closing "
                }
                break;
            case '\n':
                currLineNum++;
            case '\r': //Handle carriage return (CR) ASCII 13 for windows written Lox files
            case '\s':
                break;
            default:
                System.err.println(
                        "Error character \'" + currChar + "\' (ASCII: " + (int) currChar + ") at line " + currLineNum);
        }
    }
    
    private char advanceCurrent() {
        return source.charAt(current++);
    }

    private char peekAhead() {
        if (isEndOfFile())
            return '\0';
        return source.charAt(current);
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
