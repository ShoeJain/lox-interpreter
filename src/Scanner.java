import java.util.ArrayList;
import java.io.IOException;
import java.io.StringReader;

public class Scanner {
    public final String source;
    public final ArrayList<Token> tokenList = new ArrayList<>();

    private StringReader reader;
    private char currChar;
    private int currLineNum;

    public Scanner(String source) {
        this.source = source;
    }

    public void scanTokens() {
        reader = new StringReader(source);
        int readValue;
        currLineNum = 1;

        try {
            while ((readValue = reader.read()) != -1) {
                currChar = (char) readValue;
                switch (currChar) {
                    case '{':
                        addToken(TokenType.LEFT_BRACE, currChar);
                        break;
                    case '}':
                        addToken(TokenType.RIGHT_BRACE, currChar);
                        break;
                    case '(':
                        addToken(TokenType.LEFT_PAREN, currChar);
                        break;
                    case ')':
                        addToken(TokenType.RIGHT_PAREN, currChar);
                        break;
                    case ',':
                        addToken(TokenType.COMMA, currChar);
                        break;
                    case '.':
                        addToken(TokenType.DOT, currChar);
                        break;
                    case '-':
                        addToken(TokenType.MINUS, currChar);
                        break;
                    case '+':
                        addToken(TokenType.PLUS, currChar);
                        break;
                    case '*':
                        addToken(TokenType.STAR, currChar);
                        break;
                    case ';':
                        addToken(TokenType.SEMICOLON, currChar);
                        break;
                    case '\n':
                        currLineNum++;
                    case '\r':  //Handle carriage return (CR) ASCII 13 for windows written Lox files
                    case '\s':
                        continue;
                    default:
                        System.out.println("Error character: " + (int) currChar);
                }
            }
        } catch (IOException e) {
            System.out.println("Encountered IOException in Scanner: \n" + e.getMessage());
        }

        //Add EOF token
        ++currLineNum;
        addToken(TokenType.EOF, "", null);
    }

    public void addToken(TokenType token, char lexeme) {
        addToken(token, String.valueOf(lexeme), "null");
    }

    public void addToken(TokenType token, String lexeme) {
        addToken(token, lexeme, "null");
    }
    public void addToken(TokenType token, String lexeme, Object literal) {
        tokenList.add(new Token(token, lexeme, literal, currLineNum));
    }
    
    public void printTokens() {
        for (Token t : tokenList) {
            System.out.println(t.toString());
        }
    }
}
