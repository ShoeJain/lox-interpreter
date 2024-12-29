public class LoxError { //Meant to be a Singleton
    enum Module {
        SCANNER,
        PARSER,
        INTERPRETER,
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
