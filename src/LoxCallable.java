import java.util.ArrayList;

public class LoxCallable {
    private final Statement.FuncStatement declaration;

    LoxCallable(Statement.FuncStatement declaration) {
        this.declaration = declaration;
    }

    public Object call(LoxInterpreter interp, ArrayList<Object> args) {
        LoxEnvironment env = new LoxEnvironment(interp.globalScope);
        if(args.size() != arity()) {
            throw new LoxError.RuntimeError(declaration.funcName, "Func being called with wrong arity!! Oh the joys of dynamic typing :)");
        }

        for (int i = 0; i < arity(); i++) {
            env.defineVar(declaration.paramList.get(i), args.get(i));
        }

        Object retVal = interp.executeBlock((Statement.Block) declaration.funcStmts, env);
        return retVal;
    }

    public int arity() {
        return declaration.paramList.size();
    }

    @Override
    public String toString() {
        String retStr = "<func " + declaration.funcName + ": ";
        for (Token tk : declaration.paramList) {
            retStr += tk.lexeme + ", ";
        }

        return retStr;
    }
}
