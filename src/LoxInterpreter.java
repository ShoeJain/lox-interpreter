public class LoxInterpreter implements ExpressionVisitor<Object> {

    public void interpret(Expression expr) {
        try {
            Object result = evaluate(expr);
            System.out.println(objectToString(result));
        }
        catch (RuntimeError e) {
            LoxError.printError(LoxError.Module.INTERPRETER, e.token.lineNumber, e.getMessage());
        }
    }

    private Object evaluate(Expression expr) {
        return expr.accept(this);
    }

    private String objectToString(Object obj) {
        if (obj == null)
            return "nil";
        return obj.toString();
    }

    @Override
    public Object visitBinary(Expression.Binary binaryExp) {
        Object op1 = evaluate(binaryExp.left);
        Object op2 = evaluate(binaryExp.right);

        switch (binaryExp.operator.type) {
            case PLUS:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 + (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return (String) op1 + (String) op2;
                throw new RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case MINUS:
                checkBinaryNumberOperand(binaryExp.operator, op1, op2);
                return (double) op1 - (double) op2;
            case SLASH:
                checkBinaryNumberOperand(binaryExp.operator, op1, op2);
                return (double) op1 / (double) op2;
            case STAR:
                checkBinaryNumberOperand(binaryExp.operator, op1, op2);
                return (double) op1 * (double) op2;
            case EQUAL_EQUAL:
                if (op1 == null && op2 == null)
                    return true; //If both are null - we need to convert Lox's nil to Java's null to successfully interpret
                if (op1 == null || op2 == null)
                    return false; //If only 1 of them is null
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 == (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) == 0 ? true : false;
                throw new RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case NOT_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 != (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) != 0 ? true : false;
                throw new RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case GREATER_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 >= (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) >= 0 ? true : false;
                throw new RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case LESSER_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 <= (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) <= 0 ? true : false;
                throw new RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case GREATER:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 > (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) > 0 ? true : false;
                throw new RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            case LESSER:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 < (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) < 0 ? true : false;
                throw new RuntimeError(binaryExp.operator, "Operands must both be numbers or strings");
            default:
                return null;
        }
    }

    @Override
    public Object visitUnary(Expression.Unary unaryExp) {
        Object expressionResult = evaluate(unaryExp.expr);

        switch (unaryExp.operator.type) {
            case MINUS:
                checkUnaryNumberOperand(unaryExp.operator, expressionResult);
                return -1 * (double) expressionResult;
            case NOT:
                return !isTruthy(expressionResult);
            default:
                return null;
        }
    }

    @Override
    public Object visitGrouping(Expression.Grouping groupingExp) {
        return evaluate(groupingExp.expr);
    }

    @Override
    public Object visitLiteral(Expression.Literal literalExp) {
        return literalExp.value;
    }

    private boolean isTruthy(Object expression) {
        if (expression == null)
            return false;
        if (expression instanceof Boolean)
            return (boolean) expression;
        return true;
    }

    private void checkUnaryNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double)
            return;
        throw new RuntimeError(operator, "Invalid operand type for operation");
    }

    private void checkBinaryNumberOperand(Token operator, Object operand1, Object operand2) {
        if (operand1 instanceof Double && operand2 instanceof Double)
            return;
        throw new RuntimeError(operator, "Invalid operand types for operation");
    }

}
