import java.util.Map;
import java.util.HashMap;

public class LoxEnvironment {
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, Integer> functions = new HashMap<>();
    LoxEnvironment outerScope;

    LoxEnvironment(LoxEnvironment outer) {
        outerScope = outer;
    }

    void defineVar(Token varName, Object value) {
        variables.put(varName.lexeme, value);
    }

    void assignVar(Token varName, Object value) {
        if (variables.containsKey(varName.lexeme)) {
            variables.put(varName.lexeme, value);
            return;
        }

        if (outerScope != null) {
            outerScope.assignVar(varName, value);
            return;
        }
            
        throw new LoxError.RuntimeError(varName, "Attempt to assign to undefined variable \'" + varName.lexeme + "\'");    
    }

    Object getVar(Token varName) {
        if (variables.containsKey(varName.lexeme))
            return variables.get(varName.lexeme);
        
        if (outerScope != null)
            return outerScope.getVar(varName);

        throw new LoxError.RuntimeError(varName, "Attempt to access undefined variable \'" + varName.lexeme + "\'");
    }
    
    boolean exists(Token varName) {
        return variables.containsKey(varName.lexeme);
    }
}
