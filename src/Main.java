import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * TODOs:
 * Add error reporting feature
 * Add support for interactive session
 */
public class Main {
  public static void main(String[] args) {  
        /*if (args.length > 1) {
            System.err.println("Too many args, use 1 or 0");
        } else if (args.length == 1) {
            interpretFile(args[0]);
        } else
            interactivePrompt();*/

        Expression expression = new Expression.Binary(
        new Expression.Unary(
            new Token(TokenType.MINUS, "-", null, 1),
            new Expression.Literal(123)),
        new Token(TokenType.STAR, "*", null, 1),
        new Expression.Grouping(
                        new Expression.Literal(45.67)));
            
        System.out.println(new ExpressionPrinter().print(expression));
    }
  
    public static void interpretFile(String filename) {
        String fileContents = "";
        try {
            fileContents = Files.readString(Path.of(filename));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        interpret(fileContents);
    }

    public static void interactivePrompt() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while(true) {
                System.out.print("> ");
                String inputLine = reader.readLine();
                if(inputLine == null) break;
                interpret(inputLine);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void interpret(String program) {
        if (program.length() > 0) {
            Scanner tokenScanner = new Scanner(program);
            tokenScanner.scanTokens();
            tokenScanner.printTokens();
        } else {
            System.out.println("Empty program passed in, exiting...");
        }
    }
}
