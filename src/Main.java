import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/*
 * TODOs:
 * Add error reporting feature
 * Add support for interactive session
 */
public class Main {
    private static LoxInterpreter interp = new LoxInterpreter();

    public static void main(String[] args) {  
        if (args.length > 1) {
            System.err.println("Too many args, use 1 or 0");
        } else if (args.length == 1) {
            interpretFile(args[0]);
        } else
            interactivePrompt();
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
            //Scan input program
            Scanner tokenScanner = new Scanner(program);
            List<Token> tokenList = tokenScanner.scanTokens();
            tokenScanner.printTokens();

            //Parse token list output by Scanner
            LoxParser parser = new LoxParser(tokenList);
            Expression singleExpression = parser.parse(); //TODO: Should this be the place to pass in tokenList?
            System.out.println(new ExpressionPrinter().print(singleExpression));

            interp.interpret(singleExpression);
        } else {
            System.out.println("Empty program passed in, exiting...");
        }
    }
}
