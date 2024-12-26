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
        while (current < source.length()) {    //if we've reached the end of the file
            start = current;
            scanNextToken();
        }

        //Add EOF token
        tokenList.add(new Token(TokenType.EOF, "", null, currLineNum));
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
            case '\n':
                currLineNum++;
            case '\r': //Handle carriage return (CR) ASCII 13 for windows written Lox files
            case '\s':
                break;
            default:
                System.out.println(
                        "Error character \'" + currChar + "\' (ASCII: " + (int) currChar + ") at line " + currLineNum);
        }
    }
    
    private char advanceCurrent() {
        return source.charAt(current++);
    }

    private boolean matchNextChar(char c) {
        return source.charAt(current) == c;
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
