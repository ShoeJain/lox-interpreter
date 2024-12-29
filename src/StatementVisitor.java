public interface StatementVisitor<T> {
    T visitExpressionStatement(Statement.ExpressionStmt statement);

    T visitPrintStatement(Statement.Print statement);
}
