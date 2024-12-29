public class LoxInterpreter implements ExpressionVisitor<Object> {

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
            case MINUS:
                return (double) op1 - (double) op2;
            case SLASH:
                return (double) op1 / (double) op2;
            case STAR:
                return (double) op1 / (double) op2;
            case EQUAL_EQUAL:
                if (op1 == null && op2 == null)
                    return true; //If both are null - we need to convert Lox's nil to Java's null to successfully interpret
                if (op1 == null || op2 == null)
                    return false; //If only 1 of them is null
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 == (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) == 0 ? true : false;
            case NOT_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 != (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) != 0 ? true : false;
            case GREATER_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 >= (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) >= 0 ? true : false;
            case LESSER_EQUAL:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 <= (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) <= 0 ? true : false;
            case GREATER:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 > (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) > 0 ? true : false;
            case LESSER:
                if (op1 instanceof Double && op2 instanceof Double)
                    return (double) op1 < (double) op2;
                if (op1 instanceof String && op2 instanceof String)
                    return ((String) op1).compareTo((String) op2) < 0 ? true : false;
            default:
                return null;
        }
    }

    @Override
    public Object visitUnary(Expression.Unary unaryExp) {
        Object expressionResult = evaluate(unaryExp.expr);

        switch (unaryExp.operator.type) {
            case MINUS:
                if (expressionResult instanceof Double) {
                    return -1 * (double) expressionResult;
                }
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

    private Object evaluate(Expression expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(Object expression) {
        if (expression == null)
            return false;
        if (expression instanceof Boolean)
            return (boolean) expression;
        return true;
    }

}
