public class LoxError { //Meant to be a Singleton
    enum Module {
        SCANNER,
        PARSER,
        INTERPRETER,
    }

    static class RuntimeError extends RuntimeException {
        final Token token;

        RuntimeError(Token token, String msg) {
            super(msg);
            this.token = token;
        }
    }
    
    static class ParserError extends RuntimeException{
        final Token token;

        ParserError(Token token, String msg) {
            super(msg);
            this.token = token;
        }
    }

    private static LoxError instance = new LoxError();

    private LoxError() {}

    public LoxError getInstance() {
        return instance;
    }

    public static void printError(Module mod, int lineNumber, String msg) {
        System.err.println("ERROR! " + mod + " at ln " + lineNumber + ": " + msg);
    }



}
