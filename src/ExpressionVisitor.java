public interface ExpressionVisitor<T> {
    T visitBinary(Expression.Binary binaryExp);

    T visitUnary(Expression.Unary literalExp);

    T visitGrouping(Expression.Grouping groupingExp);

    T visitLiteral(Expression.Literal literalExp);
}


