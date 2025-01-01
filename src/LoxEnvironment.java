import java.util.Map;
import java.util.HashMap;

public class LoxEnvironment {
    private final Map<String, Object> variables = new HashMap<>();
    LoxEnvironment outerScope;

    LoxEnvironment(LoxEnvironment outer) {
        outerScope = outer;
    }

    void define(Token varName, Object value) {
        variables.put(varName.lexeme, value);
    }

    void assign(Token varName, Object value) {
        if (variables.containsKey(varName.lexeme)) {
            variables.put(varName.lexeme, value);
            return;
        }

        if (outerScope != null) {
            outerScope.assign(varName, value);
            return;
        }
            
        throw new LoxError.RuntimeError(varName, "Attempt to assign to undefined variable \'" + varName.lexeme + "\'");    
    }

    Object get(Token varName) {
        if (variables.containsKey(varName.lexeme))
            return variables.get(varName.lexeme);
        
        if (outerScope != null)
            return outerScope.get(varName);

        throw new LoxError.RuntimeError(varName, "Attempt to access undefined variable \'" + varName.lexeme + "\'");
    }
    
    boolean exists(Token varName) {
        return variables.containsKey(varName.lexeme);
    }
}
