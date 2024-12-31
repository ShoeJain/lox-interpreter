public interface ExpressionVisitor<T> {
    T visitBinary(Expression.Binary binaryExp);

    T visitUnary(Expression.Unary unaryExp);

    T visitGrouping(Expression.Grouping groupingExp);

    T visitLiteral(Expression.Literal literalExp);

    T visitVariable(Expression.Variable variable);
}


