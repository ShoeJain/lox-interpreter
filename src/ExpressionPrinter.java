public class ExpressionPrinter implements ExpressionVisitor<String> {

    String print(Expression expr) {
        return expr.accept(this);
    }
    @Override
    public String visitBinary(Expression.Binary binaryExp) {
        return "(" + binaryExp.operator.lexeme + " " + binaryExp.left.accept(this) + " " + binaryExp.right.accept(this) + ")";
    }

    @Override
    public String visitUnary(Expression.Unary unaryExp) {
        return "(" + unaryExp.operator.lexeme + " " + unaryExp.expr.accept(this) + ")";
    }

    @Override
    public String visitGrouping(Expression.Grouping groupingExp) {
        return "(group " + groupingExp.expr.accept(this) + ")";
    }

    @Override
    public String visitLiteral(Expression.Literal literalExp) {
        if (literalExp.value == null)
            return "nil";
        return literalExp.value.toString();

    }
    @Override
    public String visitVariable(Expression.Variable variable) {
        return "(" + variable.varName.lexeme + ")";
    }
}
