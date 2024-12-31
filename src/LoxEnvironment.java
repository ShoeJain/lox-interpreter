import java.util.Map;
import java.util.HashMap;

public class LoxEnvironment {
    private final Map<String, Object> variables = new HashMap<>();

    void define(String varName, Object value) {
        variables.put(varName, value);
    }

    Object get(Token varName) {
        if (variables.containsKey(varName.lexeme))
            return variables.get(varName.lexeme);
        
        throw new LoxError.RuntimeError(varName, "Attempt to access undefined variable \'" + varName.lexeme + "\'");
    }
}
